package io.github.rxcats.rose.chat.ws.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Component;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface WsController {

    String prefix() default "";

    String desc() default "";

    String groupName() default "";

    int threadSize() default 10;

    int queueSize() default 50;

    int timeout() default 3000;

}
