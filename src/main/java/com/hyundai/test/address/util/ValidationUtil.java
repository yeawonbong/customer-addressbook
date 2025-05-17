package com.hyundai.test.address.util;

import jakarta.validation.ValidationException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import com.hyundai.test.address.dto.common.ErrorResponse;

/**
 * 검증 관련 유틸 함수 모음 클래스입니다.
 */
public class ValidationUtil {
    /**
     * 유효성 검사 실패 시 ResponseEntity 반환하고, 없으면 null 반환
     */
    public static void validateBindingResultOrThrow(BindingResult bindingResult) {
        String defaultMsg = "입력값이 올바르지 않습니다.";
        if (bindingResult != null && bindingResult.hasErrors()) {
            FieldError error = bindingResult.getFieldErrors().get(0);
            String errorMsg = error.getDefaultMessage();
            throw new ValidationException(StringUtils.isBlank(errorMsg) ? defaultMsg : errorMsg);
        }
    }

    private ValidationUtil() {}
}
