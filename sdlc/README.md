# SDLC 執行紀錄與驗證報告

本文件記錄以「壽險保費試算（Life Premium）」案例，依序執行 `.github/skills/` 下 7 個 GitHub Copilot Skill
的完整過程與驗證結果，證明每個 Skill 皆可產出對應交付物並串接下一階段。

## 執行時間軸

| # | Skill | 執行日期 | 狀態 | 主要輸出 |
|---|-------|---------|------|---------|
| 0 | （前置）需求輸入 | 2026-09-08 | ✅ 完成 | [sdlc/inputs/LIFE-PREMIUM-requirements.md](inputs/LIFE-PREMIUM-requirements.md) |
| ① | `doc-to-markdown` | — | ⏭️ 略過 | 需求已為 Markdown，無需轉換（見下方說明） |
| ② | `generate-fsd` | 2026-09-08 | ✅ 完成 | [FSD-LIFE-v1.0.md](fsd/output/FSD-LIFE-v1.0.md)、[premium-calculation.feature](fsd/output/features/premium-calculation.feature) |
| ③ | `generate-sd` | 待執行 | ⏳ | `sdlc/sd/output/SD-LIFE-v1.0.md` + ADR + Task List |
| ④ | `springboot-codegen` | 待執行 | ⏳ | `src/` |
| ⑤ | `test-report` | 待執行 | ⏳ | `sdlc/test/output/TEST-REPORT-LIFE-v1.0.md` |
| ⑥ | `code-review` | 待執行 | ⏳ | Code Review 報告 |
| ⑦ | `markdown-to-word` | 待執行（示範指令） | ⏳ | `FSD-LIFE-v1.0.docx` / `SD-LIFE-v1.0.docx` |

### 關於 ① doc-to-markdown 的驗證方式

本次驗證輸入需求本身即以 Markdown 撰寫（[sdlc/inputs/LIFE-PREMIUM-requirements.md](inputs/LIFE-PREMIUM-requirements.md)），
故無需實際轉檔。`doc-to-markdown` Skill 之可用性以 `.github/skills/doc-to-markdown/scripts/convert.py`
腳本可正確執行 `--help` 且邏輯正確作為驗證基準（詳見下方「Skill 可用性驗證」章節）。

## 驗證結論

（各階段完成後於此處補充執行摘要與遇到的問題／調整）
