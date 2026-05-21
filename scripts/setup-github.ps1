# ============================================================
# GitHub 저장소 초기 설정 스크립트
# 실행: .\scripts\setup-github.ps1 -Token "ghp_xxxx" -Owner "your-org" -Repo "your-repo"
# ============================================================
param(
    [Parameter(Mandatory = $true)]
    [string]$Token,

    # TODO: Owner 와 Repo 를 실제 값으로 변경하거나 실행 시 파라미터로 전달하세요.
    [string]$Owner = "your-org",
    [string]$Repo  = "your-repo"
)

# PowerShell 5.1 에서 GitHub API 를 UTF-8 로 호출하는 헬퍼
function Invoke-GH {
    param($Method, $Path, $Body)
    $uri = "https://api.github.com/repos/$Owner/$Repo$Path"
    $hdrs = @{
        Authorization          = "Bearer $Token"
        Accept                 = "application/vnd.github+json"
        "X-GitHub-Api-Version" = "2022-11-28"
    }
    try {
        if ($Body) {
            $bytes = [System.Text.Encoding]::UTF8.GetBytes(($Body | ConvertTo-Json -Depth 10))
            Invoke-RestMethod -Method $Method -Uri $uri -Headers $hdrs `
                -Body $bytes -ContentType "application/json; charset=utf-8" -UseBasicParsing
        } else {
            Invoke-RestMethod -Method $Method -Uri $uri -Headers $hdrs -UseBasicParsing
        }
    } catch {
        Write-Warning "$Method $Path → $($_.Exception.Message)"
        $null
    }
}

# ── 1. main 브랜치 보호 ──────────────────────────────────────
Write-Host "`n[1/4] main 브랜치 보호 설정..." -ForegroundColor Cyan

$mainProtection = @{
    required_status_checks = @{
        strict   = $true
        contexts = @(
            "Scope Guard (Blocked Files)",
            "Static Analysis",
            "Test Gate (JUnit)",
            "Harness Result"
        )
    }
    enforce_admins                  = $true
    required_pull_request_reviews   = @{
        required_approving_review_count = 1
        dismiss_stale_reviews           = $true
        require_code_owner_reviews      = $true
    }
    restrictions                    = $null
    allow_force_pushes              = $false
    allow_deletions                 = $false
}

$result = Invoke-GH PUT "/branches/main/protection" $mainProtection
if ($result) { Write-Host "  OK: main 브랜치 보호 완료" -ForegroundColor Green }

# ── 2. develop 브랜치 보호 ──────────────────────────────────
Write-Host "`n[2/4] develop 브랜치 보호 설정..." -ForegroundColor Cyan

$developProtection = @{
    required_status_checks = @{
        strict   = $true
        contexts = @(
            "Scope Guard (Blocked Files)",
            "Static Analysis",
            "Test Gate (JUnit)",
            "Harness Result"
        )
    }
    enforce_admins                  = $false
    required_pull_request_reviews   = @{
        required_approving_review_count = 1
        dismiss_stale_reviews           = $true
        require_code_owner_reviews      = $false
    }
    restrictions                    = $null
    allow_force_pushes              = $false
    allow_deletions                 = $false
}

$result = Invoke-GH PUT "/branches/develop/protection" $developProtection
if ($result) { Write-Host "  OK: develop 브랜치 보호 완료" -ForegroundColor Green }

# ── 3. 이슈 라벨 생성 ───────────────────────────────────────
Write-Host "`n[3/4] AI 트리거 라벨 생성..." -ForegroundColor Cyan

$labels = @(
    @{ name = "ai-fix";  color = "d73a4a"; description = "AI가 버그를 수정합니다" }
    @{ name = "ai-docs"; color = "0075ca"; description = "AI가 문서/주석을 개선합니다" }
    @{ name = "ai-test"; color = "e4e669"; description = "AI가 테스트 코드를 추가합니다" }
)

foreach ($label in $labels) {
    $result = Invoke-GH POST "/labels" $label
    if ($result) {
        Write-Host "  OK: '$($label.name)' 라벨 생성" -ForegroundColor Green
    }
}

# ── 4. Secrets 안내 ─────────────────────────────────────────
Write-Host "`n[4/4] GitHub Secrets 등록 안내" -ForegroundColor Cyan
Write-Host @"

  아래 Secret을 수동으로 등록하세요:
  GitHub 저장소 → Settings → Secrets and variables → Actions → New repository secret

  ┌─────────────────────┬──────────────────────────────────────┐
  │ Secret 이름         │ 값                                   │
  ├─────────────────────┼──────────────────────────────────────┤
  │ ANTHROPIC_API_KEY   │ sk-ant-xxxxxxxxxxxxxxxxxxxx (Claude) │
  └─────────────────────┴──────────────────────────────────────┘

  * GITHUB_TOKEN은 Actions에서 자동 제공되므로 별도 등록 불필요

"@ -ForegroundColor Yellow

Write-Host "설정 완료!" -ForegroundColor Green
Write-Host "저장소: https://github.com/$Owner/$Repo" -ForegroundColor Cyan
