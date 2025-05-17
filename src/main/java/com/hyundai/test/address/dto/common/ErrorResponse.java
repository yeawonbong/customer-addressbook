package com.hyundai.test.address.dto.common;

public class ErrorResponse {

    private String code;
    private String message;

    public ErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

    // Getter/Setter 추가 (또는 Lombok @Getter, @Setter, @AllArgsConstructor 사용)
    public String getCode() {
        return code;
    }
    public String getMessage() {
        return message;
    }

}
