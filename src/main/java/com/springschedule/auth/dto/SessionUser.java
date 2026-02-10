package com.springschedule.auth.dto;

import lombok.Getter;

@Getter
public class SessionUser {

    private final Long id;
    private final String email;
    private final String userName;

    public SessionUser(Long id, String email, String userName) {
        this.id = id;
        this.email = email;
        this.userName = userName;
    }
}
