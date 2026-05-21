package com.example.harness.repository;

import com.example.harness.domain.sample.SampleRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * SampleRecord JPA 레포지토리.
 */
public interface SampleRecordRepository extends JpaRepository<SampleRecord, Long> {

    /**
     * 제목에 키워드가 포함된 레코드를 조회합니다.
     *
     * @param keyword 검색 키워드
     * @return 조회 결과 목록
     */
    List<SampleRecord> findByTitleContaining(String keyword);

}
