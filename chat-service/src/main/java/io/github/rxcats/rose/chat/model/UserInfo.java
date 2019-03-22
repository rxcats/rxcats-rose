package io.github.rxcats.rose.chat.model;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class UserInfo {

    protected String userId;

    protected String username;

    protected String avatar;

    protected LocalDateTime createdAt;

    protected LocalDateTime updatedAt;

}
