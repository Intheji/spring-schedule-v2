package com.springschedule.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 400: 잘못된 요청을 공통 에러 응답으로 반환
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(
            IllegalArgumentException e,
            HttpServletRequest request
    ) {
        return error(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", e.getMessage(), request);
    }

    // 404: 리소스가 존재하지 않는 경우 공통 에러 응답으로 반환
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            IllegalStateException e,
            HttpServletRequest request
    ) {
        return error(HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND", e.getMessage(), request);
    }

    // 400: @Valid @RequestBody 검증 실패 시에 첫 번째 필드 에러 메시지 반환
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException e,
            HttpServletRequest request
    ) {
        // 사용자에게 보여 줄 대표 검증 메시지 추출
        String message = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(fieldError -> fieldError.getDefaultMessage())
                .orElse("요청 값이 유효하지 않음");

        return error(HttpStatus.BAD_REQUEST, "VALIDATION_FAILED", message, request);
    }

    // 400: QueryParam/pathVariable Bean Validation 실패(@Min/@Max) 시
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(
            ConstraintViolationException e,
            HttpServletRequest request
    ) {
        // 사용자에게 보여 줄 메시지 추출
        String message = e.getConstraintViolations()
                .stream()
                .findFirst()
                .map(v -> v.getMessage())
                .orElse("요청 값이 유효하지 않음");

        return error(HttpStatus.BAD_REQUEST, "VALIDATION_FAILED", message, request);
    }

    // 401: 인증되지 않은 요청(세션이 없을 때 등)에 대해 로그인 필요 응답 반환
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(
            UnauthorizedException e,
            HttpServletRequest request
    ) {
        return error(HttpStatus.UNAUTHORIZED, "AUTH_REQUIRED", e.getMessage(), request);
    }

    @ExceptionHandler(HttpSessionRequiredException.class)
    public ResponseEntity<ErrorResponse> handleHttpSessionRequired(
            HttpSessionRequiredException e, HttpServletRequest request
    ) {
        return error(HttpStatus.UNAUTHORIZED, "AUTH_REQUIRED", e.getMessage(), request);
    }


    // 500: 예상하지 못한 예외 서버 오류 통일하고 로그 남김
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(
            Exception e,
            HttpServletRequest request
    ) {
        log.error("Unhandled exception", e);
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "서버 오류 발생~", request);
    }


    
    // 중복되는 에러 응답을 공통 메서드로 분리
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
