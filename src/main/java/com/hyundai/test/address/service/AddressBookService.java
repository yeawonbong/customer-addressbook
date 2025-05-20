package com.hyundai.test.address.service;

import com.hyundai.test.address.dao.AddressBookDao;
import com.hyundai.test.address.dao.SequenceDao;
import com.hyundai.test.address.dto.CustomerRequest;
import com.hyundai.test.address.exception.ConflictException;
import com.hyundai.test.address.exception.NoChangeException;
import com.hyundai.test.address.exception.NotFoundException;
import com.hyundai.test.address.mapper.CustomerMapper;
import com.hyundai.test.address.model.Customer;
import com.hyundai.test.address.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 고객 주소록 서비스
 * - 고객 정보의 CRUD 작업 처리
 * - 데이터 유효성 검증 및 비즈니스 로직 처리
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class AddressBookService {

    private static final String ADDRESS = "address";

    private final AddressBookDao addressBook;
    private final MessageUtil messageUtil;
    private final SequenceDao sequenceDao;
    private final CustomerMapper customerMapper;

    /**
     * 새로운 고객 정보를 등록합니다.
     * @param dto 등록할 고객 정보
     * @return 등록된 고객 정보
     * @throws ConflictException 중복된 고객 정보가 존재할 경우
     */
    public Customer addCustomer(CustomerRequest dto) {
        Customer customer = customerMapper.toCustomer(dto);
        customer.setId(sequenceDao.getNextSequence(ADDRESS));
        validateAllConflict(customer);
        return addressBook.save(customer);
    }

    /**
     * 고객 정보를 검색합니다.
     * @param filter 필터
     * @param keyword 검색 키워드
     * @param sortBy 정렬 기준
     * @param sortDir 정렬 방향
     * @return 검색된 고객 목록
     */
    public List<Customer> searchCustomers(
            String filter, String keyword, String sortBy, String sortDir
    ) {
        // 전체 리스트 가져오기
        List<Customer> all = new ArrayList<>(addressBook.getAddressBook().values());

        // 1. 필터/검색
        List<Customer> customerList = all.stream()
                .filter(c -> {
                    if (StringUtils.isBlank(keyword)) return true; // 검색어 없으면 전체
                    if (StringUtils.isBlank(filter)) {
                        // 모든 필드에서 검색 (부분일치)
                        return containsIgnoreCase(c.getName(), keyword)
                                || containsIgnoreCase(c.getAddress(), keyword)
                                || containsIgnoreCase(c.getPhoneNumber(), keyword)
                                || containsIgnoreCase(c.getEmail(), keyword);
                    }
                    return switch (filter) {
                        case "name" -> containsIgnoreCase(c.getName(), keyword);
                        case "address" -> containsIgnoreCase(c.getAddress(), keyword);
                        case "phoneNumber" -> containsIgnoreCase(c.getPhoneNumber(), keyword);
                        case "email" -> containsIgnoreCase(c.getEmail(), keyword);
                        default -> true;
                    };
                })
                .collect(Collectors.toList());

        // 2. 정렬
        Comparator<Customer> comparator = switch (sortBy) {
            case "phoneNumber" ->
                    Comparator.comparing(Customer::getPhoneNumber, Comparator.nullsFirst(String::compareToIgnoreCase));
            case "email" ->
                    Comparator.comparing(Customer::getEmail, Comparator.nullsFirst(String::compareToIgnoreCase));
            case "address" ->
                    Comparator.comparing(Customer::getAddress, Comparator.nullsFirst(String::compareToIgnoreCase));
            default -> Comparator.comparing(Customer::getName, Comparator.nullsFirst(String::compareToIgnoreCase));
        };
        if ("desc".equalsIgnoreCase(sortDir)) {
            comparator = comparator.reversed();
        }
        customerList.sort(comparator);

        // 3. 매핑
        return customerList;
    }

    // 문자열 부분일치(대소문자 무시)
    private boolean containsIgnoreCase(String value, String keyword) {
        if (value == null || keyword == null) return false;
        return value.toLowerCase().contains(keyword.toLowerCase());
    }

    /**
     * 고객 정보를 수정합니다.
     * @param id 수정할 고객 ID
     * @param dto 수정할 고객 정보
     * @return 수정된 고객 정보
     * @throws NotFoundException 고객 정보가 존재하지 않을 경우
     * @throws ConflictException 중복된 고객 정보가 존재할 경우
     * @throws NoChangeException 변경된 정보가 없을 경우
     */
    public Map<String, Customer> updateCustomer(Long id, CustomerRequest dto) {
        Customer customerBefore = validateKeyExist(id);
        validateUkConflict(id, dto);
        Customer customerAfter = customerMapper.toCustomer(dto);
        customerAfter.setId(id);
        if (customerBefore.equals(customerAfter)) {
            throw new NoChangeException(messageUtil.getMessage("customer.nochange"));
        }
        addressBook.save(customerAfter);
        Map<String, Customer> updatedCustomer = new HashMap<>();
        updatedCustomer.put("before", customerBefore);
        updatedCustomer.put("after", customerAfter);
        return updatedCustomer;
    }

    /**
     * 고객 정보를 삭제합니다.
     * @param ids 삭제할 고객 ID 목록
     * @return 삭제된 고객 정보 목록
     */
    public List<Customer> deleteCustomers(List<Long> ids) {
        List<Customer> deletedCustomers = new ArrayList<>();
        for (Long id : ids) {
            Customer customer = validateKeyExist(id);
            deletedCustomers.add(addressBook.delete(customer));
        }
        return deletedCustomers;
    }

    public void validateAllConflict(Customer dto) {
        if (addressBook.findById(dto.getId()).isPresent()) {
            throw new ConflictException(messageUtil.getMessage("validation.conflict.id"));
        }
        if (addressBook.findByPhoneNumber(dto.getPhoneNumber()).isPresent()) {
            throw new ConflictException(messageUtil.getMessage("validation.conflict.phone"));
        }
        if (addressBook.findByEmail(dto.getEmail()).isPresent()) {
            throw new ConflictException(messageUtil.getMessage("validation.conflict.email"));
        }
    }

    public Customer validateKeyExist(Long id) {
        return addressBook.findById(id)
                .orElseThrow(() -> new NotFoundException(messageUtil.getMessage("customer.notfound") + " - " + id));
    }

    public void validateUkConflict(Long id, CustomerRequest dto) {
        if (addressBook.findByPhoneNumber(dto.getPhoneNumber())
                .filter(customer -> !id.equals(customer.getId()))
                .isPresent()) {
            throw new ConflictException(messageUtil.getMessage("validation.conflict.phone"));
        }
        if (addressBook.findByEmail(dto.getEmail())
                .filter(customer -> !id.equals(customer.getId()))
                .isPresent()) {
            throw new ConflictException(messageUtil.getMessage("validation.conflict.email"));
        }
    }

}
