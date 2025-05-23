package com.ybong.test.address.exception;

/**
 * 비즈니스 유효성 검증 예외
 * - 비즈니스 로직에서 발생하는 유효성 검증 실패 시 사용
 */
public class BizValidationException extends RuntimeException {
    public BizValidationException(String message) {
        super(message);
    }
} 