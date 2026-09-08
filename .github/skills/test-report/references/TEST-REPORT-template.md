# 測試報告 (Test Report)

**文件編號：** TEST-REPORT-{PROJECT_CODE}-{VERSION}
**專案名稱：** {PROJECT_NAME}　**產出日期：** {DATE}
**對應 Commit：** {GIT_SHA}

---

## 1. 整體摘要

| 項目 | 結果 |
|------|------|
| 單元/整合測試 | {PASSED}/{TOTAL} 通過（{PASS_RATE}%） |
| BDD（Cucumber）情境 | {PASSED}/{TOTAL} 通過 |
| 架構測試（ArchUnit） | {PASSED}/{TOTAL} 通過 |
| 行覆蓋率 | {LINE_COVERAGE}% |
| 分支覆蓋率 | {BRANCH_COVERAGE}% |
| 總執行時間 | {DURATION} 秒 |

**結論：** {✅ 全數通過，可進入 code-review / ⚠️ 有 N 項失敗，需修正}

---

## 2. 單元 / 整合測試明細（surefire）

| 測試類別 | 測試數 | 通過 | 失敗 | 錯誤 | 執行時間(s) |
|---------|-------|------|------|------|------------|
| {ClassName}Test | {N} | {N} | {N} | {N} | {N} |

**失敗案例：**

| 類別 | 方法 | 失敗訊息摘要 |
|------|------|-------------|
| {ClassName} | {methodName} | {ERROR_SUMMARY} |

---

## 3. BDD 測試明細（Cucumber）

| 標籤 | Scenario 數 | 通過 | 失敗 |
|------|------------|------|------|
| @smoke | {N} | {N} | {N} |
| @regression | {N} | {N} | {N} |
| @boundary | {N} | {N} | {N} |
| @error-handling | {N} | {N} | {N} |

---

## 4. 架構測試明細（ArchUnit）

| 規則 | 結果 |
|------|------|
| Controller 不得依賴 Repository | {✅/❌} |
| Service 依賴方向正確 | {✅/❌} |
| Domain 不得依賴上層 | {✅/❌} |
| 無循環依賴 | {✅/❌} |
| 命名慣例（Entity/Repository/Service/Controller） | {✅/❌} |

---

## 5. 覆蓋率明細（JaCoCo）

| Package | 行覆蓋率 | 分支覆蓋率 | 備註 |
|---------|---------|-----------|------|
| service | {N}% | {N}% | |
| controller | {N}% | {N}% | |
| repository | {N}% | {N}% | |

**未覆蓋的關鍵路徑：**
- {CLASS}.{METHOD}：{說明未覆蓋的分支或情境}

---

## 6. 待改善項目

- [ ] {ITEM_1}

---

*本報告由 GitHub Copilot `test-report` Skill 產生。*
