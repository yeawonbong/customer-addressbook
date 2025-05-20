package com.hyundai.test.address.dto;

import com.hyundai.test.address.model.Customer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * 고객 정보 수정 응답 DTO
 * - 고객 정보 수정 결과를 담는 응답 데이터 객체
 * - 수정 전/후 고객 정보 포함
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "고객 정보 수정 responseDto")
public class CustomerUpdateResponse {
    @Schema(description = "수정 전 고객 정보")
    private Customer before;

    @Schema(description = "수정 후 고객 정보")
    private Customer after;
}
