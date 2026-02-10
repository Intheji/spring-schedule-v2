package com.springschedule.common.exception;

import lombok.Getter;

@Getter
public class ErrorResponse {

    private final int status;
    private final String message;
    private final String path;

    public ErrorResponse(int status, String message, String path) {
        this.status = status;
        this.message = message;
        this.path = path;
    }
}
