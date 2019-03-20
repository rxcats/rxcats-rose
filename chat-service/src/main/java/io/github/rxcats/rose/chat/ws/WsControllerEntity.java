package io.github.rxcats.rose.chat.ws;

import java.lang.reflect.Method;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WsControllerEntity {

    private Object bean;

    private Method method;

    private HystrixThreadGroup hystrixThreadGroup;

}
