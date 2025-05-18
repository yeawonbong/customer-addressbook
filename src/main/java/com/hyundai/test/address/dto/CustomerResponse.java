package com.hyundai.test.address.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;

import static com.hyundai.test.address.util.ValidationPatterns.PATTERN_PHONE;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "고객 정보 responseDto")
public class CustomerResponse {
    private Long id;
    private String address;
    private String phoneNumber;
    private String email;
    private String name;
}
