package io.github.rxcats.rose.chat.model.message;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class LogoutRequest {

    @NotBlank
    private String userId;

}
