package com.hyundai.test.address.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

import static com.hyundai.test.address.util.ValidationPatterns.PATTERN_PHONE;

/**
 * 고객 정보 모델
 * - 고객의 기본 정보를 담는 엔티티
 * - ID, 이름, 이메일, 전화번호, 주소 정보 포함
 */
@Data
@Builder
public class Customer {
    @NotNull(message = "{validation.required.id}")
    private Long id;            // PK

    @NotBlank(message = "{validation.required.address}")
    private String address;     // Unique Key

    @Pattern(regexp = PATTERN_PHONE, message = "{validation.invalid.phone}")
    @NotBlank(message = "{validation.required.phone}")
    private String phoneNumber; // Unique Key

    @Email(message = "{validation.invalid.email}")
    @NotBlank(message = "{validation.required.email}")
    private String email;

    @NotBlank(message = "{validation.required.name}")
    private String name;

    /**
     * 고객 ID를 문자열로 반환합니다.
     * @return 고객 ID 문자열
     */
    public String getIdStr() {
        return id.toString();
    }
}
