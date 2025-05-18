package com.hyundai.test.address.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "고객 정보 responseDto")
public class CustomerUpdateResponse {
    private CustomerResponse before; // 수정 전 정보
    private CustomerResponse after;  // 수정 후 정보
}
