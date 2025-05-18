package com.hyundai.test.address.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import static com.hyundai.test.address.util.ValidationPatterns.PATTERN_PHONE;

@Getter
@Setter
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

    public String getIdStr() {
        return id.toString();
    }
}
