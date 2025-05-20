package com.hyundai.test.address.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 에러 응답 DTO
 * - API 예외 발생 시 클라이언트에게 전달되는 응답 형식
 */
@Getter
@RequiredArgsConstructor
public class ErrorResponse {
    private final String message;
} 