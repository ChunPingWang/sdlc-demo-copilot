---
name: generate-fsd
description: '根據需求文件、User Story、PRD 或現有原始碼，產出功能規格文件（FSD），內含 C4 L1/L2 架構圖與 Gherkin BDD 測試案例。Use when the user asks to generate a Functional Specification Document (FSD), turn requirements into functional specs, or produce Gherkin feature files from business requirements.'
argument-hint: '#sdlc/inputs/需求文件.md'
---

# generate-fsd — 功能規格文件產出

## 概述

本 Skill 指導 Copilot 將輸入的需求文件或原始碼，轉化為符合企業標準的功能規格文件（FSD）。
輸出格式支援 **Markdown**（主要）與 **Word 套版**（依 [references/FSD-word-style-guide.md](./references/FSD-word-style-guide.md) 規範，
實際轉檔由 `/markdown-to-word` 執行）。

採**分階段 HITL 確認**，避免一次產出大量內容後才發現方向錯誤：

| Phase | 產出 | HITL 確認重點 |
|-------|------|--------------|
| Phase 1 | FSD 主體 + C4 L1 System Context + C4 L2 Container + 業務循序圖 | 架構與功能正確性 |
| Phase 2 | Gherkin `.feature` 檔（BDD 測試情境） | 測試案例是否覆蓋所有驗收標準 |

## 輸入來源（Input）

| 輸入類型 | 說明 | 範例指令 |
|---------|------|---------|
| 需求文件 | PRD、User Story、訪談紀錄、需求規格 | `/generate-fsd #需求文件.md` |
| 原始碼 | 現有程式碼（逆向推導功能規格） | `/generate-fsd #src/` |
| 自由描述 | 以文字直接描述功能需求 | `/generate-fsd 我需要一個壽險保費試算系統` |

## 執行步驟

### Step 1：分析輸入

1. 讀取使用者提供的所有輸入文件或描述。
2. 識別以下關鍵資訊：專案名稱與系統邊界、使用者角色（Actor）、核心功能模組（依業務領域分群）、
   業務規則與驗證條件、整合的外部系統。
3. 若輸入為原始碼，分析：Route/Controller → 功能項目；Service 層邏輯 → 業務規則；
   Schema/Model → 資料需求；現有 Test Case → 驗收標準。

### Step 2：規劃文件結構

依分析結果決定模組劃分，每個模組對應 FSD 第 7 章的一個小節。
功能編號規則：`FR-{MODULE_CODE}-{3位序號}`，例如 `FR-PREMIUM-001`。

### Step 3：產生 FSD Markdown（Phase 1）

嚴格依照 [references/FSD-template.md](./references/FSD-template.md) 的章節結構填寫（第 1–12 章）：

- 所有 `{PLACEHOLDER}` 必須替換為實際內容；資訊不足時標注 `⚠️ 待確認：{說明}`。
- 每個功能項目（FR）必須包含：優先等級、功能描述、主要流程、驗收標準。
- 優先等級：高（核心業務流程，缺少即無法運作）／中（重要輔助功能）／低（Nice-to-have）。
- C4 L1/L2 架構圖與業務循序圖一律使用 **Mermaid** 程式碼區塊（` ```mermaid `），可直接在 GitHub/VS Code 預覽。

存至：`sdlc/fsd/output/FSD-{PROJECT_CODE}-v{VERSION}.md`

### ⏸ HITL 確認點 — Phase 1（FSD 主體）

產出 FSD 主體後**停止**，呈現：已識別的模組清單、FR 總數、C4 L1/L2 圖、標注為「待確認」的問題清單。
等待使用者確認「FSD 主體正確」後才進入 Phase 2。

### Step 4：產生 Gherkin 測試案例（Phase 2）

依照 [references/FSD-template.md](./references/FSD-template.md) 第 13 章規範，為每個模組產出對應 `.feature` 檔：

- 每個模組一個 `.feature` 檔，命名 `{MODULE_CODE}-{feature-name}.feature`。
- 每個 Scenario 對應一個 FR 的驗收標準或業務情境（正常流程、替代流程、例外情境各自獨立）。
- 標籤策略：`@smoke`（冒煙）／`@regression`（迴歸）／`@happy-path`／`@boundary`（邊界值）／
  `@error-handling`（例外）／`@wip`（開發中，暫不執行）。
- 邊界值與多組資料驗證使用 `Scenario Outline` + `Examples`。

存至：`sdlc/fsd/output/features/{MODULE_CODE}-{feature-name}.feature`，並在 FSD 第 13 章附上 Feature 清單表。

### ⏸ HITL 確認點 — Phase 2（Gherkin 情境）

呈現 Feature 清單、Scenario 總數、各情境對應的 FR，確認測試情境覆蓋率與業務規則正確後，
提示使用者可接續執行 `/generate-sd #sdlc/fsd/output/FSD-{PROJECT_CODE}-v{VERSION}.md`。

### Step 5：Word 套版轉換（選用）

若使用者要求 Word 檔，提示可執行 `/markdown-to-word #sdlc/fsd/output/FSD-{PROJECT_CODE}-v{VERSION}.md`。

## 品質檢查清單

- [ ] 文件標頭（編號、專案名稱、版本、日期）已填寫
- [ ] 所有功能項目均有唯一的 FR 編號
- [ ] 每個 FR 均有明確的驗收標準
- [ ] 非功能需求（效能、安全、可用性）章節已填寫
- [ ] C4 L1/L2 架構圖與循序圖使用 **Mermaid** 格式
- [ ] 無殘留的 `{PLACEHOLDER}` 佔位符（「待確認」除外）
- [ ] 審查與核准表格已列出相關人員欄位

## 參考資源

- [references/FSD-template.md](./references/FSD-template.md) — FSD 章節結構範本（含 Gherkin 規範）
- [references/FSD-word-style-guide.md](./references/FSD-word-style-guide.md) — Word 套版轉換規範
- 輸出目錄：`sdlc/fsd/output/`
