---
name: generate-sd
description: '根據 FSD 文件與技術架構考量，產出系統設計文件（SD，含 C4 L3 元件圖、技術循序圖、API 規格、資料表設計）與架構決策紀錄（ADR），最終產出開發 Task List 供架構師確認後再進行 code gen。Use when the user asks to generate a System Design Document (SD), draft ADRs, design APIs/data tables from an FSD, or produce a development task list.'
argument-hint: '#sdlc/fsd/output/FSD-{CODE}-v{N}.md'
---

# generate-sd — 系統設計文件產出

## 概述

本 Skill 以 FSD 文件為主要輸入，結合技術架構決策，產出完整的系統設計文件（SD）與**架構決策紀錄（ADR）**。
兩個 HITL 確認關卡確保架構師在大量程式碼產出前驗證設計正確性，避免方向錯誤導致的 token 浪費與重工。

> **ADR 是輸出，HITL-1 是雙向關卡。** 架構決策的主體是「人」，Copilot 不自行拍板。
> HITL-1 同時具備兩個方向：**輸入**（架構師提供實際決策 / 從候選方案挑選）與**審核**（核准 ADR 由 `Proposed` 轉 `Accepted`）。
> 先前已 `Accepted` 的 ADR 被沿用時，屬「重用既有輸出」，非重新撰寫輸入。

```
輸入：FSD-{CODE}-*.md（+ 既有已 Accepted 的 ADR，若有）+ 企業技術標準規範
   │
   ▼ Phase 1：產出 SD 文件本體 + 起草 ADR（Status: Proposed）
   │      ① Copilot 提出候選方案（含 tradeoff）      系統 → 人
   │      ② 架構師輸入決策 / 補限制                  人 → 系統（輸入）
   │      ③ Copilot 依決策定稿 ADR
   │      ④ 架構師審核核准 Proposed → Accepted        人 → 系統（審核）
   │      ⑤ 輸出 sdlc/adr/output/ADR-NNNN-*.md
   ▼ Phase 2：產出開發 Task List
   │  ⏸ HITL-2：Task List 確認
   ▼ 進入 /springboot-codegen
```

## 輸入來源（Input）

| 輸入類型 | 路徑 / 說明 |
|---------|------------|
| FSD 文件 | `sdlc/fsd/output/FSD-{PROJECT_CODE}-*.md` |
| Gherkin Feature | `sdlc/fsd/output/features/*.feature` |
| 既有 ADR（沿用） | `sdlc/adr/output/ADR-*.md`（狀態為 `Accepted` 者作為既定前提） |
| 技術標準規範 | 使用者提供或 `sdlc/inputs/tech-standards.md` |

> ADR **不是**需要人工事先手寫的輸入文件。除非有先前專案已 `Accepted` 的 ADR 要沿用，
> 否則本階段的架構決策一律由本 Skill 於 Phase 1 起草為 `Proposed`，經 HITL-1 由架構師輸入決策並審核後定稿為輸出。

## Phase 1：產出 SD 文件本體

### Step 1-1：讀取並分析 FSD
依序讀取 FSD 第 4 章（系統概述）、第 5 章（C4 L1/L2）、第 6 章（業務循序圖）、
第 7 章（功能需求 FR）、第 8 章（非功能需求）、第 9 章（整合需求）、第 13 章（Gherkin）。

### Step 1-2：起草架構決策（ADR，Status: Proposed）

讀取 `sdlc/adr/output/` 既有 `Accepted` ADR 作為既定前提；對每個尚未有決策的架構面向，
**提出候選方案（含 tradeoff）並起草 ADR 草稿**，狀態一律標 `Proposed`：

| 面向 | 決策來源 |
|------|---------|
| 架構風格 | FSD 系統邊界 + 企業標準 |
| 後端框架 | 企業標準 / 非功能需求 |
| 資料庫 | 非功能需求（效能）+ 資料需求 |
| 快取 | 非功能需求（效能） |
| 認證方式 | FSD 安全需求 |
| ORM / 映射 | 企業標準 |
| 部署平台 | FSD 可用性需求 |

**起草原則：**
- 使用 [references/ADR-template.md](./references/ADR-template.md) 格式（Context / Decision / Alternatives / Consequences）
- 每個決策**至少列 2 個替代方案**與否決理由，供架構師在 HITL-1 判斷
- 草稿一律 `Status: Proposed`，**絕不自行標 `Accepted`**
- 編號由 `sdlc/adr/output/` 現有最大編號接續遞增，不重用

