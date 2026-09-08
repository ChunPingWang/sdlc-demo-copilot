# 測試報告 (Test Report)

**文件編號：** TEST-REPORT-LIFE-v1.0
**專案名稱：** 壽險保費試算系統（LifePremium）　**產出日期：** 2026-09-08
**對應 Commit：** 9140bf0

---

## 1. 整體摘要

| 項目 | 結果 |
|------|------|
| 單元/整合測試 | 20/20 通過（100%） |
| BDD（Cucumber）情境 | 8/8 通過 |
| 架構測試（ArchUnit） | 7/7 通過 |
| 行覆蓋率 | 81.2% |
| 分支覆蓋率 | 83.3% |
| 總執行時間 | 7.47 秒 |

**結論：** ✅ 全數通過（單元 20 + BDD 8，共 28 個測試案例），可進入 code-review。

---

## 2. 單元 / 整合測試明細（surefire）

| 測試類別 | 測試數 | 通過 | 失敗 | 錯誤 | 執行時間(s) |
|---------|-------|------|------|------|------------|
| ArchitectureTest | 7 | 7 | 0 | 0 | 0.891 |
| RunCucumberTest（BDD） | 8 | 8 | 0 | 0 | 1.161 |
| PremiumCalculationControllerTest | 5 | 5 | 0 | 0 | 1.120 |
| RateEntryRepositoryTest | 3 | 3 | 0 | 0 | 3.964 |
| PremiumCalculationServiceTest | 5 | 5 | 0 | 0 | 0.337 |

**失敗案例：** 無。

---

## 3. BDD 測試明細（Cucumber）

| 標籤 | Scenario 數 | 通過 | 失敗 |
|------|------------|------|------|
| @smoke | 1 | 1 | 0 |
| @regression | 3 | 3 | 0 |
| @boundary | 2 | 2 | 0 |
| @error-handling | 4 | 4 | 0 |

> 「正常試算年繳與月繳保費」（@smoke）、「不同年齡性別組合的正常試算」（@regression，Scenario
> Outline 3 組例子）、「年齡/保額超出上限應拒絕試算」（@boundary + @error-handling）、
> 「繳費年期不在允許清單」「查無對應費率」（@error-handling）共 8 個情境，全數通過。

---

## 4. 架構測試明細（ArchUnit）

| 規則 | 結果 |
|------|------|
| Controller 不得依賴 Repository | ✅ |
| Service 依賴方向正確（僅可依賴允許清單內的 package） | ✅ |
| Domain 不得依賴上層（controller/service/repository） | ✅ |
| 無循環依賴（package 間） | ✅ |
| 命名慣例 — Repository 類別以 `Repository` 結尾 | ✅ |
| 命名慣例 — Service 實作類別以 `ServiceImpl` 結尾 | ✅ |
| 命名慣例 — Controller 類別以 `Controller` 結尾 | ✅ |

---

## 5. 覆蓋率明細（JaCoCo）

| Package | 行覆蓋率 | 分支覆蓋率 | 備註 |
|---------|---------|-----------|------|
| service.impl | 100.0% | 83.3% | 業務邏輯核心，覆蓋完整 |
| controller | 100.0% | - | |
| dto.request / dto.response | 100.0% | - | |
| exception | 86.2% | - | |
| domain（Entity） | 63.1% | - | 低於 80% 門檻，詳見下方 |
| lifepremium（Application 進入點） | 33.3% | - | `main()` 未在測試中執行，屬預期情況 |

**未覆蓋的關鍵路徑：**
- `domain.CalculationRecord`（56.0%）、`domain.RateTableVersion`（63.6%）、
  `domain.RateEntry`（66.7%）、`domain.Product`（71.4%）：Entity 的部分 getter/setter、
  `equals`/`hashCode`、次要建構子未被測試直接呼叫到，屬 JPA Entity 常見情況，
  非業務邏輯風險，暫列為 Minor 待補強項目。
- `LifePremiumApplication.main()`：Spring Boot 進入點方法，`@SpringBootTest` 不會執行到
  `main()` 本身，屬預期不覆蓋範圍。

---

## 6. 待改善項目

- [ ] `domain` package 的 Entity 類別（`CalculationRecord`、`RateTableVersion`、`RateEntry`、
      `Product`）行覆蓋率低於 80% 門檻，建議視需要補充 `equals`/`hashCode`/建構子的單元測試。

---

*本報告由 GitHub Copilot `test-report` Skill 產生。*
