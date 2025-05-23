package com.ybong.test.address.exception;

/**
 * 데이터 미존재 시 사용하는 Custom Exception.
 * 404: NOT_FOUND
 */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
