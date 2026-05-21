package com.example.harness.controller;

import com.example.harness.domain.sample.SampleRecord;
import com.example.harness.service.SampleRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * SampleRecord REST API 컨트롤러.
 */
@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
public class SampleRecordController {

    private final SampleRecordService service;

    /**
     * 전체 레코드 목록을 조회합니다.
     *
     * @return 레코드 목록
     */
    @GetMapping
    public ResponseEntity<List<SampleRecord>> list() {
        return ResponseEntity.ok(service.findAll());
    }

    /**
     * 레코드를 단건 조회합니다.
     *
     * @param id 레코드 ID
     * @return 레코드
     */
    @GetMapping("/{id}")
    public ResponseEntity<SampleRecord> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    /**
     * 새 레코드를 생성합니다.
     *
     * @param title       제목
     * @param description 설명
     * @return 생성된 레코드
     */
    @PostMapping
    public ResponseEntity<SampleRecord> create(
            @RequestParam String title,
            @RequestParam(required = false) String description) {
        return ResponseEntity.ok(service.create(title, description));
    }

    /**
     * 레코드 제목을 수정합니다.
     *
     * @param id    레코드 ID
     * @param title 새 제목
     * @return 수정된 레코드
     */
    @PutMapping("/{id}/title")
    public ResponseEntity<SampleRecord> updateTitle(
            @PathVariable Long id,
            @RequestParam String title) {
        return ResponseEntity.ok(service.updateTitle(id, title));
    }

    /**
     * 레코드를 삭제합니다.
     *
     * @param id 레코드 ID
     * @return 204 No Content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

}
