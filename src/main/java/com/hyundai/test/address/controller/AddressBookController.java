package com.hyundai.test.address.controller;

import com.hyundai.test.address.dto.common.ErrorResponse;
import com.hyundai.test.address.dto.customer.CustomerDto;
import com.hyundai.test.address.model.Customer;
import com.hyundai.test.address.service.AddressBookService;
import com.hyundai.test.address.util.ValidationUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/customers")
public class AddressBookController {

    private final AddressBookService addressBookService;

    @Operation(summary = "고객 등록"
            , description = "고객 등록 API<br>- since: 2024-05-20, 봉예원")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "등록 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류"),
            @ApiResponse(responseCode = "409", description = "중복 오류")
    })
    @PostMapping
    public ResponseEntity<?> add(@RequestBody @Validated CustomerDto dto, BindingResult bindingResult) {
        ValidationUtil.validateBindingResultOrThrow(bindingResult); //요청값 유효 검증
        return ResponseEntity.ok(addressBookService.addCustomer(dto));
    }

    @GetMapping
    public ResponseEntity<List<Customer>> getAll() {
        return ResponseEntity.ok(addressBookService.getAllCustomers());
    }

    @PutMapping("/{phoneNumber}")
    public ResponseEntity<Customer> update(@PathVariable String phoneNumber, @RequestBody CustomerDto dto) {
        return ResponseEntity.ok(addressBookService.updateCustomer(phoneNumber, dto));
    }

    @DeleteMapping("/{phoneNumber}")
    public ResponseEntity<?> delete(@PathVariable String phoneNumber) {
        addressBookService.deleteCustomer(phoneNumber);
        return ResponseEntity.ok().build();
    }
}

