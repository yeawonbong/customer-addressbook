package com.ybong.test.address.controller;

import com.ybong.test.address.dto.*;
import com.ybong.test.address.mapper.CustomerMapper;
import com.ybong.test.address.service.AddressBookService;
import com.ybong.test.address.util.ValidationUtil;
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

/**
 * 고객 주소록 관리를 위한 REST API 컨트롤러
 * - 고객 등록, 조회, 수정, 삭제 기능 제공
 * - Swagger 문서화 지원
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/customers")
public class AddressBookController {

    private final AddressBookService addressBookService;
    private final ValidationUtil validationUtil;
    private final CustomerMapper customerMapper;

    /**
     * 새로운 고객 정보를 등록합니다.
     * @param reqDto 고객 정보 요청 DTO
     * @param bindingResult 유효성 검증 결과
     * @return 등록된 고객 정보
     */
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

    /**
     * 고객 목록을 검색 조건에 따라 조회합니다.
     * @param reqDto 검색 조건 요청 DTO
     * @param bindingResult 유효성 검증 결과
     * @return 검색된 고객 목록
     */
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

    /**
     * 기존 고객 정보를 수정합니다.
     * @param id 수정할 고객 ID
     * @param dto 수정할 고객 정보
     * @param bindingResult 유효성 검증 결과
     * @return 수정된 고객 정보
     */
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

    /**
     * 여러 고객 정보를 삭제합니다.
     * @param ids 삭제할 고객 ID 목록
     * @return 삭제된 고객 정보 목록
     */
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

