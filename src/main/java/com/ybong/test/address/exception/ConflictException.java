package com.ybong.test.address.exception;

/**
 * 중복(duplicate) 데이터 발생 시 사용하는 Custom Exception.
 * 409: CONFLICT
 */
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
