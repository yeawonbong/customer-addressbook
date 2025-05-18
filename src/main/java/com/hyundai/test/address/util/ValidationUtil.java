package com.hyundai.test.address.util;

import com.hyundai.test.address.dao.AddressBookDao;
import com.hyundai.test.address.dto.CustomerRequest;
import com.hyundai.test.address.exception.ConflictException;
import jakarta.validation.ValidationException;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

/**
 * 검증 관련 유틸 함수 모음 클래스입니다.
 */
@AllArgsConstructor
@Component
public class ValidationUtil {

    private final AddressBookDao addressBook;
    private final MessageUtil messageUtil;

    public void validateBindingResultOrThrow(BindingResult bindingResult) {
        String defaultMsg = messageUtil.getMessage("validation.default");
        if (bindingResult != null && bindingResult.hasErrors()) {
            FieldError error = bindingResult.getFieldErrors().get(0);
            String errorMsg = error.getDefaultMessage();
            throw new ValidationException(StringUtils.isBlank(errorMsg) ? defaultMsg : errorMsg);
        }
    }

}
