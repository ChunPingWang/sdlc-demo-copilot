# FSD Word 套版轉換規範

供 `/markdown-to-word` Skill 轉換 `FSD-*.md` → `FSD-*.docx` 時套用的樣式對應規則。

## 樣式對應

| Markdown 元素 | Word 樣式（reference-doc） |
|---------------|---------------------------|
| `#` 文件標題 | Title |
| `##` 章節標題 | Heading 1 |
| `###` 小節標題 | Heading 2 |
| `####` FR 項目標題 | Heading 3 |
| 表格 | Table Grid（含表頭底色） |
| ` ```mermaid ` 區塊 | 需先轉為圖片（見下方） |
| `[ ]` 待辦清單 | 核取方塊清單樣式 |
| `⚠️ 待確認` 標注 | 反白 / 醒目提示色（黃底） |

## Mermaid 圖表轉換

Pandoc 無法直接渲染 Mermaid，轉換前需：

1. 使用 `mmdc`（[@mermaid-js/mermaid-cli](https://github.com/mermaid-js/mermaid-cli)）
   將 FSD 內每個 ` ```mermaid ` 區塊匯出為 PNG：
   ```bash
   mmdc -i diagram.mmd -o diagram.png -b transparent
   ```
2. 將 Markdown 中的 mermaid 區塊替換為 `![]({diagram}.png)` 圖片參照後，再交給 Pandoc 轉換。

## Pandoc 轉換指令

```bash
pandoc sdlc/fsd/output/FSD-{PROJECT_CODE}-v{VERSION}.md \
  --reference-doc=sdlc/fsd/templates/FSD-template.docx \
  --toc --toc-depth=3 \
  --output=sdlc/fsd/output/FSD-{PROJECT_CODE}-v{VERSION}.docx
```

## 樣板檔案

- `sdlc/fsd/templates/FSD-template.docx` — 公司標準 Word 樣板（頁首/頁尾、字型、配色）
