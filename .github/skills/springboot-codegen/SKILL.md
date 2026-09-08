---
name: springboot-codegen
description: '以 TDD/BDD（Red → Green → Refactor）方法為 Java/Spring Boot 專案產生程式碼。輸入來自 SD 文件（API 規格、資料表設計、C4 L3）與 FSD 的 Gherkin .feature 檔。Use when the user asks to generate Spring Boot code, implement entities/repositories/services/controllers from a Task List, or write tests before implementation using TDD.'
argument-hint: '（無需參數，依 Task List 順序自動驅動）'
---

# springboot-codegen — TDD/BDD 程式碼產出

## 概述

本 Skill 以 **TDD/BDD（Red → Green → Refactor）** 為核心方法論，從 SDLC 文件鏈產生 Java/Spring Boot 程式碼。

**輸入文件鏈：**

| 文件 | 路徑 | 用途 |
|------|------|------|
| Task List | `sdlc/sd/output/TASK-LIST-{PROJECT_CODE}-*.md` | 驅動產出順序（HITL-2 已確認） |
| Gherkin Feature | `sdlc/fsd/output/features/*.feature` | BDD 情境 → Cucumber Step Definitions |
| SD 文件 | `sdlc/sd/output/SD-{PROJECT_CODE}-*.md` | API / 資料表 / C4 L3 → 實作程式碼 |
| FSD 文件 | `sdlc/fsd/output/FSD-{PROJECT_CODE}-*.md` | FR 驗收標準 → 測試案例補充 |

> **⚠️ code gen 開始前必須先讀取 SD 文件第 3.2 節「技術標準宣告」，取得 `Package Root` 定義。**
> 若 SD 文件不存在或 §3.2 為空，**必須停止並提示使用者補充後再繼續**，禁止自行推測 package 名稱。
> 命名規範詳見 `.github/copilot-instructions.md`（每次 session 自動載入，強制生效）。

**輸出目錄：** `src/` 下標準 Spring Boot 專案結構（見 [references/project-structure.md](./references/project-structure.md)）

---

## 執行流程

### ── Phase 1：產出測試程式（Red）⏸ HITL

> 目標：在沒有任何實作的情況下，先讓所有測試「編譯成功但執行失敗（Red）」。

1. **讀取輸入文件**：依序讀取 `.feature` 檔、SD 第 8 章 API 規格、SD 第 7 章資料設計、
   SD 第 4 章 C4 L3、FSD 第 7 章功能需求，**不得跳過任何一項**。
2. **產出 Cucumber Step Definitions**：`src/test/java/{package}/bdd/steps/{Module}Steps.java`，
   每個 Step 先拋出 `PendingException`（確保 Red 狀態），遵循
   [references/test-patterns.md](./references/test-patterns.md) §1。
3. **產出 Controller Integration Tests**：`src/test/java/{package}/controller/{Resource}ControllerTest.java`，
   `@SpringBootTest` + `MockMvc`，涵蓋 2xx/400/401/403/404，遵循 test-patterns.md §2。
4. **產出 Service Unit Tests**：`src/test/java/{package}/service/{Business}ServiceTest.java`，
   JUnit5 + Mockito，涵蓋每個 FR 的主要流程、替代流程、例外情境，遵循 test-patterns.md §3。
5. **產出 Repository Tests**：`src/test/java/{package}/repository/{Resource}RepositoryTest.java`，
   `@DataJpaTest`，涵蓋 CRUD、自訂查詢、唯一性約束，遵循 test-patterns.md §4。
6. **執行編譯驗證**：
   ```bash
   ./mvnw test-compile
   ./mvnw test -Dmaven.test.failure.ignore=true 2>&1 | tail -30
   ```
   | 狀況 | 代表意義 | 處置 |
   |------|---------|------|
   | 編譯成功 + 所有測試失敗 | ✅ 正確的 Red 狀態 | 繼續 HITL |
   | 編譯失敗 | ❌ 測試程式有語法錯誤 | 修正後重新執行 |
   | 部分測試通過 | ⚠️ 可能有預設實作殘留 | 檢查 Production Code 是否乾淨 |

#### ⏸ HITL 確認點 — Phase 1
呈現：編譯結果、測試統計（{N} tests, {N} failed — Red 符合預期）、已產出測試檔案清單。
請使用者確認業務規則、邊界值、測試資料是否正確，回覆「確認，繼續 Phase 2」後才進入 Phase 2。

### ── Phase 2：產出實作程式碼（Green）【自動執行，無需確認】

