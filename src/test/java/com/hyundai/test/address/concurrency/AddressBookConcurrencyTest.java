package com.hyundai.test.address.concurrency;

import com.hyundai.test.address.dao.AddressBookDao;
import com.hyundai.test.address.model.Customer;
import com.hyundai.test.address.util.MessageUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 주소록 동시성 테스트
 * - 다중 스레드 환경에서의 데이터 일관성 검증
 * - 동시 읽기/쓰기 작업의 안정성 검증
 * - 동시 삭제 작업의 안정성 검증
 */
class AddressBookConcurrencyTest {

    private AddressBookDao addressBookDao;
    @Mock
    private MessageUtil messageUtil;
    private static final int THREAD_COUNT = 10;  // 동시 실행 스레드 수
    private static final int OPERATION_COUNT = 100;  // 스레드당 수행할 작업 수

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        addressBookDao = new AddressBookDao(messageUtil);
    }

    @Test
    @DisplayName("동시 쓰기 작업의 일관성 테스트")
    void testConcurrentWriteConsistency() throws InterruptedException {
        // Given
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        List<Customer> savedCustomers = new ArrayList<>();

        // When
        for (int i = 0; i < THREAD_COUNT; i++) {
            final int threadId = i;
            executorService.submit(() -> {
                try {
                    for (int j = 0; j < OPERATION_COUNT; j++) {
                        Customer customer = Customer.builder()
                                .id((long) (threadId * OPERATION_COUNT + j))
                                .name("Test User " + threadId)
                                .email("test" + threadId + "@test.com")
                                .phoneNumber("010-1234-" + String.format("%04d", j))
                                .address("Test Address " + threadId)
                                .build();
                        
                        Customer saved = addressBookDao.save(customer);
                        synchronized (savedCustomers) {
                            savedCustomers.add(saved);
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        
        latch.await(1, TimeUnit.MINUTES);

        // Then
        Map<String, Customer> addressBook = addressBookDao.getAddressBook();
        assertEquals(THREAD_COUNT * OPERATION_COUNT, addressBook.size(), "모든 고객이 저장되어야 함");
        
        for (Customer customer : savedCustomers) {
            assertTrue(addressBook.containsKey(customer.getIdStr()), 
                "저장된 고객이 주소록에 존재해야 함: " + customer.getIdStr());
            assertEquals(customer, addressBook.get(customer.getIdStr()),
                "저장된 고객 정보가 일치해야 함");
        }
    }

    @Test
    @DisplayName("동시 읽기/쓰기 작업의 일관성 테스트")
    void testConcurrentReadWriteConsistency() throws InterruptedException {
        // Given
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        List<Customer> savedCustomers = new ArrayList<>();

        // When
        for (int i = 0; i < THREAD_COUNT; i++) {
            final int threadId = i;
            executorService.submit(() -> {
                try {
                    for (int j = 0; j < OPERATION_COUNT; j++) {
                        // 쓰기 작업
                        Customer customer = Customer.builder()
                                .id((long) (threadId * OPERATION_COUNT + j))
                                .name("Test User " + threadId)
                                .email("test" + threadId + "@test.com")
                                .phoneNumber("010-1234-" + String.format("%04d", j))
                                .address("Test Address " + threadId)
                                .build();
                        
                        Customer saved = addressBookDao.save(customer);
                        synchronized (savedCustomers) {
                            savedCustomers.add(saved);
                        }

                        // 읽기 작업
                        Optional<Customer> found = addressBookDao.findById(saved.getId());
                        assertTrue(found.isPresent(), "저장된 고객을 찾을 수 있어야 함");
                        assertEquals(saved, found.get(), "저장된 고객 정보가 일치해야 함");
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        
        latch.await(1, TimeUnit.MINUTES);

        // Then
        Map<String, Customer> addressBook = addressBookDao.getAddressBook();
        assertEquals(THREAD_COUNT * OPERATION_COUNT, addressBook.size(), "모든 고객이 저장되어야 함");
    }

    @Test
    @DisplayName("동시 삭제 작업의 일관성 테스트")
    void testConcurrentDeleteConsistency() throws InterruptedException {
        // Given
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        List<Customer> customersToDelete = new ArrayList<>();

        // 먼저 테스트 데이터 생성
        for (int i = 0; i < THREAD_COUNT * OPERATION_COUNT; i++) {
            Customer customer = Customer.builder()
                    .id((long) i)
                    .name("Test User " + i)
                    .email("test" + i + "@test.com")
                    .phoneNumber("010-1234-" + String.format("%04d", i))
                    .address("Test Address " + i)
                    .build();
            addressBookDao.save(customer);
            customersToDelete.add(customer);
        }

        // When
        for (int i = 0; i < THREAD_COUNT; i++) {
            final int threadId = i;
            executorService.submit(() -> {
                try {
                    for (int j = 0; j < OPERATION_COUNT; j++) {
                        int index = threadId * OPERATION_COUNT + j;
                        if (index < customersToDelete.size()) {
                            Customer customer = customersToDelete.get(index);
                            Customer deleted = addressBookDao.delete(customer);
                            assertNotNull(deleted, "삭제된 고객 정보가 반환되어야 함");
                            assertEquals(customer, deleted, "삭제된 고객 정보가 일치해야 함");
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        
        latch.await(1, TimeUnit.MINUTES);

        // Then
        Map<String, Customer> addressBook = addressBookDao.getAddressBook();
        assertTrue(addressBook.isEmpty(), "모든 고객이 삭제되어야 함");
    }
} 