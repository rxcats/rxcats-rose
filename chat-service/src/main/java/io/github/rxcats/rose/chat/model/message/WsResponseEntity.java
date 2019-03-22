package io.github.rxcats.rose.chat.model.message;

import lombok.Data;

@Data
public class WsResponseEntity<T> {

    private String uri = "";

    private int code = ResultCode.ok.code;

    private String message = ResultCode.ok.message;

    private T result;

    private String stack;

    private long ts = System.currentTimeMillis();

    private WsResponseEntity() {

    }

    private WsResponseEntity(String uri, T result) {
        this();
        this.uri = uri;
        this.result = result;
    }

    private WsResponseEntity(ResultCode code, String uri, T result) {
        this(uri, result);
        this.code = code.code;
        this.message = code.message;
    }

    private WsResponseEntity(ResultCode code, String uri, T result, String stack) {
        this(code, uri, result);
        this.stack = stack;
    }

    public static <T> WsResponseEntity<T> of(String uri, T result) {
        return new WsResponseEntity<>(uri, result);
    }

    public static <T> WsResponseEntity<T> of(ResultCode code, String uri, T result) {
        return new WsResponseEntity<>(code, uri, result);
    }

    public static <T> WsResponseEntity<T> error(String uri, String stack) {
        return new WsResponseEntity<>(ResultCode.error, uri, null, stack);
    }

    public static <T> WsResponseEntity<T> error(ResultCode code, String uri) {
        return new WsResponseEntity<>(code, uri, null);
    }

    public static <T> WsResponseEntity<T> error(ResultCode code, String uri, String stack) {
        return new WsResponseEntity<>(code, uri, null, stack);
    }

}
