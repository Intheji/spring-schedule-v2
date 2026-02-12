package com.springschedule.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 400 입력값 검증 실패
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(
            IllegalArgumentException e,
            HttpServletRequest request
    ) {
        return error(HttpStatus.BAD_REQUEST, "BAD_REQUEST", e.getMessage(), request);
    }

    // 404 일정 없음 등
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            IllegalStateException e,
            HttpServletRequest request
    ) {
        return error(HttpStatus.NOT_FOUND, "NOT_FOUND", e.getMessage(), request);
    }

    // 400 Bean Validation 실패 예외 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException e,
            HttpServletRequest request
    ) {
        String message = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(fieldError -> fieldError.getDefaultMessage())
                .orElse("요청 값이 유효하지 않음");

        return error(HttpStatus.BAD_REQUEST, "VALIDATION_FAILED", message, request);
    }

    // 400 QueryParam/pathVariable Bean Validation 실패(@Min/@Max) 처리
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(
            ConstraintViolationException e,
            HttpServletRequest request
    ) {
        String message = e.getConstraintViolations()
                .stream()
                .findFirst()
                .map(v -> v.getMessage())
                .orElse("요청 값이 유효하지 않음");

        return error(HttpStatus.BAD_REQUEST, "VALIDATION_FAILED", message, request);
    }

    // 공통 응답 중복으로 메서드로 뺌
    private ResponseEntity<ErrorResponse> error(
            HttpStatus status,
            String code,
            String message,
            HttpServletRequest request
    ) {
        ErrorResponse body = new ErrorResponse(
                status.value(),
                status.name(),
                code,
                message,
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(body);
    }
}
