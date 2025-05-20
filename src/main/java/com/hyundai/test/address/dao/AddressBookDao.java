package com.hyundai.test.address.dao;

import com.hyundai.test.address.model.Customer;
import com.hyundai.test.address.util.MessageUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 고객 주소록 데이터 접근 객체
 * - 메모리 내 고객 정보 데이터 관리
 */
@Slf4j
@RequiredArgsConstructor
@Setter
@Getter
@Repository
public class AddressBookDao {
    private final Map<String, Customer> addressBook = new ConcurrentHashMap<>();
    private final Map<String, Customer> addressBook_readOnly = new ConcurrentHashMap<>();
    private final MessageUtil messageUtil;
    private List<String> addressBook_init;

    /**
     * 등록/수정 시 고객 정보를 저장합니다.
     * @param customer 저장할 고객 정보
     * @return 저장된 고객 정보
     */
    public Customer save(Customer customer) {
        addressBook.put(customer.getIdStr(), customer);
        addressBook_readOnly.put(customer.getIdStr(), customer);
        log.debug(messageUtil.getMessage("log.customer.save.success"));
        return customer;
    }

    /**
     * 고객 ID로 고객 정보를 조회합니다.
     * @param id 조회할 고객 ID
     * @return 조회된 고객 정보 (Optional)
     */
    public Optional<Customer> findById(Long id) {
        return Optional.ofNullable(addressBook_readOnly.get(id.toString()));
    }

    /**
     * 전화번호로 고객 정보를 조회합니다.
     * @param phoneNumber 조회할 전화번호
     * @return 조회된 고객 정보 (Optional)
     */
    public Optional<Customer> findByPhoneNumber(String phoneNumber) {
        for (Customer customer : addressBook_readOnly.values()) {
            if (phoneNumber.equals(customer.getPhoneNumber())) {
                return Optional.of(customer);
            }
        }
        return Optional.empty();
    }

    /**
     * 이메일로 고객 정보를 조회합니다.
     * @param email 조회할 이메일
     * @return 조회된 고객 정보 (Optional)
     */
    public Optional<Customer> findByEmail(String email) {
        for (Customer customer : addressBook_readOnly.values()) {
            if (email.equals(customer.getEmail())) {
                return Optional.of(customer);
            }
        }
        return Optional.empty();
    }

    /**
     * 고객 정보를 삭제합니다.
     * @param customer 삭제할 고객 정보
     * @return 삭제된 고객 정보
     */
    public Customer delete(Customer customer) {
        addressBook.remove(customer.getIdStr());
        Customer customerDeleted = addressBook_readOnly.remove(customer.getIdStr());
        log.debug(messageUtil.getMessage("log.customer.delete.success"));
        return customerDeleted;
    }

    /**
     * 전체 주소록 데이터를 반환합니다.
     * @return 주소록 데이터 맵
     */
    public Map<String, Customer> getAddressBook() {
        return addressBook_readOnly;
    }

    public void setAddressBook_init() {
        this.addressBook_init = this.toCsvLines();
    }

    /**
     * 주소록 데이터를 CSV 형식의 문자열 목록으로 변환합니다.
     * @return CSV 형식의 문자열 목록
     */
    public List<String> toCsvLines() {
        List<Customer> customerList = new ArrayList<>(addressBook.values());
        customerList.sort(Comparator.comparing(Customer::getId));
        List<String> lines = new ArrayList<>();
        lines.add("고객ID,주소,연락처,이메일,이름"); // 헤더
        for (Customer c : customerList) {
            lines.add(String.format("%s,%s,%s,%s,%s",
                    c.getIdStr(),
                    c.getAddress(),
                    c.getPhoneNumber(),
                    c.getEmail(),
                    c.getName()));
        }
        return lines;
    }
}
