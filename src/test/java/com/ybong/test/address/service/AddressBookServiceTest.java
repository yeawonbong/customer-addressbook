package com.ybong.test.address.service;

import com.ybong.test.address.dao.AddressBookDao;
import com.ybong.test.address.dao.SequenceDao;
import com.ybong.test.address.dto.CustomerRequest;
import com.ybong.test.address.model.Customer;
import com.ybong.test.address.util.MessageUtil;
import com.ybong.test.address.mapper.CustomerMapper;
import com.ybong.test.address.exception.ConflictException;
import com.ybong.test.address.exception.NotFoundException;
import com.ybong.test.address.exception.NoChangeException;
import org.junit.jupiter.api.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * AddressBookService의 주요 비즈니스 메서드 단위 테스트
 */
class AddressBookServiceTest {
    AddressBookDao addressBookDao = mock(AddressBookDao.class);
    SequenceDao sequenceDao = mock(SequenceDao.class);
    MessageUtil messageUtil = mock(MessageUtil.class);
    CustomerMapper customerMapper = mock(CustomerMapper.class);
    AddressBookService service;

    @BeforeEach
    void setUp() {
        service = new AddressBookService(addressBookDao, messageUtil, sequenceDao, customerMapper);
        
        // 기본 메시지 설정
        when(messageUtil.getMessage("validation.conflict.phone")).thenReturn("전화번호가 이미 등록되어 있습니다");
        when(messageUtil.getMessage("validation.conflict.email")).thenReturn("이메일이 이미 등록되어 있습니다");
        when(messageUtil.getMessage("customer.notfound")).thenReturn("고객을 찾을 수 없습니다");
        when(messageUtil.getMessage("customer.nochange")).thenReturn("변경사항이 없습니다");
        
        // 기본 시퀀스 설정
        when(sequenceDao.getMaxSequence(any())).thenReturn(1L);
        when(sequenceDao.getNextSequence(any())).thenReturn(2L);
    }

    @Test
    void testAddCustomer() {
        // Given
        CustomerRequest req = CustomerRequest.builder()
                .address("서울특별시 용산구")
                .phoneNumber("010-3333-4444")
                .email("test333@aaa.com")
                .name("서비스단위테스트")
                .build();

        Customer customer = Customer.builder()
                .id(2L)
                .address("서울특별시 용산구")
                .phoneNumber("010-3333-4444")
                .email("test333@aaa.com")
                .name("서비스단위테스트")
                .build();

        when(addressBookDao.findByPhoneNumber(anyString())).thenReturn(Optional.empty());
        when(addressBookDao.findByEmail(anyString())).thenReturn(Optional.empty());
        when(customerMapper.toCustomer(any(CustomerRequest.class))).thenReturn(customer);
        when(addressBookDao.save(any(Customer.class))).thenReturn(customer);

        // When
        Customer result = service.addCustomer(req);

        assertNotNull(result);
        assertEquals("서비스단위테스트", result.getName());
        assertEquals("010-3333-4444", result.getPhoneNumber());
        assertEquals("test333@aaa.com", result.getEmail());
    }

    @Test
    void testAddCustomerWithDuplicatePhoneNumber() {
        // 전화번호 중복 시 ConflictException 발생 테스트
        CustomerRequest req = CustomerRequest.builder()
                .address("서울특별시 용산구")
                .phoneNumber("010-3333-4444")
                .email("test333@aaa.com")
                .name("서비스단위테스트")
                .build();

        Customer existingCustomer = Customer.builder()
                .id(1L)
                .address("서울특별시 마포구")
                .phoneNumber("010-3333-4444")
                .email("dupPhone@aaa.com")
                .name("서비스단위테스트")
                .build();

        when(addressBookDao.findByPhoneNumber(anyString())).thenReturn(java.util.Optional.of(existingCustomer));
        when(customerMapper.toCustomer(any(CustomerRequest.class))).thenReturn(existingCustomer);

        ConflictException exception = assertThrows(ConflictException.class, () -> service.addCustomer(req));
        assertEquals("전화번호가 이미 등록되어 있습니다", exception.getMessage());
    }

    @Test
    void testAddCustomerWithDuplicateEmail() {
        // 이메일 중복 시 ConflictException 발생 테스트
        CustomerRequest req = CustomerRequest.builder()
                .address("서울특별시 용산구")
                .phoneNumber("010-3333-4444")
                .email("test333@aaa.com")
                .name("서비스단위테스트")
                .build();

        Customer mappedReq = Customer.builder()
                .address("서울특별시 용산구")
                .phoneNumber("010-3333-4444")
                .email("test333@aaa.com")
                .name("서비스단위테스트")
                .build();

        Customer existingCustomer = Customer.builder()
                .id(1L)
                .address("서울특별시 마포구")
                .phoneNumber("010-1000-2000")
                .email("test333@aaa.com")
                .name("서비스단위테스트")
                .build();

        when(addressBookDao.findByPhoneNumber(anyString())).thenReturn(java.util.Optional.empty());
        when(addressBookDao.findByEmail(anyString())).thenReturn(java.util.Optional.of(existingCustomer));
        when(customerMapper.toCustomer(any(CustomerRequest.class))).thenReturn(mappedReq);

        ConflictException exception = assertThrows(ConflictException.class, () -> service.addCustomer(req));
        assertEquals("이메일이 이미 등록되어 있습니다", exception.getMessage());
    }

