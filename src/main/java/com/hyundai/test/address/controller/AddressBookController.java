package com.hyundai.test.address.controller;

import com.hyundai.test.address.dto.CustomerRequest;
import com.hyundai.test.address.dto.CustomerResponse;
import com.hyundai.test.address.dto.CustomerUpdateRequest;
import com.hyundai.test.address.dto.CustomerUpdateResponse;
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
    private final ValidationUtil validationUtil;

    @Operation(summary = "고객 등록"
            , description = "고객 등록 API<br>- since: 2024-05-20, 봉예원")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "등록 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류"),
            @ApiResponse(responseCode = "409", description = "중복 오류")
    })
    @PostMapping
    public ResponseEntity<?> addCustomer(@RequestBody @Validated CustomerRequest dto, BindingResult bindingResult) {
        validationUtil.validateBindingResultOrThrow(bindingResult); //요청값 유효 검증
        return ResponseEntity.ok(addressBookService.addCustomer(dto));
    }

//    @GetMapping
//    public List<CustomerRequest> searchCustomers(
//            @RequestParam(required = false) String phoneNumber,
//            @RequestParam(required = false) String email,
//            @RequestParam(required = false) String address,
//            @RequestParam(required = false) String name,
//            @RequestParam(defaultValue = "phoneNumber") String sortBy,
//            @RequestParam(defaultValue = "asc") String sortDir
//    ) {
//        return null;//addressBookService.getCustomers(phoneNumber, email, address, name, sortBy, sortDir);
//    }
    @Operation(summary = "고객 정보 수정"
            , description = "고객 정보 수정 API<br>- since: 2024-05-20, 봉예원")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "등록 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류"),
            @ApiResponse(responseCode = "409", description = "중복 오류")
    })
    @PutMapping("/customers/{id}")
    public ResponseEntity<CustomerUpdateResponse> updateCustomer(
            @PathVariable Long id,
            @RequestBody @Validated CustomerUpdateRequest dto
            , BindingResult bindingResult
    ) {
        validationUtil.validateBindingResultOrThrow(bindingResult); //요청값 유효 검증
        return ResponseEntity.ok(addressBookService.updateCustomer(id, dto));
    }

    @Operation(summary = "고객 정보 삭제"
            , description = "고객 정보 삭제 API<br>- since: 2024-05-20, 봉예원")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "등록 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류"),
            @ApiResponse(responseCode = "409", description = "중복 오류")
    })
    @DeleteMapping("/customers")
    public ResponseEntity<List<CustomerResponse>> deleteCustomers(@RequestBody List<Long> ids) {
        List<CustomerResponse> deletedCustomers = addressBookService.deleteCustomers(ids);
        return ResponseEntity.ok(deletedCustomers);
    }
}

