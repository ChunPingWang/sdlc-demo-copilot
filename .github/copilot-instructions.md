# Java 開發標準（Steering 對應）

> 適用範圍：`src/**`（本檔為 `.github/copilot-instructions.md`，每次 session 對整個 workspace 自動載入；
> 以下規則實務上僅約束 `src/` 下的 Java 程式碼產出與審查）。

> 本檔案每次 session 自動載入，強制 `springboot-codegen`、`code-review` 等 Skill 遵守下列規範。
> 與各 Skill `SKILL.md` 內文衝突時，**以本檔案為最終依據**。

## Package 結構

Package Root 必須從 SD 文件第 3.2 節「技術標準宣告」讀取，**禁止自行猜測**。本專案 Package Root 為
`com.example.lifepremium`：

```
com.example.lifepremium
├── domain / domain.common      # Entity
├── repository                  # Spring Data JPA Repository
├── service / service.impl      # 業務邏輯（介面 + 實作分離）
├── controller                  # REST Controller
├── dto.request / dto.response  # Java Record DTO
├── exception                   # 例外類別 + GlobalExceptionHandler
├── mapper                      # Entity ↔ DTO 轉換
├── config                      # Spring 設定類別
└── bdd / architecture          # 測試專用：Cucumber steps、ArchUnit
```

## 分層依賴規則（ArchUnit 強制）

- `controller` 只能依賴 `service`、`dto`、`exception`，**不得直接依賴** `repository`。
- `service` 只能依賴 `repository`、`domain`、`dto`、`mapper`、`exception`。
- `domain`（Entity）不得依賴 `controller`、`service`、`repository`。
- 禁止循環依賴（package 間）。

## 命名規範

| 類型 | 規範 | 範例 |
|------|------|------|
| Entity | PascalCase，業務單數名詞 | `RateEntry`, `CalculationRecord` |
| Repository | `{Entity}Repository` | `RateEntryRepository` |
| Service 介面 | `{BusinessConcept}Service` | `PremiumCalculationService` |
| Service 實作 | `{BusinessConcept}ServiceImpl` | `PremiumCalculationServiceImpl` |
| Controller | `{Resource}Controller` | `PremiumCalculationController` |
| Request DTO | `{Resource}{Action}Request` | `PremiumCalculateRequest` |
| Response DTO | `{Resource}Response` | `PremiumCalculateResponse` |
| Exception | `{BusinessReason}Exception` | `AgeOutOfRangeException` |
| Test 類別 | `{TargetClass}Test` | `PremiumCalculationServiceTest` |
| Step Definition | `{Feature}Steps` | `PremiumCalculationSteps` |

## 注解規範

- 寫入操作 Service 方法加 `@Transactional`；讀取操作加 `@Transactional(readOnly = true)`。
- Request DTO 使用 Bean Validation 注解（`@NotNull`, `@Min`, `@Max`, `@Pattern` 等），
  驗證失敗一律交由 `GlobalExceptionHandler`（`@RestControllerAdvice`）統一處理，回傳 422/400。
- Entity 主鍵使用 `@Id` + `@GeneratedValue(strategy = GenerationType.UUID)`（除已有自然鍵之業務表如
  `RateEntry` 可用複合唯一約束）。
- Controller 不含業務邏輯，只做請求轉發與回應格式化；業務規則驗證集中於 Service 層。
- DTO 一律使用 Java Record（Java 17+），Response 不對外暴露 Entity 直接引用。

## TDD/BDD 方法論

`springboot-codegen` 必須遵循 **Red → Green → Refactor**：先產出測試（編譯成功但執行失敗），
經 HITL 確認後才產出實作程式碼，全部測試轉綠後才輸出 `REFACTOR-NOTES.md` 重構建議（僅建議，不自動修改）。

## Code Review 分工（降低 token 消耗）

- **結構規則**（分層依賴、命名慣例、循環依賴）一律交給 `src/test/java/.../architecture/ArchitectureTest.java`
  的 ArchUnit 確定性測試驗證，不透過 LLM 判斷。
- **語意規則**（業務邏輯正確性、資安意圖、輸入驗證缺漏）才由 `code-review` Skill 的 LLM 階段審查。
