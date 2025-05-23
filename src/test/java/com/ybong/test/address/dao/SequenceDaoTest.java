package com.ybong.test.address.dao;

import com.ybong.test.address.model.Sequence;
import com.ybong.test.address.util.MessageUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * SequenceDao의 단위 테스트.
 * - CSV 초기 로딩, 시퀀스 증가, 데이터 비교 등 기본 기능 검증
 */
@Slf4j
class SequenceDaoTest {

    @Mock
    private MessageUtil messageUtil;
    private SequenceDao dao;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(messageUtil.getMessage(anyString())).thenReturn("Test Message");
        dao = new SequenceDao(messageUtil);
        
        // 테스트용 시퀀스 데이터 초기화
        Sequence sequence = Sequence.builder()
                .data("address")
                .maxSequence(1L)
                .build();
        dao.insert(sequence);
    }

    @Test
    void testInitialLoad() {
        // CSV에서 정상적으로 데이터가 로딩되는지 검증
        assertFalse(dao.getSequenceMap().isEmpty(), "초기 로딩 시 시퀀스 데이터가 존재해야 함");
        assertTrue(dao.getSequenceMap().containsKey("address"), "address 시퀀스가 존재해야 함");
        assertEquals(1L, dao.getMaxSequence("address"), "초기 시퀀스 값이 1이어야 함");
    }

    @Test
    void testGetNextSequence() {
        // 시퀀스 증가 테스트
        long currentSeq = dao.getMaxSequence("address");
        long nextSeq = dao.getNextSequence("address");
        assertTrue(nextSeq > currentSeq, "다음 시퀀스는 현재 시퀀스보다 커야 함");
        assertEquals(currentSeq + 1, nextSeq, "다음 시퀀스는 현재 시퀀스 + 1이어야 함");
    }

    @Test
    void testToCsvLines() {
        // CSV 데이터 변환 테스트
        List<String> csvLines = dao.toCsvLines();
        assertFalse(csvLines.isEmpty(), "CSV 라인이 비어있지 않아야 함");
        assertEquals(2, csvLines.size(), "CSV 라인은 헤더와 데이터 2개여야 함");
        assertEquals("데이터명,MAX_SEQUENCE", csvLines.get(0), "CSV 헤더가 올바르게 반영되어야 함");
        assertTrue(csvLines.get(1).startsWith("address,"), "CSV 데이터가 올바르게 반영되어야 함");
    }
}
