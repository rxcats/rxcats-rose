package io.github.rxcats.rose.chat.model.message;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class SendMessageRequest {

    @NotBlank
    private String message;

}
