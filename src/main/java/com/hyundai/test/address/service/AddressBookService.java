package com.hyundai.test.address.service;

import com.hyundai.test.address.dao.AddressBookDao;
import com.hyundai.test.address.dao.SequenceDao;
import com.hyundai.test.address.dto.CustomerRequest;
import com.hyundai.test.address.dto.CustomerResponse;
import com.hyundai.test.address.dto.CustomerUpdateRequest;
import com.hyundai.test.address.dto.CustomerUpdateResponse;
import com.hyundai.test.address.exception.ConflictException;
import com.hyundai.test.address.exception.NoChangeException;
import com.hyundai.test.address.exception.NotFoundException;
import com.hyundai.test.address.mapper.CustomerMapper;
import com.hyundai.test.address.model.Customer;
import com.hyundai.test.address.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class AddressBookService {

    private static final String ADDRESS = "address";

    private final AddressBookDao addressBook;
    private final MessageUtil messageUtil;
    private final SequenceDao sequenceDao;
    private final CustomerMapper customerMapper;

    public CustomerResponse addCustomer(CustomerRequest dto) {
        Customer customer = customerMapper.toCustomer(dto);
        customer.setId(sequenceDao.getNextSequence(ADDRESS));
        validateAllConflict(customerMapper.toCustomerUpdateRequest(customer));
        return customerMapper.toCustomerResponse(addressBook.save(customer));
    }

    public CustomerUpdateResponse updateCustomer(Long id, CustomerRequest dto) {
        Customer customerBefore = validateKeyExist(id);
        validateUkConflict(id, dto);
        Customer customerAfter = customerMapper.toCustomer(dto);
        customerAfter.setId(id);
        if (customerBefore.equals(customerAfter)) {
            throw new NoChangeException(messageUtil.getMessage("customer.nochange"));
        }
        return customerMapper.toCustomerUpdateResponse(customerBefore, addressBook.save(customerAfter));
    }

    public List<CustomerResponse> deleteCustomers(List<Long> ids) {
        List<CustomerResponse> deletedCustomers = new ArrayList<>();
        for (Long id : ids) {
            Customer customer = validateKeyExist(id);
            deletedCustomers.add(customerMapper.toCustomerResponse(addressBook.delete(customer)));
        }
        return deletedCustomers;
    }

    public void validateAllConflict(CustomerUpdateRequest dto) {
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
                .orElseThrow(() -> new NotFoundException(messageUtil.getMessage("customer.notfound" + " - " + id)));
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
