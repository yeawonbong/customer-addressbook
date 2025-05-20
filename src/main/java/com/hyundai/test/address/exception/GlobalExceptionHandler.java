package com.hyundai.test.address.exception;

import com.hyundai.test.address.common.ErrorCode;
import com.hyundai.test.address.dto.ErrorResponse;
import com.hyundai.test.address.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final MessageUtil messageUtil;

    @ExceptionHandler(BizValidationException.class)
    public ResponseEntity<?> handleBizValidation(BizValidationException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(ErrorCode.VALIDATION_ERROR, e.getMessage()));
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<?> handleConflict(ConflictException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(ErrorCode.CONFLICT_ERROR, e.getMessage()));
    }


    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> handleNotFound(NotFoundException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(ErrorCode.NOT_FOUND_ERROR, e.getMessage()));
    }

    @ExceptionHandler(NoChangeException.class)
    public ResponseEntity<ErrorResponse> handleNoChangeException(NoChangeException ex) {
        ErrorResponse error = new ErrorResponse(ErrorCode.CONFLICT_ERROR, ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(ErrorCode.VALIDATION_ERROR, messageUtil.getMessage("validation.default")));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAll(Exception e) {
        log.error(e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(ErrorCode.SERVER_ERROR, messageUtil.getMessage("error.default")));
    }
}
