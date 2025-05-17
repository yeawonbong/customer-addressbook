package com.hyundai.test.address.dto.customer;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import static com.hyundai.test.address.util.ValidationPatterns.PATTERN_PHONE;

@Getter
@Setter
@Builder
@Schema(description = "고객 정보 DTO")
public class CustomerDto {

    @Schema(description = "전화번호", example = "010-1234-1234")
    @Pattern(regexp = PATTERN_PHONE, message = "{validation.invalid.phone}")
    @NotBlank(message = "{validation.required.phone}")
    private String phoneNumber;

    @Schema(description = "이름", example = "홍길동")
    @NotBlank(message = "{validation.required.name}")
    private String name;

    @Schema(description = "주소", example = "서울특별시 마포구")
    @NotBlank(message = "{validation.required.address}")
    private String address;

    @Schema(description = "이메일", example = "gildong@test.com")
    @Email(message = "{validation.invalid.email}")
    @NotBlank(message = "{validation.required.email}")
    private String email;

}
