package com.example.harness.service;

import com.example.harness.domain.sample.SampleRecord;
import com.example.harness.repository.SampleRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * SampleRecord 비즈니스 로직 서비스.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SampleRecordService {

    private final SampleRecordRepository repository;

    /**
     * 새 레코드를 저장합니다.
     *
     * @param title       제목
     * @param description 설명
     * @return 저장된 레코드
     */
    @Transactional
    public SampleRecord create(String title, String description) {
        SampleRecord record = SampleRecord.builder()
                .title(title)
                .description(description)
                .build();
        return repository.save(record);
    }

    /**
     * 전체 레코드 목록을 반환합니다.
     *
     * @return 레코드 목록
     */
    public List<SampleRecord> findAll() {
        return repository.findAll();
    }

    /**
     * ID로 레코드를 조회합니다.
     *
     * @param id 레코드 ID
     * @return 조회된 레코드
     */
    public SampleRecord findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Record not found: " + id));
    }

    /**
     * 레코드 제목을 수정합니다.
     *
     * @param id    레코드 ID
     * @param title 새 제목
     * @return 수정된 레코드
     */
    @Transactional
    public SampleRecord updateTitle(Long id, String title) {
        SampleRecord record = findById(id);
        record.updateTitle(title);
        return record;
    }

    /**
     * 레코드를 삭제합니다.
     *
     * @param id 레코드 ID
     */
    @Transactional
    public void delete(Long id) {
        SampleRecord record = findById(id);
        repository.delete(record);
    }

}
