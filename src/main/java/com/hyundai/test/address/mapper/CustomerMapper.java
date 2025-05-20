package com.hyundai.test.address.mapper;

import com.hyundai.test.address.dto.*;
import com.hyundai.test.address.model.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    CustomerMapper INSTANCE = Mappers.getMapper(CustomerMapper.class);

    @Mapping(target = "phoneNumber", qualifiedByName = "sanitizePhoneNumber")
    Customer toCustomer(CustomerRequest dto);

    default CustomerSearchResponse toCustomerSearchResponse(List<Customer> customerList) {
        return CustomerSearchResponse.builder()
                .count(customerList.size())
                .customers(customerList)
                .build();
    }

    CustomerResponse toCustomerResponse(Customer customer);
    default CustomerUpdateResponse toCustomerUpdateResponse(Map<String, Customer> updatedCustomer) {
        return CustomerUpdateResponse.builder()
                .before(updatedCustomer.get("before"))
                .after(updatedCustomer.get("after"))
                .build();
    }
    default CustomerDeleteResponse toCustomerDeleteResponse(List<Customer> deletedCustomers) {
        return CustomerDeleteResponse.builder()
                .deletedCount(deletedCustomers.size())
                .deletedCustomers(deletedCustomers)
                .build();
    };

    @Named("sanitizePhoneNumber")
    static String sanitizePhoneNumber(String phoneNumber) {
        return phoneNumber.replaceAll("-", "");
    }
}
