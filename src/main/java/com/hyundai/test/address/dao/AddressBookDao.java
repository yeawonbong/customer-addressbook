package com.hyundai.test.address.dao;

import com.hyundai.test.address.model.Customer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@Setter
@Getter
@Repository
public class AddressBookDao {
    private final Map<String, Customer> addressBook = new ConcurrentHashMap<>(); //TODO ConcurrentHashMap
    private final Map<String, Customer> addressBook_readOnly = new ConcurrentHashMap<>();

    public void insert(Customer customer) {
        try {
            if (findByPhoneNumber(customer.getPhoneNumber()).isPresent()) {
                System.out.println("⚠ 중복 전화번호 스킵: " + customer.getPhoneNumber());
            } else {
                addressBook.put(customer.getPhoneNumber(), customer);
                addressBook_readOnly.put(customer.getPhoneNumber(), customer);
            }
        } catch (Exception e) {

        }
    }

    //TODO update

    public void delete(String phoneNumber) {
        addressBook.remove(phoneNumber);
    }

    public void clearAll() {
        addressBook.clear();
    }

    public Optional<Customer> findByPhoneNumber(String phoneNumber) {
        return Optional.ofNullable(addressBook_readOnly.get(phoneNumber));
    }

    public List<Customer> findAll() {
        return new ArrayList<>(addressBook_readOnly.values()); //TODO List, ArrayList
    }

    public Optional<Customer> findByEmail(String email) {
        return Optional.ofNullable(addressBook_readOnly.get(email));
    }

    public Map<String, Customer> getaddressBook() {
        return addressBook_readOnly;
    }
}
