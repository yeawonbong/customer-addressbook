package com.hyundai.test.address.controller;

import com.hyundai.test.address.model.Customer;
import com.hyundai.test.address.service.AddressBookService;
import com.hyundai.test.address.util.MessageUtil;
import com.hyundai.test.address.mapper.CustomerMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.util.List;
import java.util.Map;

import com.hyundai.test.address.util.ValidationUtil;
import com.hyundai.test.address.exception.BizValidationException;
import com.hyundai.test.address.exception.ConflictException;
import com.hyundai.test.address.exception.NotFoundException;
import com.hyundai.test.address.exception.NoChangeException;
import com.hyundai.test.address.exception.GlobalExceptionHandler;

/**
 * AddressBookController의 MockMvc를 이용한 단위 테스트
 */
@WebMvcTest(controllers = {AddressBookController.class, GlobalExceptionHandler.class})
class AddressBookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AddressBookService addressBookService;

    @MockBean
    private MessageUtil messageUtil;

    @MockBean
    private CustomerMapper customerMapper;


    @MockBean
    private ValidationUtil validationUtil;
    

    @BeforeEach
    void setUp() {
        // 기본 메시지 설정
        when(messageUtil.getMessage("validation.conflict.phone")).thenReturn("전화번호가 이미 등록되어 있습니다");
        when(messageUtil.getMessage("validation.conflict.email")).thenReturn("이메일이 이미 등록되어 있습니다");
        when(messageUtil.getMessage("customer.notfound")).thenReturn("고객을 찾을 수 없습니다");
        when(messageUtil.getMessage("customer.nochange")).thenReturn("변경사항이 없습니다");
        
        // ValidationUtil Mock 설정 수정
        doNothing().when(validationUtil).validateBindingResultOrThrow(any());
    }

    @Test
    void testAddCustomer() throws Exception {
        // 고객 등록 API 테스트
        String requestBody = "{\"name\":\"봉예원\",\"email\":\"bong@test.com\",\"phoneNumber\":\"010-1111-2222\",\"address\":\"서울특별시\"}";
        Customer customer = Customer.builder()
                .id(1L)
                .name("봉예원")
                .email("bong@test.com")
                .phoneNumber("010-1111-2222")
                .address("서울특별시")
                .build();

        when(addressBookService.addCustomer(any())).thenReturn(customer);
        when(customerMapper.toCustomerResponse(any())).thenReturn(new com.hyundai.test.address.dto.CustomerResponse(customer));

        mockMvc.perform(post("/api/customers")
                .contentType("application/json")
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customer.id").value(1))
                .andExpect(jsonPath("$.customer.name").value("봉예원"))
                .andExpect(jsonPath("$.customer.email").value("bong@test.com"))
                .andExpect(jsonPath("$.customer.phoneNumber").value("010-1111-2222"))
                .andExpect(jsonPath("$.customer.address").value("서울특별시"));
    }

    @Test
    void testAddCustomerWithInvalidData() throws Exception {
        // 유효하지 않은 데이터로 고객 등록 시도 시 400 에러 테스트
        String requestBody = "{\"name\":\"\",\"email\":\"invalid-email\",\"phoneNumber\":\"123\",\"address\":\"\"}";
        // 유효하지 않은 입력 케이스에 대한 ValidationUtil 세팅
        doThrow(new BizValidationException("입력값이 올바르지 않습니다")).when(validationUtil).validateBindingResultOrThrow(any());

        mockMvc.perform(post("/api/customers")
                .contentType("application/json")
                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").exists())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void testAddCustomerWithDuplicateData() throws Exception {
        // 중복된 데이터로 고객 등록 시도 시 409 에러 테스트
        String requestBody = "{\"name\":\"홍길동\",\"email\":\"duplicate@test.com\",\"phoneNumber\":\"010-1234-5678\",\"address\":\"서울특별시\"}";
        when(addressBookService.addCustomer(any())).thenThrow(new ConflictException("전화번호가 이미 등록되어 있습니다"));

        mockMvc.perform(post("/api/customers")
                .contentType("application/json")
                .content(requestBody))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").exists())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void testSearchCustomers() throws Exception {
        // 고객 목록 조회 API 테스트
        Customer customer = Customer.builder()
                .id(1L)
                .name("봉예원")
                .email("bong@test.com")
                .phoneNumber("010-1111-2222")
                .address("서울특별시")
                .build();

        when(addressBookService.searchCustomers(any(), any(), any(), any())).thenReturn(List.of(customer));
        when(customerMapper.toCustomerSearchResponse(any())).thenReturn(
            com.hyundai.test.address.dto.CustomerSearchResponse.builder()
                .count(1)
                .customers(List.of(customer))
                .build()
        );

        mockMvc.perform(get("/api/customers")
                .param("filter", "name")
                .param("keyword", "봉예원")
                .param("sortBy", "name")
                .param("sortDir", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(1))
                .andExpect(jsonPath("$.customers[0].id").value(1))
                .andExpect(jsonPath("$.customers[0].name").value("봉예원"))
                .andExpect(jsonPath("$.customers[0].email").value("bong@test.com"))
                .andExpect(jsonPath("$.customers[0].phoneNumber").value("010-1111-2222"))
                .andExpect(jsonPath("$.customers[0].address").value("서울특별시"));
    }

    @Test
    void testSearchCustomersWithInvalidParams() throws Exception {
        // 유효하지 않은 파라미터로 검색 시도 시 400 에러 테스트
        // 유효하지 않은 입력 케이스에 대한 ValidationUtil 세팅
        doThrow(new BizValidationException("입력값이 올바르지 않습니다")).when(validationUtil).validateBindingResultOrThrow(any());
        mockMvc.perform(get("/api/customers")
                .param("filter", "invalid")
                .param("sortBy", "invalid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").exists())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void testUpdateCustomer() throws Exception {
        // 고객 정보 수정 API 테스트
        String requestBody = "{\"name\":\"수정된홍길동\",\"email\":\"updated@test.com\",\"phoneNumber\":\"010-8765-4321\",\"address\":\"부산광역시\"}";
        Customer updatedCustomer = Customer.builder()
                .id(1L)
                .name("수정된홍길동")
                .email("updated@test.com")
                .phoneNumber("010-8765-4321")
                .address("부산광역시")
                .build();

        when(addressBookService.updateCustomer(any(), any())).thenReturn(Map.of("after", updatedCustomer));
        when(customerMapper.toCustomerUpdateResponse(any())).thenReturn(
            com.hyundai.test.address.dto.CustomerUpdateResponse.builder()
                .after(updatedCustomer)
                .build()
        );

        mockMvc.perform(put("/api/customers/1")
                .contentType("application/json")
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.after.id").value(1))
                .andExpect(jsonPath("$.after.name").value("수정된홍길동"))
                .andExpect(jsonPath("$.after.email").value("updated@test.com"))
                .andExpect(jsonPath("$.after.phoneNumber").value("010-8765-4321"))
                .andExpect(jsonPath("$.after.address").value("부산광역시"));
    }

    @Test
    void testUpdateCustomerNotFound() throws Exception {
        // 존재하지 않는 고객 수정 시도 시 404 에러 테스트
        String requestBody = "{\"name\":\"수정된홍길동\",\"email\":\"updated@test.com\",\"phoneNumber\":\"010-8765-4321\",\"address\":\"부산광역시\"}";
        when(addressBookService.updateCustomer(any(), any())).thenThrow(new NotFoundException("고객을 찾을 수 없습니다 - 999"));

        mockMvc.perform(put("/api/customers/999")
                .contentType("application/json")
                .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").exists())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void testUpdateCustomerNoChange() throws Exception {
        // 변경사항 없는 수정 시도 시 409 에러 테스트
        String requestBody = "{\"name\":\"홍길동\",\"email\":\"hong@test.com\",\"phoneNumber\":\"010-1234-5678\",\"address\":\"서울특별시\"}";
        when(addressBookService.updateCustomer(any(), any())).thenThrow(new NoChangeException("변경사항이 없습니다"));

        mockMvc.perform(put("/api/customers/1")
                .contentType("application/json")
                .content(requestBody))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").exists())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void testDeleteCustomers() throws Exception {
        // 고객 삭제 API 테스트
        Customer customer1 = Customer.builder().id(1L).name("삭제1").build();
        Customer customer2 = Customer.builder().id(2L).name("삭제2").build();
        when(addressBookService.deleteCustomers(any())).thenReturn(List.of(customer1, customer2));
        when(customerMapper.toCustomerDeleteResponse(any())).thenReturn(
            com.hyundai.test.address.dto.CustomerDeleteResponse.builder()
                .deletedCount(2)
                .deletedCustomers(List.of(customer1, customer2))
                .build()
        );

        mockMvc.perform(post("/api/customers/delete")
                .contentType("application/json")
                .content("[1, 2]"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deletedCount").value(2))
                .andExpect(jsonPath("$.deletedCustomers[0].id").value(1))
                .andExpect(jsonPath("$.deletedCustomers[0].name").value("삭제1"))
                .andExpect(jsonPath("$.deletedCustomers[1].id").value(2))
                .andExpect(jsonPath("$.deletedCustomers[1].name").value("삭제2"));
    }

    @Test
    void testDeleteCustomersNotFound() throws Exception {
        // 존재하지 않는 고객 삭제 시도 시 404 에러 테스트
        when(addressBookService.deleteCustomers(any())).thenThrow(new NotFoundException("고객을 찾을 수 없습니다 - 999"));

        mockMvc.perform(post("/api/customers/delete")
                .contentType("application/json")
                .content("[999]"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").exists())
                .andExpect(jsonPath("$.message").exists());
    }
}