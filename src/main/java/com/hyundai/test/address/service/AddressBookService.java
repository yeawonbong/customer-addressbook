package com.hyundai.test.address.service;

import com.hyundai.test.address.dao.AddressBookDao;
import com.hyundai.test.address.dto.customer.CustomerDto;
import com.hyundai.test.address.exception.ConflictException;
import com.hyundai.test.address.model.Customer;
import com.hyundai.test.address.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class AddressBookService {

    private final AddressBookDao addressBook;
    private final MessageUtil messageUtil;

    public Customer addCustomer(CustomerDto dto) {
        if (addressBook.findByPhoneNumber(dto.getPhoneNumber()).isPresent()) {
            throw new ConflictException(messageUtil.getMessage("validation.conflict.phone"));
        }
        if (addressBook.findByEmail(dto.getEmail()).isPresent()) {
            throw new ConflictException(messageUtil.getMessage("validation.conflict.email"));
        }
        Customer customer = toEntity(dto); //TODO mapper로 교체
        addressBook.insert(customer);
        return customer;
    }

    public List<Customer> getAllCustomers() {
        return addressBook.findAll();
    }

    public Customer updateCustomer(String phoneNumber, CustomerDto dto) {
        Customer existing = addressBook.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new IllegalArgumentException("고객 없음"));

        existing.setName(dto.getName());
        existing.setAddress(dto.getAddress());
        existing.setEmail(dto.getEmail());
        return existing;
    }

    public void deleteCustomer(String phoneNumber) {
        addressBook.delete(phoneNumber);
    }

    private Customer toEntity(CustomerDto dto) { //TODO 매퍼로 교체하기
        return Customer.builder()
                .phoneNumber(dto.getPhoneNumber())
                .name(dto.getName())
                .address(dto.getAddress())
                .email(dto.getEmail())
                .build();
    }
}
