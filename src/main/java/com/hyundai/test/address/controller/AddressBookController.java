package com.hyundai.test.address.controller;

import com.hyundai.test.address.dto.*;
import com.hyundai.test.address.mapper.CustomerMapper;
import com.hyundai.test.address.service.AddressBookService;
import com.hyundai.test.address.util.ValidationUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    private final CustomerMapper customerMapper;

    @Operation(summary = "고객 등록"
            , description = "고객 등록 API<br>- since: 2024-05-20, 봉예원")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "등록 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류"),
            @ApiResponse(responseCode = "409", description = "중복 오류")
    })
    @PostMapping
    public ResponseEntity<CustomerResponse> addCustomer(
            @RequestBody @Validated CustomerRequest reqDto,
            BindingResult bindingResult) {
        validationUtil.validateBindingResultOrThrow(bindingResult); //요청값 유효 검증
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(customerMapper.toCustomerResponse(addressBookService.addCustomer(reqDto)));
    }

    @Operation(
            summary = "고객 목록 조회",
            description = "검색/정렬 가능한 고객 정보 리스트 조회 API<br>- since: 2024-05-20, 봉예원"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류")
    })
    @GetMapping
    public ResponseEntity<CustomerSearchResponse> searchCustomers(
            @Validated @ModelAttribute CustomerSearchRequest reqDto,
            BindingResult bindingResult
    ) {
        validationUtil.validateBindingResultOrThrow(bindingResult); //요청값 유효 검증
        return ResponseEntity.ok(customerMapper.toCustomerSearchResponse(
                addressBookService.searchCustomers(
                        reqDto.getFilter(),
                        reqDto.getKeyword(),
                        reqDto.getSortBy(),
                        reqDto.getSortDir())
        ));
    }

    @Operation(
            summary = "고객 정보 수정",
            description = "고객 정보 수정 API<br>- since: 2024-05-20, 봉예원"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류"),
            @ApiResponse(responseCode = "404", description = "고객 없음"),
            @ApiResponse(responseCode = "409", description = "중복 오류")
    })
    @PutMapping("/{id}")
    public ResponseEntity<CustomerUpdateResponse> updateCustomer(
            @PathVariable Long id,
            @RequestBody @Validated CustomerRequest dto,
            BindingResult bindingResult
    ) {
        validationUtil.validateBindingResultOrThrow(bindingResult); //요청값 유효 검증
        return ResponseEntity.ok(customerMapper.toCustomerUpdateResponse(
                addressBookService.updateCustomer(id, dto)
        ));
    }

    @Operation(
            summary = "고객 정보 삭제",
            description = "여러 고객 정보 삭제 API<br>- since: 2024-05-20, 봉예원"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류"),
            @ApiResponse(responseCode = "404", description = "고객 없음")
    })
    @PostMapping("/delete")
    public ResponseEntity<CustomerDeleteResponse> deleteCustomers(@RequestBody List<Long> ids) {
        return ResponseEntity.ok(customerMapper.toCustomerDeleteResponse(
                addressBookService.deleteCustomers(ids)
        ));
    }
}

