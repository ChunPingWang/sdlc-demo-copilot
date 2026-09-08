# ADR 索引（Architecture Decision Records）

本目錄收錄壽險保費試算系統（Life Premium）之所有架構決策紀錄，依 `generate-sd` Skill 的
ADR 治理流程產生：AI 提出候選方案與取捨 → 架構師於 HITL-1 輸入決策 → AI 起草 ADR →
架構師核准（Proposed → Accepted）。

| ADR | 決策主題 | 狀態 |
|-----|---------|------|
| [ADR-0001](./ADR-0001-架構風格.md) | 架構風格：Modular Monolith | Accepted |
| [ADR-0002](./ADR-0002-後端框架.md) | 後端框架：Spring Boot 3.3 + Java 17 | Accepted |
| [ADR-0003](./ADR-0003-資料庫選型.md) | 資料庫選型：PostgreSQL | Accepted |
| [ADR-0004](./ADR-0004-費率快取策略.md) | 費率快取策略：Redis Cache-Aside | Accepted |
| [ADR-0005](./ADR-0005-主鍵產生策略.md) | 主鍵產生策略：UUID | Accepted |
| [ADR-0006](./ADR-0006-測試資料庫替代方案.md) | 測試環境資料庫替代方案：H2 | Accepted |
