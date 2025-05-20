package com.hyundai.test.address.dto;

import com.hyundai.test.address.model.Customer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "고객 정보 responseDto")
public class CustomerResponse {
    private Customer customer;
}