> 依賴方向由內而外逐層產出：`Entity → Repository → DTO → Exception → Service → Controller → Config`。
> 每個 Step 產出後立即編譯驗證，持續修正直到所有測試通過。

1. **Entity**：`domain/{Entity}.java`，`@Entity`/`@Table`，UUID 主鍵 `@GeneratedValue(strategy = GenerationType.UUID)`，
   關聯用 `FetchType.LAZY`。產出後執行 `./mvnw compile`。
2. **Repository**：`repository/{Entity}Repository.java`，繼承 `JpaRepository<{Entity}, UUID>`，
   自訂查詢優先用 Spring Data 命名查詢。執行 `./mvnw test -Dtest="*RepositoryTest"`（此時應仍 Red，但編譯需成功）。
3. **DTO**：`dto.request`/`dto.response` 下 Java Record，Request 加 Bean Validation 注解，
   Response 不暴露 Entity。含 `ApiResponse<T>`、`PageResponse<T>`。
4. **Exception**：`BusinessException`（抽象基底）、`{Resource}NotFoundException`、
   `BusinessValidationException`、`ErrorCode`（枚舉）、`GlobalExceptionHandler`（`@RestControllerAdvice`）。
5. **Mapper + Service**：先定義 `{Business}Service` 介面，再實作 `{Business}ServiceImpl`；
   寫入操作 `@Transactional`，讀取操作 `@Transactional(readOnly = true)`；業務規則驗證集中於 Service 層。
   執行 `./mvnw test -Dtest="*ServiceTest"`，應由 Red 轉 Green。
6. **Controller**：`@RestController`/`@RequestMapping("/api/v1/{resources}")`，`@Valid` 輸入驗證，
   不含業務邏輯。執行 `./mvnw test -Dtest="*ControllerTest"`，應由 Red 轉 Green。
7. **Config**：依需求產出 `SecurityConfig`/`JpaConfig`（`@EnableJpaAuditing`）/`CacheConfig`/`OpenApiConfig`。
   執行完整測試套件：`./mvnw test 2>&1 | tail -30`。

若測試持續失敗，依序：讀取失敗訊息 → 對照 code-patterns.md 確認實作是否符合規範 →
修正對應 Production Code → 重新執行對應測試類別 → 重複直到 Green。

全部測試通過後，輸出測試結果摘要，**自動進入 Phase 3（無需確認）**。

### ── Phase 3：重構提示（Refactor）

> 目標：在所有測試 Green 的前提下，指出可改善點，**不自動修改**，由開發者決定。

輸出 `REFACTOR-NOTES.md` 至專案根目錄，內容包含：
1. 程式碼異味偵測（重複邏輯、過長方法、巢狀過深、Magic Number）
2. 可抽取介面/抽象（`BaseService<T, ID>`、MapStruct、統一 Error Code）
3. 效能優化建議（N+1 查詢、缺少快取的熱點查詢、可改為 `@Async` 的耗時操作）
4. 測試覆蓋率缺口（哪些 FR 的邊界值未被測試覆蓋）

## 命名規範（摘要）

> ⚠️ 完整規範以 `.github/copilot-instructions.md` 為準，兩者衝突時以該檔為最終依據。

| 類型 | 規範 | 範例 |
|------|------|------|
| Entity | PascalCase，業務單數名詞 | `RateEntry` |
| Repository | `{Entity}Repository` | `RateEntryRepository` |
| Service 介面/實作 | `{Business}Service` / `{Business}ServiceImpl` | `PremiumCalculationService` |
| Controller | `{Resource}Controller` | `PremiumCalculationController` |
| Request/Response DTO | `{Resource}{Action}Request` / `{Resource}Response` | `PremiumCalculateRequest` |
| Exception | `{BusinessReason}Exception` | `AgeOutOfRangeException` |
| Test 類別 | `{TargetClass}Test` | `PremiumCalculationServiceTest` |

## 參考資源

- [references/project-structure.md](./references/project-structure.md) — 標準專案目錄結構
- [references/code-patterns.md](./references/code-patterns.md) — Entity/Repository/Service/Controller/DTO/Exception 程式碼範本
- [references/test-patterns.md](./references/test-patterns.md) — Cucumber/JUnit5/Mockito/@DataJpaTest 測試範本
- SD 輸入：`sdlc/sd/output/SD-{PROJECT_CODE}-*.md`（**必須先讀取 §3.2 取得 Package Root**）
- FSD 輸入：`sdlc/fsd/output/FSD-{PROJECT_CODE}-*.md`
- Gherkin 輸入：`sdlc/fsd/output/features/*.feature`
