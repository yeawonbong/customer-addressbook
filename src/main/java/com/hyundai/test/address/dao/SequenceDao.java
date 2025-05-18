package com.hyundai.test.address.dao;

import com.hyundai.test.address.model.Sequence;
import com.hyundai.test.address.util.MessageUtil;
import jakarta.validation.ValidationException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@Setter
@Getter
@Repository
public class SequenceDao {
    private final Map<String, Sequence> sequenceMap = new ConcurrentHashMap<>();
    private final MessageUtil messageUtil;

    public void insert(Sequence sequence) {
        if (sequence == null || sequence.getData() == null) {
            throw new ValidationException(messageUtil.getMessage("validation.default"));
        }
        sequenceMap.put(sequence.getData(), sequence);
    }

    public Sequence getSequence(String dataName) {
        return sequenceMap.get(dataName);
    }

    public Long getMaxSequence(String dataName) {
        return sequenceMap.get(dataName).getMaxSequence();
    }

    public Long getNextSequence(String dataName) {
        this.getSequence(dataName).setMaxSequence(this.getMaxSequence(dataName) + 1);
        return sequenceMap.get(dataName).getMaxSequence();
    }

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
