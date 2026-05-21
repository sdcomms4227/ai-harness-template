package com.example.harness.domain.sample;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 샘플 레코드 엔티티 — AI 수정 대상 예시.
 */
@Entity
@Table(name = "sample_record")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SampleRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String title;

    @Size(max = 500)
    @Column(length = 500)
    private String description;

    @Builder
    public SampleRecord(String title, String description) {
        this.title = title;
        this.description = description;
    }

    /**
     * 제목을 변경합니다.
     *
     * @param title 새 제목
     */
    public void updateTitle(String title) {
        this.title = title;
    }

}
