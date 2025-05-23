package com.ybong.test.address.util;


/**
 * 유효패턴 검증에 필요한 정규식 패턴 상수들을 공통으로 관리하는 클래스입니다.
 */
public class ValidationPatterns {
    public static final String PATTERN_PHONE = "^(010)(\\d{7,8}|-\\d{3,4}-\\d{4})$";

    private ValidationPatterns() {}
}
