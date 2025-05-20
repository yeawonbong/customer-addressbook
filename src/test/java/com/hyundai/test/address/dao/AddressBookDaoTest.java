package com.hyundai.test.address.dao;

import com.hyundai.test.address.model.Customer;
import com.hyundai.test.address.util.MessageUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collection;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * AddressBookDao의 단위 테스트.
 * - CSV 초기 로딩, 저장, 데이터 비교 등 기본 기능 검증
 */
@Slf4j
class AddressBookDaoTest {

    @Mock
    private MessageUtil messageUtil;
    private AddressBookDao dao;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(messageUtil.getMessage(anyString())).thenReturn("Test Message");
        dao = new AddressBookDao(messageUtil);
        
        // 테스트용 고객 데이터 초기화
        Customer customer = Customer.builder()
                .id(1L)
                .name("홍길동")
                .email("test1@test.com")
                .phoneNumber("01012345678")
                .address("서울시 마포구")
                .build();
        dao.save(customer);
        dao.setAddressBook_init();
    }

    @Test
    void testInitialLoad() {
        // CSV에서 정상적으로 데이터가 로딩되는지 검증
        Collection<Customer> addressBook = dao.getAddressBook().values();
        Collection<Customer> addressBook_readOnly = dao.getAddressBook_readOnly().values();
        assertFalse(addressBook.isEmpty(), "초기 로딩 시 최소 한 명 이상 데이터가 있어야 한다.");
        assertEquals(addressBook.size(), addressBook_readOnly.size(), "초기 로딩 시 두 컬렉션의 크기가 같아야 한다.");
    }

    @Test
    void testAddressBookConsistency() {
        // addressBook과 초기 상태 addressBook_readOnly가 일치하는지 검증
        Collection<Customer> addressBook = dao.getAddressBook().values();
        Collection<Customer> addressBook_readOnly = dao.getAddressBook_readOnly().values();
        assertEquals(addressBook, addressBook_readOnly, "초기 로딩 시 마스터데이터와 readOnly 데이터가 일치해야 한다.");
    }

    @Test
    void testAddAndFindCustomer() {
        // 고객 추가 및 id 기반 조회 테스트
        Customer newCustomer = Customer.builder()
                .id(99L)
                .name("홍길동")
                .email("test@test.com")
                .phoneNumber("010-1234-5678")
                .address("서울시 종로구")
                .build();
        dao.save(newCustomer);
        assertTrue(dao.findById(99L).isPresent(), "고객 추가 후 조회 가능해야 한다.");
    }

    @Test
    void testToCsvLinesConsistency() {
        // 파일에서 읽은 데이터와 내부 데이터가 일치하는지 검증
        List<String> csvLines = dao.toCsvLines();
        List<String> initLines = dao.getAddressBook_init();
        assertEquals(csvLines, initLines, "CSV 데이터가 일치해야 함");
    }

    @Test
    void testDeleteCustomer() {
        // 고객 삭제 테스트
        Customer customerToDelete = Customer.builder()
                .id(100L)
                .name("삭제테스트")
                .email("delete@test.com")
                .phoneNumber("010-9876-5432")
                .address("서울시 동대문구")
                .build();
        dao.save(customerToDelete);
        Customer deletedCustomer = dao.delete(customerToDelete);
        assertNotNull(deletedCustomer, "삭제된 고객 정보가 반환되어야 함");
        assertFalse(dao.findById(100L).isPresent(), "삭제 후 조회 불가능해야 함");
    }
}
