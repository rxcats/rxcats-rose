package io.github.rxcats.rose.chat.model.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResultCode {

    ok(0, "Success"),
    error(900001, "Service error"),
    command_not_found(900002, "Command not found"),
    invalid_parameter(900003, "Invalid parameter"),

    ;

    public final int code;
    public final String message;

}
