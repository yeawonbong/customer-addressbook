package com.hyundai.test.address.dao;

import com.hyundai.test.address.exception.BizValidationException;
import com.hyundai.test.address.model.Sequence;
import com.hyundai.test.address.util.MessageUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 시퀀스 데이터 접근 객체
 * - 메모리 내 시퀀스 데이터 관리
 */
@RequiredArgsConstructor
@Setter
@Getter
@Repository
public class SequenceDao {
    private final Map<String, Sequence> sequenceMap = new ConcurrentHashMap<>();
    private final MessageUtil messageUtil;

    /**
     * 새로운 시퀀스를 저장합니다.
     * @param sequence 저장할 시퀀스 정보
     * @throws BizValidationException 시퀀스 정보가 유효하지 않을 경우
     */
    public void insert(Sequence sequence) {
        if (sequence == null || sequence.getData() == null) {
            throw new BizValidationException(messageUtil.getMessage("validation.default"));
        }
        sequenceMap.put(sequence.getData(), sequence);
    }

    /**
     * 데이터명으로 시퀀스를 조회합니다.
     * @param dataName 조회할 데이터명
     * @return 조회된 시퀀스 정보
     */
    public Sequence getSequence(String dataName) {
        return sequenceMap.get(dataName);
    }

    /**
     * 데이터명의 최대 시퀀스 값을 조회합니다.
     * @param dataName 조회할 데이터명
     * @return 최대 시퀀스 값
     */
    public Long getMaxSequence(String dataName) {
        return sequenceMap.get(dataName).getMaxSequence();
    }

    /**
     * 데이터명의 다음 시퀀스 값을 조회하고 증가시킵니다.
     * @param dataName 조회할 데이터명
     * @return 다음 시퀀스 값
     */
    public Long getNextSequence(String dataName) {
        this.getSequence(dataName).setMaxSequence(this.getMaxSequence(dataName) + 1);
        return sequenceMap.get(dataName).getMaxSequence();
    }

    /**
     * 시퀀스 데이터를 CSV 형식의 문자열 목록으로 변환합니다.
     * @return CSV 형식의 문자열 목록
     */
    public List<String> toCsvLines() {
        List<String> lines = new ArrayList<>();
        lines.add("데이터명,MAX_SEQUENCE"); // 헤더
        for (Sequence s : sequenceMap.values()) {
            lines.add(String.format("%s,%d",
                    s.getData(),
                    s.getMaxSequence()));
        }
        return lines;
    }
}