    @Test
    void testUpdateCustomerNotFound() {
        // 존재하지 않는 고객 수정 시도 시 NotFoundException 발생 테스트
        Long id = 999L;
        CustomerRequest req = CustomerRequest.builder()
                .address("서울특별시 용산구")
                .phoneNumber("010-3333-4444")
                .email("test333@aaa.com")
                .name("서비스단위테스트")
                .build();

        String errorMessage = "고객을 찾을 수 없습니다 - 999";
        when(addressBookDao.findById(id)).thenReturn(java.util.Optional.empty());
        when(messageUtil.getMessage("customer.notfound")).thenReturn("고객을 찾을 수 없습니다");

        NotFoundException exception = assertThrows(NotFoundException.class, () -> service.updateCustomer(id, req));
        assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    void testUpdateCustomerNoChange() {
        // 변경사항 없을 때 NoChangeException 발생 테스트
        Long id = 1L;
        CustomerRequest req = CustomerRequest.builder()
                .address("서울특별시")
                .phoneNumber("010-1111-2222")
                .email("test@test.com")
                .name("홍길동")
                .build();

        Customer existingCustomer = Customer.builder()
                .id(id)
                .address("서울특별시")
                .phoneNumber("010-1111-2222")
                .email("test@test.com")
                .name("홍길동")
                .build();

        String errorMessage = "변경사항이 없습니다";
        when(addressBookDao.findById(id)).thenReturn(java.util.Optional.of(existingCustomer));
        when(customerMapper.toCustomer(any(CustomerRequest.class))).thenReturn(existingCustomer);
        when(messageUtil.getMessage("customer.nochange")).thenReturn(errorMessage);

        NoChangeException exception = assertThrows(NoChangeException.class, () -> service.updateCustomer(id, req));
        assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    void testDeleteCustomerNotFound() {
        // 존재하지 않는 고객 삭제 시도 시 NotFoundException 발생 테스트
        List<Long> ids = List.of(999L);
        String errorMessage = "고객을 찾을 수 없습니다 - 999";
        when(addressBookDao.findById(999L)).thenReturn(java.util.Optional.empty());
        when(messageUtil.getMessage("customer.notfound")).thenReturn("고객을 찾을 수 없습니다");

        NotFoundException exception = assertThrows(NotFoundException.class, () -> service.deleteCustomers(ids));
        assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    void testSearchCustomers() {
        // 고객 검색 기능 테스트
        String filter = "name";
        String keyword = "홍길동";
        String sortBy = "name";
        String sortDir = "asc";
        List<Customer> expectedCustomers = List.of(
            Customer.builder().id(1L).name("홍길동").email("hong@test.com").phoneNumber("010-1234-5678").build(),
            Customer.builder().id(2L).name("홍길순").email("hong2@test.com").phoneNumber("010-8765-4321").build()
        );
        when(addressBookDao.getAddressBook()).thenReturn(Map.of("1", expectedCustomers.get(0), "2", expectedCustomers.get(1)));

        List<Customer> result = service.searchCustomers(filter, keyword, sortBy, sortDir);

        assertNotNull(result);
        assertFalse(result.isEmpty(), "검색 결과가 존재해야 함");
    }

    @Test
    void testUpdateCustomer() {
        // 고객 정보 수정 테스트
        Long id = 1L;
        CustomerRequest updateReq = CustomerRequest.builder()
                .address("부산광역시")
                .phoneNumber("010-5555-6666")
                .email("update@test.com")
                .name("수정테스트")
                .build();

        Customer existingCustomer = Customer.builder()
                .id(id)
                .address("서울특별시")
                .phoneNumber("010-1111-2222")
                .email("old@test.com")
                .name("기존고객")
                .build();

        Customer updatedCustomer = Customer.builder()
                .id(id)
                .address("부산광역시")
                .phoneNumber("010-5555-6666")
                .email("update@test.com")
                .name("수정테스트")
                .build();

        when(addressBookDao.findById(id)).thenReturn(java.util.Optional.of(existingCustomer));
        when(addressBookDao.findByPhoneNumber(anyString())).thenReturn(java.util.Optional.empty());
        when(addressBookDao.findByEmail(anyString())).thenReturn(java.util.Optional.empty());
        when(customerMapper.toCustomer(any(CustomerRequest.class))).thenReturn(updatedCustomer);

        var result = service.updateCustomer(id, updateReq);

        assertNotNull(result);
        assertEquals("수정테스트", result.get("after").getName());
        assertEquals("부산광역시", result.get("after").getAddress());
    }

    @Test
    void testDeleteCustomers() {
        // 고객 삭제 기능 테스트
        List<Long> ids = List.of(1L, 2L);
        Customer customer1 = Customer.builder().id(1L).name("삭제1").build();
        Customer customer2 = Customer.builder().id(2L).name("삭제2").build();

        when(addressBookDao.findById(1L)).thenReturn(java.util.Optional.of(customer1));
        when(addressBookDao.findById(2L)).thenReturn(java.util.Optional.of(customer2));
        when(addressBookDao.delete(any(Customer.class))).thenReturn(customer1, customer2);

        List<Customer> deletedCustomers = service.deleteCustomers(ids);

        assertNotNull(deletedCustomers);
        assertEquals(2, deletedCustomers.size(), "삭제된 고객 수가 일치해야 함");
    }
}
