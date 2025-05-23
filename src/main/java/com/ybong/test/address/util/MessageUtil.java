package com.ybong.test.address.util;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

/**
 * 메시지 유틸리티 클래스
 * - 공통 메시지 처리를 위한 유틸리티
 * - 메시지 프로퍼티 파일에서 메시지를 조회
 */
@Component
@RequiredArgsConstructor
public class MessageUtil {
    private final MessageSource messageSource;

    /**
     * 메시지 키에 해당하는 메시지를 조회합니다.
     * @param code 메시지 키
     * @return 조회된 메시지
     */
    public String getMessage(String code) {
        return messageSource.getMessage(code, null, java.util.Locale.getDefault());
    }

}
