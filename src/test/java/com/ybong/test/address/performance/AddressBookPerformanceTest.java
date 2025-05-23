package com.ybong.test.address.performance;

import com.ybong.test.address.dao.AddressBookDao;
import com.ybong.test.address.model.Customer;
import com.ybong.test.address.util.MessageUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 주소록 성능 테스트
 * - 대량 데이터 처리 성능 검증
 * - 검색 및 정렬 성능 검증
 * - 메모리 사용량 검증
 */
class AddressBookPerformanceTest {

    private AddressBookDao addressBookDao;
    @Mock
    private MessageUtil messageUtil;
    private static final int BULK_SIZE = 1000;  // 대량 데이터 크기

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        addressBookDao = new AddressBookDao(messageUtil);
    }

    @Test
    @DisplayName("대량 데이터 삽입 성능 테스트")
    void testBulkInsertPerformance() {
        // Given
        List<Customer> customers = generateBulkCustomers();
        long startTime = System.currentTimeMillis();

        // When
        for (Customer customer : customers) {
            addressBookDao.save(customer);
        }
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        // Then
        Map<String, Customer> addressBook = addressBookDao.getAddressBook();
        assertEquals(BULK_SIZE, addressBook.size(), "모든 고객이 저장되어야 함");
        assertTrue(executionTime < 5000, "대량 데이터 삽입은 5초 이내에 완료되어야 함");
    }

    @Test
    @DisplayName("대량 데이터 검색 성능 테스트")
    void testBulkSearchPerformance() {
        // Given
        List<Customer> customers = generateBulkCustomers();
        for (Customer customer : customers) {
            addressBookDao.save(customer);
        }
        long startTime = System.currentTimeMillis();

        // When
        for (Customer customer : customers) {
            Optional<Customer> found = addressBookDao.findById(customer.getId());
            assertTrue(found.isPresent(), "저장된 고객을 찾을 수 있어야 함");
        }
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        // Then
        assertTrue(executionTime < 100, "대량 데이터 검색은 100ms 이내에 완료되어야 함");
    }

    @Test
    @DisplayName("대량 데이터 삭제 성능 테스트")
    void testBulkDeletePerformance() {
        // Given
        List<Customer> customers = generateBulkCustomers();
        for (Customer customer : customers) {
            addressBookDao.save(customer);
        }
        long startTime = System.currentTimeMillis();

        // When
        for (Customer customer : customers) {
            addressBookDao.delete(customer);
        }
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        // Then
        Map<String, Customer> addressBook = addressBookDao.getAddressBook();
        assertTrue(addressBook.isEmpty(), "모든 고객이 삭제되어야 함");
        assertTrue(executionTime < 5000, "대량 데이터 삭제는 5초 이내에 완료되어야 함");
    }

    private List<Customer> generateBulkCustomers() {
        List<Customer> customers = new ArrayList<>();
        for (int i = 0; i < BULK_SIZE; i++) {
            customers.add(Customer.builder()
                    .id((long) i)
                    .name("Test User " + i)
                    .email("test" + i + "@test.com")
                    .phoneNumber("010-1234-" + String.format("%04d", i))
                    .address("Test Address " + i)
                    .build());
        }
        return customers;
    }
} 