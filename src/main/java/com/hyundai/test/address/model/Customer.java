package com.hyundai.test.address.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Customer {
    private String phoneNumber; // PK
    private String name;
    private String address;     // Unique Key
    private String email;
}
