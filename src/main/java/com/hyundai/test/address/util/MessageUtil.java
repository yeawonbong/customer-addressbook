package com.hyundai.test.address.util;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class MessageUtil {
    private final MessageSource messageSource;

    public String getMessage(String code) {
        return messageSource.getMessage(code, null, java.util.Locale.getDefault());
    }

}
