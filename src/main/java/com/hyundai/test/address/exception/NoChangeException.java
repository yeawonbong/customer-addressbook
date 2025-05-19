package com.hyundai.test.address.exception;

/**
 * 변경 사항이 없을 경우 사용하는 Custom Exception.
 * 409: CONFLICT
 */

public class NoChangeException extends RuntimeException {
    public NoChangeException(String message) {
        super(message);
    }
}
