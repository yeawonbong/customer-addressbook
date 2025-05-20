package com.hyundai.test.address.exception;

import com.hyundai.test.address.util.MessageUtil;
import com.hyundai.test.address.dto.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * 전역 예외 처리 핸들러 테스트
 * - 각종 예외 상황에 대한 처리 검증
 * - 응답 상태 코드와 메시지 검증
 */
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @Mock
    private MessageUtil messageUtil;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        exceptionHandler = new GlobalExceptionHandler(messageUtil);
        when(messageUtil.getMessage(anyString())).thenReturn("Test Message");
    }

    @Test
    @DisplayName("비즈니스 유효성 검증 예외 처리 테스트")
    void handleBizValidationException() {
        // Given
        String errorMessage = "입력값이 올바르지 않습니다.";
        when(messageUtil.getMessage("validation.default")).thenReturn(errorMessage);
        BizValidationException exception = new BizValidationException(messageUtil.getMessage("validation.default"));

        // When
        ResponseEntity<?> response = exceptionHandler.handleBizValidation(exception);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(errorMessage, ((ErrorResponse)response.getBody()).getMessage());
    }

    @Test
    @DisplayName("데이터 충돌 예외 처리 테스트")
    void handleConflictException() {
        // Given
        String errorMessage = "이미 등록된 ID입니다.";
        when(messageUtil.getMessage("validation.conflict.id")).thenReturn(errorMessage);
        ConflictException exception = new ConflictException(messageUtil.getMessage("validation.conflict.id"));

        // When
        ResponseEntity<?> response = exceptionHandler.handleConflict(exception);

        // Then
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        String responseMessage = ((ErrorResponse)response.getBody()).getMessage();
        assertTrue(responseMessage.startsWith("이미 등록된"));
    }

    @Test
    @DisplayName("리소스 미존재 예외 처리 테스트")
    void handleNotFoundException() {
        // Given
        String errorMessage = "존재하지 않는 고객입니다.";
        when(messageUtil.getMessage("customer.notfound")).thenReturn(errorMessage);
        NotFoundException exception = new NotFoundException(messageUtil.getMessage("customer.notfound"));

        // When
        ResponseEntity<?> response = exceptionHandler.handleNotFound(exception);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(errorMessage, ((ErrorResponse)response.getBody()).getMessage());
    }

    @Test
    @DisplayName("데이터 변경 없음 예외 처리 테스트")
    void handleNoChangeException() {
        // Given
        String errorMessage = "변경된 고객 정보가 없습니다.";
        when(messageUtil.getMessage("customer.nochange")).thenReturn(errorMessage);
        NoChangeException exception = new NoChangeException(messageUtil.getMessage("customer.nochange"));

        // When
        ResponseEntity<?> response = exceptionHandler.handleNoChangeException(exception);

        // Then
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(errorMessage, ((ErrorResponse)response.getBody()).getMessage());
    }

    @Test
    @DisplayName("기타 예외 처리 테스트")
    void handleAllException() {
        // Given
        String errorMessage = "서버 처리 중 오류가 발생했습니다.";
        when(messageUtil.getMessage("error.default")).thenReturn(errorMessage);
        Exception exception = new Exception(messageUtil.getMessage("error.default"));

        // When
        ResponseEntity<?> response = exceptionHandler.handleAll(exception);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(errorMessage, ((ErrorResponse)response.getBody()).getMessage());
    }
} 