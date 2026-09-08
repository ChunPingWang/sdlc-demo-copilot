---
name: code-review
description: '對 src/ 程式碼進行分層架構審查：結構規則（分層依賴、命名慣例、循環依賴）交給 ArchUnit 確定性測試，業務邏輯與資安意圖交給 LLM 語意審查，降低 token 消耗。Use when the user asks to review code, check architecture compliance, run a security review, or validate layering rules before merging.'
argument-hint: '（無需參數，審查整個 src/）'
---

# code-review — 架構與資安審查（ArchUnit + LLM 分層）

## 概述

採兩層分工，把可機械化的規則交給確定性測試，LLM 只審查真正需要判斷的語意問題，大幅降低 token 消耗：

| 階段 | 執行者 | 審查內容 |
|------|--------|---------|
| Phase 1 結構檢查 | ArchUnit（確定性測試） | 分層依賴方向、package 結構、類別/方法命名慣例、注解規範、循環依賴 |
| Phase 2 語意審查 | LLM（Copilot） | 業務邏輯正確性、資安意圖（授權、注入、機密外洩）、缺漏的輸入驗證、錯誤處理 |
| Phase 3 報告 | LLM（Copilot） | 彙整問題分級（Blocker/Major/Minor），發現錯誤時告警並可回饋 code gen 修正 |

> **為何用 ArchUnit 降 token？** 結構規則（如「Controller 不得直接依賴 Repository」）若交給 LLM 審查，
> token 消耗隨檔案數線性成長；改寫成 ArchUnit 測試後，這類規則由 JVM 確定性驗證，
> LLM 只需聚焦無法機械化的語意判斷。規則範本見 [references/archunit-rules.md](./references/archunit-rules.md)。

## 輸入 / 輸出

- 輸入：`src/` 程式碼 + `.github/copilot-instructions.md`
- 輸出：Code Review 報告（問題分級、修正建議），輸出於對話回應，若有 Blocker 另存
  `sdlc/test/output/CODE-REVIEW-{PROJECT_CODE}-v{VERSION}.md`

## 執行步驟

### Phase 1：結構檢查（ArchUnit）

1. 確認 `src/test/java/{package}/architecture/ArchitectureTest.java` 存在且涵蓋
   [references/archunit-rules.md](./references/archunit-rules.md) 列出的所有規則。若規則有缺漏，
   先補齊 ArchUnit 測試碼再執行。
2. 執行：
   ```bash
   ./mvnw test -Dtest="ArchitectureTest" 2>&1 | tail -30
   ```
3. 任何規則失敗（layered dependency violation、命名不符、循環依賴）視為 **Blocker**，
   必須先修正才能進入 Phase 2。

### Phase 2：語意審查（LLM）

針對 Phase 1 通過後的程式碼，逐檔審查以下面向（**不重複檢查 Phase 1 已涵蓋的結構規則**）：

- **業務邏輯正確性**：計算公式、四捨五入規則、邊界值判斷是否符合 FSD/SD 的業務規則
- **資安意圖**（OWASP Top 10 對照）：
  - 輸入驗證是否完整（SQL Injection、輸入未經 Bean Validation 即進入 Service）
  - 授權檢查是否缺漏（是否所有需要權限的 Endpoint 都有驗證）
  - 機密資訊是否外洩（Log 印出敏感欄位、例外訊息洩漏內部結構）
  - 是否有不安全的反序列化、路徑遍歷、CSRF/CORS 設定問題
- **錯誤處理**：例外是否被正確捕捉並轉換為適當的 HTTP 狀態碼，是否有吞噬例外（catch 後不處理）

### Phase 3：彙整報告

依嚴重度分級：
- **Blocker**：會導致資安漏洞、資料錯誤或系統崩潰，必須修正才能合併
- **Major**：違反業務規則或明顯的程式碼品質問題，建議修正
- **Minor**：風格建議、可讀性改善，不強制

若發現 Blocker，明確告警並列出建議修正方式；使用者確認後可回饋 `springboot-codegen` 重新產出對應程式碼。

## 品質檢查清單

- [ ] ArchUnit 測試（Phase 1）全數通過，無需 LLM 重複檢查結構規則
- [ ] 每個 Service 的業務規則皆有對照 FSD/SD 來源
- [ ] 已檢查 OWASP Top 10 相關風險（輸入驗證、授權、機密外洩、注入）
- [ ] 問題已依 Blocker/Major/Minor 分級並附修正建議

## 參考資源

- [references/archunit-rules.md](./references/archunit-rules.md) — ArchUnit 規則範本
- 對應的架構測試：`src/test/java/{package}/architecture/ArchitectureTest.java`
- 強制規範：`.github/copilot-instructions.md`
