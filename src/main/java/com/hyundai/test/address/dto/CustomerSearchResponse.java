package com.hyundai.test.address.dto;

import com.hyundai.test.address.model.Customer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

/**
 * 고객 검색 응답 DTO
 * - 고객 검색 결과를 담는 응답 데이터 객체
 * - 검색된 고객 수와 고객 목록 포함
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "고객 정보 조회 responseDto")
public class CustomerSearchResponse {
    @Schema(description = "검색된 고객 수")
    private int count;

    @Schema(description = "검색된 고객 목록")
    private List<Customer> customers;
}
