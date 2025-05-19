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

@Slf4j
@RequiredArgsConstructor
@Setter
@Getter
@Repository
public class AddressBookDao {
    private final Map<String, Customer> addressBook = new ConcurrentHashMap<>(); //TODO ConcurrentHashMap
    private final Map<String, Customer> addressBook_readOnly = new ConcurrentHashMap<>();
    private final MessageUtil messageUtil;
// TODO lines 비교해서 지울지말지 결정    private final Map<String, Customer> addressBook_init = new ConcurrentHashMap<>(); //TODO ConcurrentHashMap

    public Customer save(Customer customer) {
        addressBook.put(customer.getIdStr(), customer);
        Customer customerSaved = addressBook_readOnly.put(customer.getIdStr(), customer);
        log.debug(messageUtil.getMessage("log.customer.save.success")); // TODO 로그내용 수정하기
        return customerSaved;
    }

    public Customer delete(Customer customer) {
        addressBook.remove(customer.getIdStr());
        Customer customerDeleted = addressBook_readOnly.remove(customer.getIdStr());
        log.debug(messageUtil.getMessage("log.customer.delete.success"));
        return customerDeleted;
    }

    public Optional<Customer> findById(Long id) {
        return Optional.ofNullable(addressBook_readOnly.get(id.toString()));
    }

    public Optional<Customer> findByPhoneNumber(String phoneNumber) {
        for (Customer customer : addressBook_readOnly.values()) {
            if (phoneNumber.equals(customer.getPhoneNumber())) {
                return Optional.of(customer);
            }
        }
        return Optional.empty();
    }

    public Optional<Customer> findByEmail(String email) {
        for (Customer customer : addressBook_readOnly.values()) {
            if (email.equals(customer.getEmail())) {
                return Optional.of(customer);
            }
        }
        return Optional.empty();
    }

    public List<Customer> findAll() {
        return new ArrayList<>(addressBook_readOnly.values()); //TODO List, ArrayList
    }

    public Map<String, Customer> getaddressBook() {
        return addressBook_readOnly;
    }

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
