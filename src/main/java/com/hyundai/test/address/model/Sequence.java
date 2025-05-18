package com.hyundai.test.address.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Builder
public class Sequence {
    private String data;
    private Long maxSequence;
}
