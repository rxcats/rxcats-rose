package io.github.rxcats.rose.chat.ws;

import java.lang.reflect.Method;

import lombok.Data;

@Data
public class WsControllerEntity {

    private Object bean;

    private Method method;

    public static WsControllerEntity of(Object bean, Method method) {
        var e = new WsControllerEntity();
        e.bean = bean;
        e.method = method;
        return e;
    }

}
