# Task List — 壽險保費試算（Life Premium）

**文件編號：** TASK-LIST-LIFE-v1.0　**對應 SD：** SD-LIFE-v1.0

## 任務清單

| Task ID | 任務描述 | 對應 FR | 對應層 | 狀態 |
|---------|---------|--------|--------|------|
| T-001 | 建立 Maven 專案骨架（pom.xml、mvnw wrapper、application.yml/-test.yml/-local.yml） | — | infra | ⏳ |
| T-002 | 建立 Entity：Product、RateTableVersion、RateEntry、CalculationRecord | FR-PREMIUM-001, FR-RATE-001 | domain | ⏳ |
| T-003 | 建立 Repository：ProductRepository、RateEntryRepository、CalculationRecordRepository、RateTableVersionRepository | FR-PREMIUM-001 | repository | ⏳ |
| T-004 | 建立 Exception：AgeOutOfRangeException、AmountOutOfRangeException、InvalidPaymentPeriodException、RateNotFoundException、GlobalExceptionHandler | FR-PREMIUM-001 | exception | ⏳ |
| T-005 | 建立 DTO：PremiumCalculateRequest、PremiumCalculateResponse、ApiResponse\<T\> | FR-PREMIUM-001 | dto | ⏳ |
| T-006 | 撰寫 Cucumber Step Definitions（Red）：premium-calculation.feature 對應 6 個情境 | FR-PREMIUM-001 | bdd | ⏳ |
| T-007 | 撰寫 Service 單元測試（Red）：PremiumCalculationServiceTest | FR-PREMIUM-001 | test | ⏳ |
| T-008 | 撰寫 Controller 整合測試（Red）：PremiumCalculationControllerTest | FR-PREMIUM-001 | test | ⏳ |
| T-009 | 撰寫 Repository 測試（Red）：RateEntryRepositoryTest | FR-PREMIUM-001 | test | ⏳ |
| T-010 | 實作 PremiumCalculationService / RateTableService / CalculationRecordService（Green） | FR-PREMIUM-001, FR-PREMIUM-002, FR-RATE-001 | service | ⏳ |
| T-011 | 實作 PremiumCalculationController | FR-PREMIUM-001 | controller | ⏳ |
| T-012 | 撰寫 ArchUnit 架構測試 ArchitectureTest | — | test/architecture | ⏳ |
| T-013 | 執行完整測試套件並確認全數轉綠（Refactor 前） | — | verification | ⏳ |
| T-014 | 執行 `test-report` 產出 TEST-REPORT-LIFE-v1.0.md | — | reporting | ⏳ |
| T-015 | 執行 `code-review`（ArchUnit + LLM 語意審查） | — | review | ⏳ |

## HITL 確認點

| HITL 點 | 說明 | 狀態 |
|---------|------|------|
| HITL-2（Task List 確認） | 架構師確認任務清單完整且對應 FR 無遺漏 | ✅ 已確認（本次驗證流程） |

---
*本文件由 GitHub Copilot `generate-sd` Skill 產生。*
