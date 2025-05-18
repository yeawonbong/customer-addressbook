package com.hyundai.test.address.mapper;

import com.hyundai.test.address.dto.CustomerRequest;
import com.hyundai.test.address.dto.CustomerResponse;
import com.hyundai.test.address.dto.CustomerUpdateRequest;
import com.hyundai.test.address.dto.CustomerUpdateResponse;
import com.hyundai.test.address.model.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    CustomerMapper INSTANCE = Mappers.getMapper(CustomerMapper.class);

    @Mapping(target = "phoneNumber", qualifiedByName = "sanitizePhoneNumber")
    Customer toCustomer(CustomerRequest dto);

    @Mapping(target = "phoneNumber", qualifiedByName = "sanitizePhoneNumber")
    Customer toCustomer(CustomerUpdateRequest dto);

    CustomerRequest toCustomerRequest(Customer customer);
    CustomerUpdateRequest toCustomerUpdateRequest(Customer customer);
    CustomerResponse toCustomerResponse(Customer customer);
    default CustomerUpdateResponse toCustomerUpdateResponse(Customer before, Customer after) {
        return CustomerUpdateResponse.builder()
                .before(toCustomerResponse(before))
                .after(toCustomerResponse(after))
                .build();
    }

    @Named("sanitizePhoneNumber")
    static String sanitizePhoneNumber(String phoneNumber) {
        return phoneNumber.replaceAll("-", "");
    }
}
