package com.hyundai.test.address.exception;

import com.hyundai.test.address.dto.common.ErrorResponse;
import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<?> handleValidation(ValidationException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("VALIDATION_ERROR", e.getMessage()));
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<?> handleConflict(ConflictException e) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponse("CONFLICT_ERROR", e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAll(Exception e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("SERVER_ERROR", "알 수 없는 오류가 발생했습니다."));
    }
}
