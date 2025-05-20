package com.hyundai.test.address.model;

import lombok.Builder;
import lombok.Data;

/**
 * 시퀀스 정보 모델
 * - ID 시퀀스 관리를 위한 엔티티
 * - 데이터명과 최대 시퀀스 값 포함
 */
@Data
@Builder
public class Sequence {
    private String data;
    private Long maxSequence;
}
