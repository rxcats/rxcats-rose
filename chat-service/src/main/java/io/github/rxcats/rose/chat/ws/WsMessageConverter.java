package io.github.rxcats.rose.chat.ws;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

import io.github.rxcats.rose.chat.ws.annotation.WsRequestBody;
import io.github.rxcats.rose.chat.ws.annotation.WsRequestHeader;
import io.github.rxcats.rose.chat.ws.annotation.WsSession;
import io.github.rxcats.rose.chat.ws.service.MessageService;

@Slf4j
@DependsOn(value = {"wsConfig"})
@Component
public class WsMessageConverter {
    private final static String FIELD_URI = "uri";
    private final static String FIELD_HEADERS = "headers";
    private final static String FIELD_BODY = "body";

    @Autowired
    Validator validator;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MessageService messageService;

    public void execute(WebSocketSession session, String payload) {

        if (StringUtils.isEmpty(payload)) {
            throw new IllegalArgumentException("payload is empty");
        }

        Map<String, Object> request;

        try {
            log.info("request: [session:{}] [payload:{}]]", session.getId(), payload);

            // @formatter:off
            request = objectMapper.readValue(payload, new TypeReference<Map<String, Object>>() {});
            // @formatter:on

            log.info("request: [session:{}] [request:{}]", session.getId(), request);
        } catch (IOException e) {
            messageService.sendErrorMessage(session, "invalid request");
            log.error("invalid request: [session:{}] [error:{}]", session.getId(), e.getMessage());
            return;
        }

        String uri = objectMapper.convertValue(request.get(FIELD_URI), String.class);

        WsControllerEntity wsController = WsControllerRegister.getWsController(uri);

        try {
            Object[] args = convertMethodParam(wsController, request, session);
            var cmd = new WsCommand(wsController.getBean(), wsController.getMethod(), args, wsController.getHystrixThreadGroup());
            Future<Object> queue = cmd.queue();
            Object result = queue.get();
            messageService.sendMessage(session, uri, result);
            log.info("response: [session:{}] [result:{}]", session.getId(), result);
        } catch (Exception e) {
            messageService.sendErrorMessage(session, uri, e.getMessage());
            log.error("response error [session:{}] [uri:{}] [error:{}]", session.getId(), uri, e.getMessage());
        }

    }

    private Object[] convertMethodParam(WsControllerEntity wsController, Map<String, Object> request, WebSocketSession session) {

        Class<?>[] parameterTypes = wsController.getMethod().getParameterTypes();
        Annotation[][] parameterAnnotations = wsController.getMethod().getParameterAnnotations();

        Object[] paramValues = new Object[parameterTypes.length];

        if (parameterTypes.length > 0) {

            for (int i = 0; i < parameterTypes.length; i++) {

                Class<?> paramType = parameterTypes[i];

                for (int j = 0; j < parameterAnnotations[i].length; j++) {

                    if (parameterAnnotations[i][j] instanceof WsRequestBody) {
                        paramValues[i] = objectMapper.convertValue(request.get(FIELD_BODY), paramType);

                        boolean useValidator = false;

                        for (int k = 0; k < parameterAnnotations[i].length; k++) {
                            if (!useValidator && parameterAnnotations[i][k] instanceof Valid) {
                                useValidator = true;
                            }
                        }

                        if (useValidator) {
                            Set<ConstraintViolation<Object>> validate = validator.validate(paramValues[i]);

                            if (!validate.isEmpty()) {
                                for (ConstraintViolation constraintViolation : validate) {
                                    log.warn("validate : {} {} {}", constraintViolation.getRootBeanClass().getSimpleName(),
                                        constraintViolation.getPropertyPath(),
                                        constraintViolation.getMessage());
                                }

                                throw new IllegalArgumentException("validation error");
                            }
                        }

                    } else if (parameterAnnotations[i][j] instanceof WsRequestHeader) {

                        // @formatter:off
                        Map<String, Object> headers = objectMapper.convertValue(request.get(FIELD_HEADERS), new TypeReference<Map<String, Object>>() {});
                        // @formatter:on

                        String key = ((WsRequestHeader) parameterAnnotations[i][j]).value();
                        paramValues[i] = objectMapper.convertValue(headers.get(key), paramType);

                    } else if (parameterAnnotations[i][j] instanceof WsSession) {
                        paramValues[i] = session;
                    }

                }
            }
        }

        return paramValues;
    }

}
