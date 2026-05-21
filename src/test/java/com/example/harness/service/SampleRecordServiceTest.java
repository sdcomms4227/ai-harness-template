package com.example.harness.service;

import com.example.harness.domain.sample.SampleRecord;
import com.example.harness.repository.SampleRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

/**
 * SampleRecordService 단위 테스트.
 */
@ExtendWith(MockitoExtension.class)
class SampleRecordServiceTest {

    @Mock
    private SampleRecordRepository repository;

    @InjectMocks
    private SampleRecordService service;

    private SampleRecord sample;

    @BeforeEach
    void setUp() {
        sample = SampleRecord.builder()
                .title("Sample Title")
                .description("Sample Description")
                .build();
    }

    @Test
    @DisplayName("레코드를 생성하면 저장소에 저장된다")
    void createSavesRecord() {
        given(repository.save(any(SampleRecord.class))).willReturn(sample);

        SampleRecord result = service.create("Sample Title", "Sample Description");

        assertThat(result.getTitle()).isEqualTo("Sample Title");
        then(repository).should().save(any(SampleRecord.class));
    }

    @Test
    @DisplayName("전체 조회 시 저장된 레코드 목록을 반환한다")
    void findAllReturnsList() {
        given(repository.findAll()).willReturn(List.of(sample));

        List<SampleRecord> result = service.findAll();

        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("존재하지 않는 ID로 조회하면 예외가 발생한다")
    void findByIdThrowsWhenNotFound() {
        given(repository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("99");
    }

}
