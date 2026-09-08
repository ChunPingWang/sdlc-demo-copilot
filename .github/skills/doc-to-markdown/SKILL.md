---
name: doc-to-markdown
description: '將 PDF、Word、Excel、PowerPoint 等原始文件在本地端轉換為 Markdown，供後續 generate-fsd 等 Skill 使用，全程不上雲端以保護文件機密性並降低 token 消耗。Use when the user provides raw PDF/Word/Excel/PPT files and asks to convert them to Markdown before generating FSD/SD documents.'
argument-hint: '#sdlc/inputs/raw/檔案.pdf'
---

# doc-to-markdown — 前置文件轉換

## 概述

企業文件往往以 PDF、Word、Excel 形式交付。本 Skill 在**本地端**將各種格式轉換為 Markdown，
再交由 `generate-fsd` 等後續 Skill 處理，目的：

1. **降低 token 消耗**：後續 Skill 只需讀取精簡的 Markdown，而非原始二進位文件。
2. **保護文件機密性**：轉換全程在本機執行，原始文件不上傳雲端 LLM。

## 輸入 / 輸出

- 輸入：`sdlc/inputs/raw/*.{pdf,docx,xlsx,pptx}`
- 輸出：`sdlc/inputs/*.md`

## 轉換工具選擇

| 文件類型 | 建議工具 | 原因 |
|---------|---------|------|
| PDF（有文字層） | [Docling](https://github.com/DS4SD/docling) | 表格、標題結構萃取最佳 |
| PDF（掃描件） | Docling + OCR | 內建 EasyOCR pipeline |
| Word / Excel / PPT | [MarkItDown](https://github.com/microsoft/markitdown) | 速度快，Office 結構保留良好 |
| 批次多格式 | [scripts/convert.py](./scripts/convert.py) | 自動依副檔名判斷工具並批次轉換 |

## 執行步驟

1. 掃描 `sdlc/inputs/raw/` 下所有待轉換檔案，依副檔名分類。
2. 呼叫 [scripts/convert.py](./scripts/convert.py)（或對應 CLI）執行本地轉換：
   ```bash
   python .github/skills/doc-to-markdown/scripts/convert.py sdlc/inputs/raw/需求訪談紀錄.pdf
   ```
3. 轉換後的 Markdown 存至 `sdlc/inputs/{原檔名}.md`，保留原始標題階層與表格結構。
4. 檢查轉換結果：
   - 表格是否正確轉為 Markdown Table
   - 標題階層（`#`/`##`/`###`）是否對應原文件章節
   - 若為掃描件 OCR，人工抽樣檢查辨識準確度
5. 回報已產出的 Markdown 檔案清單，提示使用者可接續執行 `/generate-fsd #sdlc/inputs/{檔名}.md`。

## 範例

```
/doc-to-markdown #sdlc/inputs/raw/需求訪談紀錄.pdf
```

## 參考資源

- [scripts/convert.py](./scripts/convert.py) — 依副檔名自動選擇 Docling / MarkItDown 的批次轉換腳本
- 輸出目錄：`sdlc/inputs/`
