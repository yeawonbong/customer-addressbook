package com.hyundai.test.address.dto;

import com.hyundai.test.address.model.Customer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "고객 정보 삭제 responseDto")
public class CustomerDeleteResponse {
    private int deletedCount; // 삭제된 고객 수;
    private List<Customer> deletedCustomers; // 삭제된 고객 정보 리스트;
}