### Step 1-3：填寫 SD 文件各章節

嚴格依照 [references/SD-template.md](./references/SD-template.md) 結構填寫：第 3 章架構概觀（含 §3.2 技術標準宣告 Package Root、
§3.3 ADR 索引表）、第 4 章 C4 L3 元件圖、第 5 章技術層循序圖、第 6 章模組設計、第 7 章資料設計、
第 8 章 API 設計、第 9 章安全設計、第 10 章部署架構、第 11–13 章可觀測性/效能/錯誤處理。

> **⚠️ SD §3.2「技術標準宣告」的 Package Root 是後續 `springboot-codegen` 唯一的 package 命名依據，
> 必須明確填寫（本專案為 `com.example.lifepremium`），不得留白。**

存至：`sdlc/sd/output/SD-{PROJECT_CODE}-v{VERSION}.md`。圖形一律使用 **Mermaid**。

### ⏸ HITL-1：SD 文件 + ADR 確認（雙向關卡）

產出 SD 文件與 `Proposed` ADR 草稿後**立即停止**，呈現：
① Copilot 提出的候選方案（每筆 ADR 附替代方案與 tradeoff）
② 請架構師輸入決策（採納建議 / 改選替代方案 / 補充限制條件）
③ C4 L3 元件清單、API 端點數量、資料表數量供確認
④ 待確認問題清單
⑤ 請架構師回覆「核准 ADR，產出 Task List」以審核核准。

### Step 1-5：核准後定稿 ADR（Proposed → Accepted）

架構師核准後：
1. 更新各 ADR 檔的 `Decision`/`Alternatives`/`Consequences`，反映最終裁決
2. 將 `Status` 由 `Proposed` 改為 `Accepted`，填入決策日期與決策者
3. 更新 `sdlc/adr/README.md` 決策索引表
4. 更新 SD §3.3 的 ADR 索引表狀態欄
5. 若某 ADR 被新決策取代，舊檔標 `Superseded by ADR-NNNN`（保留歷史，不刪除）

定稿後才進入 Phase 2。ADR 一旦 `Accepted`，其決策內容不再直接修改；後續變更須新開 ADR。

## Phase 2：產出開發 Task List

> 目標：將 SD 文件轉譯為明確的開發工作清單，讓架構師在 code gen 前確認 Copilot 的理解完全正確。

### Step 2-1：解析 SD 文件產出類別清單

提取規則：C4 L3 每個 Component → 一個 Java 類別；API 清單每個 Resource → Controller + Service 介面 +
ServiceImpl + Mapper；資料表每張 → Entity + Repository；Request/Response schema → DTO；
錯誤碼表 → Exception 類別 + ErrorCode 枚舉；安全設計 → Security Config。

### Step 2-2：產出 Task List 文件

存至：`sdlc/sd/output/TASK-LIST-{PROJECT_CODE}-v{VERSION}.md`，依 Phase A（測試程式 Red）／
Phase B（實作程式碼 Green，分資料層/DTO層/業務邏輯層/API層/例外處理/設定類別）／
Phase C（重構提示 Refactor）分節列出所有待產出檔案、方法簽章、來源章節對應。

### ⏸ HITL-2：Task List 確認

呈現統計摘要（測試檔案數、實作檔案數）與重點確認清單（資料模型、API 設計、業務規則、技術決策）。
架構師確認無誤後，回覆執行 `/springboot-codegen`；如需修改，Copilot 更新 Task List（不需重新產 SD）。

## 輸出清單

| 產出物 | 路徑 |
|-------|------|
| SD 文件 | `sdlc/sd/output/SD-{PROJECT_CODE}-v{VERSION}.md` |
| ADR（複數） | `sdlc/adr/output/ADR-NNNN-*.md` |
| ADR 決策索引 | `sdlc/adr/README.md` |
| Task List | `sdlc/sd/output/TASK-LIST-{PROJECT_CODE}-v{VERSION}.md` |

## 參考資源

- [references/SD-template.md](./references/SD-template.md) — SD 文件章節結構範本（§3.3 為 ADR 索引）
- [references/ADR-template.md](./references/ADR-template.md) — ADR 範本（Context/Decision/Alternatives/Consequences）
- [references/SD-word-style-guide.md](./references/SD-word-style-guide.md) — Word 套版轉換規範
- FSD 輸入：`sdlc/fsd/output/FSD-{PROJECT_CODE}-*.md`
- Gherkin 輸入：`sdlc/fsd/output/features/*.feature`
