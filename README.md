# AI + Safety Harness Template

> **"AI는 PR을 열 수 있지만, 머지는 사람이 닫는다"**

GitHub 이슈에 라벨 하나만 붙이면 Claude AI가 코드를 수정하고 PR을 자동 생성하는  
**AI + Safety Harness 파이프라인 템플릿**입니다.

---

## 이 템플릿으로 무엇을 할 수 있나요?

```
이슈 등록 + 라벨 부착
  → Claude Code Action이 코드 수정 + 브랜치 생성 + PR 오픈
  → Safety Harness 5개 게이트 자동 검증
      Gate 1 · Scope Guard      — 보안/설정 파일 변경 차단
      Gate 2 · Static Analysis  — Checkstyle + SpotBugs
      Gate 3 · Test Gate        — JUnit 전체 실행
      Gate 4 · Audit Trail      — PR 메타데이터 검증
      Gate 5 · Harness Result   — 결과 집계 + PR 코멘트
  → 사람이 코드 리뷰 후 승인 → 머지
```

---

## 빠른 시작

### 1단계 — 이 템플릿으로 새 저장소 생성

GitHub 화면 우상단 **"Use this template"** 버튼을 클릭하거나,  
저장소를 fork/clone 후 새 레포에 push합니다.

### 2단계 — TODO 항목 수정

| 파일 | 수정 내용 |
|---|---|
| `build.gradle` | `group`, `description` 변경 |
| `settings.gradle` | `rootProject.name` 변경 |
| `.github/CODEOWNERS` | `@your-github-username` → 실제 계정 |
| `src/main/resources/application.yml` | DB 접속 정보, 앱 이름 변경 |
| `scripts/setup-github.ps1` | `Owner`, `Repo` 기본값 변경 |
| `.github/workflows/ai-issue-handler.yml` | 필요 시 Claude 프롬프트 커스터마이징 |
| `src/main/java/com/example/harness/` | 실제 패키지명으로 리네임 |

### 3단계 — GitHub Secrets 등록

`Settings → Secrets and variables → Actions → New repository secret`

| Secret | 설명 |
|---|---|
| `ANTHROPIC_API_KEY` | Anthropic Console에서 발급한 API 키 |

### 4단계 — 브랜치 보호 및 라벨 자동 설정

```powershell
.\scripts\setup-github.ps1 -Token "ghp_여기에PAT입력" -Owner "your-org" -Repo "your-repo"
```

---

## 사용 방법

### AI 트리거 라벨

| 라벨 | 동작 |
|---|---|
| `ai-fix` | 이슈에 설명된 버그를 수정합니다 |
| `ai-docs` | Javadoc · 주석만 개선합니다 (로직 변경 없음) |
| `ai-test` | 누락된 JUnit · Mockito 테스트를 추가합니다 |

### AI가 생성하는 PR 구조

```
제목: [AI-GENERATED] {이슈 제목}
브랜치: feature/ai-{이슈번호} → develop

본문 필수 항목:
  issue: #이슈번호
  generated_by: claude-code-action
  label: ai-fix
```

---

## Safety Harness 게이트

| # | 게이트 | 도구 | 실패 시 |
|---|---|---|---|
| 1 | **Scope Guard** | `git diff` + 패턴 매칭 | 즉시 차단, PR 코멘트 |
| 2 | **Static Analysis** | Checkstyle · SpotBugs | PR 차단, 리포트 업로드 |
| 3 | **Test Gate** | JUnit 5 | PR 차단, 리포트 업로드 |
| 4 | **Audit Trail** | PR 메타데이터 검증 | AI-GENERATED PR만 적용 |
| 5 | **Harness Result** | 전체 집계 | PR 코멘트 자동 게시 |

### AI 허용 · 차단 범위

| 🟢 AI 허용 | 🔴 AI 차단 |
|---|---|
| 비즈니스 로직 수정 | `SecurityConfig.java` |
| 테스트 코드 추가 | `WebMvcConfig.java` |
| Javadoc / 주석 개선 | `application*.yml` / `*.properties` |
| 단순 버그 픽스 | `*/migration/*.sql` |

차단 패턴 전체: [`harness-rules/blocked-patterns.txt`](harness-rules/blocked-patterns.txt)

---

## 프로젝트 구조

```
ai-harness-template/
├── .github/
│   ├── workflows/
│   │   ├── ai-issue-handler.yml   # 이슈 라벨 → Claude 실행 → PR 생성
│   │   └── safety-harness.yml     # 5단계 안전망 게이트
│   └── CODEOWNERS                 # 리뷰어 자동 지정
│
├── src/
│   ├── main/java/com/example/harness/
│   │   ├── controller/            # REST 컨트롤러 (샘플)
│   │   ├── service/               # 비즈니스 로직 (AI 주요 수정 대상)
│   │   ├── repository/            # JPA 레포지토리
│   │   └── domain/                # 엔티티
│   ├── main/resources/
│   │   └── application.yml        # 운영 DB 설정
│   └── test/
│       ├── java/                  # JUnit + Mockito 단위 테스트
│       └── resources/
│           └── application.yml    # H2 in-memory 테스트 설정
│
├── harness-rules/
│   └── blocked-patterns.txt       # Scope Guard 차단 패턴
├── masking-config/
│   └── masking-rules.yml          # 민감 정보 마스킹 패턴
├── scripts/
│   └── setup-github.ps1           # 저장소 초기 설정 자동화
│
├── build.gradle                   # Spring Boot 3.x + Checkstyle + SpotBugs
├── checkstyle.xml                 # Checkstyle 룰셋
└── spotbugs-exclude.xml           # SpotBugs 오탐 제외
```

---

## 로컬 빌드 및 테스트

```bash
# 전체 Safety Harness 로컬 실행
./gradlew checkstyleMain checkstyleTest spotbugsMain test

# 개별 실행
./gradlew test              # JUnit 테스트 (H2 in-memory)
./gradlew checkstyleMain    # Checkstyle 정적 분석
./gradlew spotbugsMain      # SpotBugs 정적 분석
```

---

## 참고 링크

- [Claude Code Action](https://github.com/anthropics/claude-code-action)
- [Anthropic 공식 문서](https://docs.anthropic.com)
