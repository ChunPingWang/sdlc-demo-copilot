# Code Review 報告 (Code Review Report)

**文件編號：** CODE-REVIEW-LIFE-v1.0
**專案名稱：** 壽險保費試算系統（LifePremium）　**產出日期：** 2026-09-08
**對應 Commit：** 9140bf0

---

## Phase 1：結構檢查（ArchUnit，確定性測試）

執行 `./mvnw test -Dtest="ArchitectureTest"`，共 7 條規則**全數通過**，無需 LLM 重複檢查：

| 規則 | 結果 |
|------|------|
| Controller 不得依賴 Repository | ✅ |
| Service 依賴方向正確（僅可依賴允許清單內的 package） | ✅ |
| Domain 不得依賴上層（controller/service/repository） | ✅ |
| 無循環依賴（package 間） | ✅ |
| Repository 類別命名以 `Repository` 結尾 | ✅ |
| Service 實作類別命名以 `ServiceImpl` 結尾 | ✅ |
| Controller 類別命名以 `Controller` 結尾 | ✅ |

**結論：** 結構層面無 Blocker，進入 Phase 2 語意審查。

---

## Phase 2：語意審查（LLM）

### 業務邏輯正確性

對照 `sdlc/inputs/LIFE-PREMIUM-requirements.md` 的業務規則與
`PremiumCalculationServiceImpl` 實作逐條核對：

| 規則 | 需求文件定義 | 程式碼實作 | 結果 |
|------|-------------|-----------|------|
| BR-001 | 年齡 0～70 歲 | `MIN_AGE=0, MAX_AGE=70` | ✅ 一致 |
| BR-002 | 保額 100～5,000 萬元 | `MIN=1_000_000, MAX=50_000_000` | ✅ 一致 |
| BR-003 | 繳費年期 10/20/30/99 年 | `Set.of(10, 20, 30, 99)` | ✅ 一致 |
| BR-004 | 年繳 = ROUND(保額 ÷ 1000 × 費率) | `divide(1000).multiply(rate).setScale(0, HALF_UP)` | ✅ 一致 |
| BR-005 | 月繳 = ROUND(年繳 ÷ 12 × 1.03) | `divide(12,10,HALF_UP).multiply(1.03).setScale(0,HALF_UP)` | ✅ 一致 |

金額運算全程使用 `BigDecimal` 並明確指定 `RoundingMode.HALF_UP`，避免浮點數誤差，設計正確。

### 資安意圖（OWASP Top 10 對照）

| 檢查項目 | 發現 |
|---------|------|
| SQL Injection | `RateEntryRepository.findCandidates` 使用具名參數 JPQL（`:productCode` 等），無字串拼接，無注入風險 |
| 輸入驗證 | `PremiumCalculateRequest` 有 `@NotBlank/@NotNull/@Pattern`；數值邊界（年齡/保額/繳費年期）交由 Service 層業務規則驗證並回傳對應錯誤碼，設計合理 |
| 授權檢查 | 依 FSD，此 API 對「業務員或訪客」開放，無需登入即可試算，未強制驗證 `X-Agent-Id` 符合此設計意圖，非缺漏 |
| 機密資訊外洩 | 主程式碼未見任何 `log.*`/`System.out` 輸出，`GlobalExceptionHandler` 僅回傳使用者自身輸入的錯誤訊息，`application.yml` 未開啟 stacktrace 輸出，無外洩風險 |
| 反序列化/路徑遍歷/CORS | 未使用檔案上傳、自訂反序列化或跨域設定，不適用 |

### 錯誤處理

- `BusinessException` 及其子類別（`AgeOutOfRangeException` 等）皆由 `GlobalExceptionHandler`
  統一轉換為對應 HTTP 狀態碼與錯誤碼，設計正確。
- **例外**：`CalculationRecordServiceImpl.save()` 在找不到 `Product` 時拋出 `IllegalStateException`，
  此例外未繼承 `BusinessException`，也未被 `GlobalExceptionHandler` 攔截，會直接以 Spring Boot
  預設的 500 錯誤格式回傳（不會外洩堆疊，但格式與其他錯誤不一致）。詳見下方 Major 項目。

---

## Phase 3：問題彙整

### Blocker
無。

### Major

| # | 問題 | 位置 | 建議 |
|---|------|------|------|
| M-1 | 找不到 `Product` 時拋出 `IllegalStateException`（非 `BusinessException`），未被 `GlobalExceptionHandler` 攔截，回應格式與其他錯誤不一致 | `CalculationRecordServiceImpl.save()` | 新增對應的 `BusinessException` 子類別（如 `ProductNotFoundException`）並在 `GlobalExceptionHandler` 統一處理，維持 `ApiResponse` 格式一致 |

### Minor

| # | 問題 | 位置 | 建議 |
|---|------|------|------|
| m-1 | `GlobalExceptionHandler` 未提供 `@ExceptionHandler(Exception.class)` 兜底處理器 | `GlobalExceptionHandler` | 增加兜底處理器，確保任何未預期例外都能回傳一致的 `ApiResponse` 格式，而非交由框架預設錯誤頁 |
| m-2 | `PremiumCalculateRequest` 僅有 `@NotNull`，未加 `@Min/@Max` 做輸入邊界的第二道防線 | `dto/request/PremiumCalculateRequest.java` | 現行由 Service 層 BR-001~003 正確攔截，非缺陷；如需 defense-in-depth 可額外加註解，但非必要 |
| m-3 | Entity 類別（`CalculationRecord`/`RateTableVersion`/`RateEntry`/`Product`）行覆蓋率低於 80% 門檻（詳見 [TEST-REPORT-LIFE-v1.0.md](../../test/output/TEST-REPORT-LIFE-v1.0.md)） | `domain/*` | 視需要補充 `equals`/`hashCode`/建構子測試，非阻塞項目 |

---

## 結論

- ArchUnit 結構檢查 7/7 通過，業務邏輯（BR-001~005）與 OWASP Top 10 相關風險逐項核對皆無 Blocker。
- 有 1 項 Major（錯誤處理一致性）與 3 項 Minor 建議，**不阻擋合併**，建議後續 sprint 補強。

*本報告由 GitHub Copilot `code-review` Skill 產生（Phase 1 ArchUnit + Phase 2/3 LLM 語意審查）。*
