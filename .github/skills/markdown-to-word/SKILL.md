---
name: markdown-to-word
description: '使用 Pandoc 套用公司 Word 樣板，將 sdlc/fsd 或 sdlc/sd 下的 Markdown 文件轉為可交付的 .docx 檔案，全程本地端執行。Use when the user asks to export FSD/SD to Word, convert markdown to docx, or produce a deliverable document with the company template.'
argument-hint: '#sdlc/fsd/output/FSD-{CODE}-v{N}.md'
---

# markdown-to-word — Word 套版輸出

## 概述

使用 Pandoc + reference-doc 機制，套用公司 Word 樣板，產出可直接發送審查的 `.docx` 文件，
全程**本地端**執行（不上傳雲端 LLM），保護文件內容機密性。

## 輸入 / 輸出

| 文件類型 | 輸入 | 樣板 | 輸出 |
|---------|------|------|------|
| FSD | `sdlc/fsd/output/FSD-{CODE}-v{N}.md` | `sdlc/fsd/templates/FSD-template.docx` | `FSD-{CODE}-v{N}.docx` |
| SD | `sdlc/sd/output/SD-{CODE}-v{N}.md` | `sdlc/sd/templates/SD-template.docx` | `SD-{CODE}-v{N}.docx` |

## 執行步驟

1. 確認輸入 Markdown 是否包含 ` ```mermaid ` 圖表區塊；若有，先用
   [@mermaid-js/mermaid-cli](https://github.com/mermaid-js/mermaid-cli) 匯出為 PNG 並替換為圖片參照
   （詳見對應 Skill 的 word-style-guide：`generate-fsd/references/FSD-word-style-guide.md`
   或 `generate-sd/references/SD-word-style-guide.md`）。
2. 確認樣板檔案存在（`sdlc/fsd/templates/FSD-template.docx` 或 `sdlc/sd/templates/SD-template.docx`）；
   若不存在，提示使用者提供公司樣板。
3. 執行 Pandoc 轉換：
   ```bash
   pandoc {輸入.md} \
     --reference-doc={對應樣板.docx} \
     --toc --toc-depth=3 \
     --output={輸出.docx}
   ```
4. 確認輸出檔案已產生，回報檔案路徑與頁數/章節數摘要。

## 品質檢查清單

- [ ] Mermaid 圖表已轉為圖片並正確嵌入
- [ ] 目錄（TOC）已產生且階層正確
- [ ] 表格樣式套用公司樣板（Table Grid + 表頭底色）
- [ ] 轉換全程於本機執行，未經雲端 API 上傳原始內容

## 參考資源

- `sdlc/fsd/templates/FSD-template.docx`、`sdlc/sd/templates/SD-template.docx` — 公司 Word 樣板
- `.github/skills/generate-fsd/references/FSD-word-style-guide.md`
- `.github/skills/generate-sd/references/SD-word-style-guide.md`
