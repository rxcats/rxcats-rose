package io.github.rxcats.rose.chat.exception;

import io.github.rxcats.rose.chat.model.message.ResultCode;

public class ServiceException extends RuntimeException {
    private static final long serialVersionUID = -5676230011052117896L;

    private ResultCode code = ResultCode.error;

    public ServiceException() {
        super();
    }

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(ResultCode code, String message) {
        super(message);
        this.code = code;
    }

    public ResultCode getCode() {
        return code;
    }

}
