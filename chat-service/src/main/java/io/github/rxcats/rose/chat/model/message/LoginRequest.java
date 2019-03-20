package io.github.rxcats.rose.chat.model.message;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank
    private String userId;

}
