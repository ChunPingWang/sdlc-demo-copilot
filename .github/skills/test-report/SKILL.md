---
name: test-report
description: '執行 Maven/Gradle 測試後，彙整 surefire、cucumber、jacoco 原始報告為統一格式的中文測試報告。Use when the user asks to summarize test results, generate a test report, or aggregate coverage/BDD/unit test outputs after running the build.'
argument-hint: '（無需參數，讀取 target/ 下最新測試報告）'
---

# test-report — 測試報告彙整

## 概述

執行 Maven/Gradle 測試後，讀取原始報告，套用 [references/TEST-REPORT-template.md](./references/TEST-REPORT-template.md)
彙整成統一的中文測試報告，可直接附於交付文件。

## 輸入 / 輸出

| 輸入 | 說明 |
|------|------|
| `target/surefire-reports/` | 單元/整合測試通過率、失敗案例、執行時間 |
| `target/cucumber-reports/` | BDD 情境結果（依 `@smoke`/`@regression` 標籤分群） |
| `target/site/jacoco/` | 行/分支覆蓋率，標示未覆蓋的關鍵路徑 |
| ArchUnit 測試結果 | 架構規則檢查結果（分層依賴、命名、循環依賴） |

輸出：`sdlc/test/output/TEST-REPORT-{PROJECT_CODE}-v{VERSION}.md`

## 執行步驟

1. 執行完整測試套件並產生報告：
   ```bash
   ./mvnw test
   ./mvnw jacoco:report
   ```
2. 讀取 `target/surefire-reports/*.xml`，統計：總測試數、通過數、失敗數、錯誤數、執行時間；
   列出失敗案例的類別、方法、失敗訊息摘要。
3. 讀取 `target/cucumber-reports/cucumber.json`（若存在），依標籤（`@smoke`/`@regression`/
   `@boundary`/`@error-handling`）分群統計 Scenario 通過率。
4. 讀取 `target/site/jacoco/jacoco.xml`，統計整體與各 package 的行覆蓋率／分支覆蓋率，
   標示覆蓋率低於門檻（建議 80%）的類別。
5. 讀取 ArchUnit 測試結果（`architecture.ArchitectureTest`），列出通過的規則清單
   （分層依賴、命名慣例、循環依賴）。
6. 依 [references/TEST-REPORT-template.md](./references/TEST-REPORT-template.md) 套版輸出，
   存至 `sdlc/test/output/TEST-REPORT-{PROJECT_CODE}-v{VERSION}.md`。
7. 回報整體摘要：通過率、覆蓋率、待改善項目清單。

## 品質檢查清單

- [ ] 單元/整合/BDD/ArchUnit 四類測試結果皆已納入報告
- [ ] 失敗案例（如有）附上錯誤訊息摘要與可能原因
- [ ] 覆蓋率低於門檻的類別已標示並建議補強方向
- [ ] 報告存放於 `sdlc/test/output/`，檔名含專案代碼與版本

## 參考資源

- [references/TEST-REPORT-template.md](./references/TEST-REPORT-template.md) — 測試報告範本
- 輸出目錄：`sdlc/test/output/`
