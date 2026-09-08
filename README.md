# sdlc-demo-copilot

以「壽險新保件保費試算（Life Premium）」為 MVP，端對端驗證 **GitHub Copilot Skills** 驅動的 SDLC 自動化工作流程：
從需求文件到可執行的 Spring Boot 程式碼，全程由 `.github/skills/` 下的 7 個 Copilot Skill 驅動，關鍵節點由人工確認（HITL）。

本專案參考並移植自 [ChunPingWang/sdlc-demo-kiro](https://github.com/ChunPingWang/sdlc-demo-kiro)，
將原本針對 Kiro 的 Skill 定義，改寫為符合 **VS Code / GitHub Copilot Agent Skills** 開放標準
（`.github/skills/<name>/SKILL.md`）的等價實作，並以相同的保險保費試算案例完整跑過一次流程以驗證可用性。

## 快速導覽

```
sdlc-demo-copilot/
├── SKILL-DESIGN.md              # GitHub Copilot Skill 完整設計文件（本專案核心交付）
├── .github/
│   ├── copilot-instructions.md  # 開發標準（每次 session 自動載入，對應原 Kiro Steering）
│   └── skills/                  # 7 個 GitHub Copilot Skills
│       ├── doc-to-markdown/
│       ├── generate-fsd/
│       ├── generate-sd/
│       ├── springboot-codegen/
│       ├── test-report/
│       ├── code-review/
│       └── markdown-to-word/
│
├── sdlc/                        # SDLC 文件（需求 → FSD → SD → ADR → Task List → 測試報告）
│   ├── inputs/                  # 原始需求
│   ├── fsd/                     # 功能規格文件 + Gherkin
│   ├── sd/                      # 系統設計文件 + Task List
│   ├── adr/                     # 架構決策紀錄（HITL-1 審核）
│   └── test/                    # 測試報告模板與輸出
│
└── src/                         # Spring Boot 實作（由 /springboot-codegen 產出）
    ├── main/java/com/example/lifepremium/
    └── test/java/com/example/lifepremium/
```

## MVP 案例：壽險保費試算

沿用原 repo 的業務情境與規則（BR-001 ~ BR-005），詳見 [sdlc/inputs/LIFE-PREMIUM-requirements.md](sdlc/inputs/LIFE-PREMIUM-requirements.md)。

## 驗證流程

完整 SDLC 執行紀錄與各 Skill 驗證結果，詳見 [sdlc/README.md](sdlc/README.md)。

## 技術棧

| 項目 | 選型 |
|------|------|
| 語言 | Java 17 |
| 框架 | Spring Boot 3.3 |
| ORM | Spring Data JPA + Hibernate 6 |
| 資料庫（本機/正式） | PostgreSQL 15；測試 Profile 使用 H2 In-Memory |
| 測試 | JUnit 5 + Mockito + Cucumber 7 |
| 架構測試 | ArchUnit 1.3 |
| 覆蓋率 | JaCoCo |
| 文件 | Springdoc OpenAPI 2 |
| 建置 | Maven 3.9（含 Maven Wrapper） |

## 快速開始

```bash
# 執行單元測試
./mvnw test -Dgroups="unit"

# 執行全部測試（含 Cucumber BDD、ArchUnit）
./mvnw test

# 啟動應用程式（測試/本機用 H2，profile=local 對接 PostgreSQL）
./mvnw spring-boot:run -Dspring-boot.run.profiles=test
```
