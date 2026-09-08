# 標準 Spring Boot 專案目錄結構

```
src/
├── main/
│   ├── java/com/example/{projectcode}/
│   │   ├── domain/               # Entity
│   │   │   └── common/           # BaseEntity 等共用基底
│   │   ├── repository/           # Spring Data JPA Repository
│   │   ├── service/               # Service 介面
│   │   │   └── impl/             # Service 實作
│   │   ├── controller/           # REST Controller
│   │   ├── dto/
│   │   │   ├── request/          # Request DTO（Java Record）
│   │   │   └── response/         # Response DTO（Java Record）
│   │   ├── exception/            # 例外類別 + GlobalExceptionHandler
│   │   ├── mapper/                # Entity ↔ DTO 轉換
│   │   └── config/                # Spring 設定類別
│   └── resources/
│       ├── application.yml
│       ├── application-test.yml   # H2 In-Memory，供離線測試/驗證
│       └── application-local.yml  # PostgreSQL + Redis，本機開發
└── test/
    ├── java/com/example/{projectcode}/
    │   ├── bdd/
    │   │   ├── steps/            # Cucumber Step Definitions
    │   │   └── CucumberTestRunner.java
    │   ├── controller/            # Controller Integration Test
    │   ├── service/               # Service Unit Test
    │   ├── repository/            # Repository Test (@DataJpaTest)
    │   └── architecture/
    │       └── ArchitectureTest.java  # ArchUnit：分層依賴、命名、循環依賴
    └── resources/
        └── features -> ../../../../../sdlc/fsd/output/features（實際以 classpath:features 讀取）
```

## 分層依賴規則（ArchUnit 強制，摘要）

- `controller` 只能依賴 `service`、`dto`、`exception`
- `service` 只能依賴 `repository`、`domain`、`dto`、`mapper`、`exception`
- `domain` 不得依賴 `controller`/`service`/`repository`
- 禁止循環依賴

完整規則與 ArchUnit 測試碼範本見 `code-review` Skill 的
[references/archunit-rules.md](../../code-review/references/archunit-rules.md)。
