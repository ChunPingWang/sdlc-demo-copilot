# SD Word 套版轉換規範

供 `/markdown-to-word` Skill 轉換 `SD-*.md` → `SD-*.docx` 時套用的樣式對應規則。

## 樣式對應

| Markdown 元素 | Word 樣式（reference-doc） |
|---------------|---------------------------|
| `#` 文件標題 | Title |
| `##` 章節標題 | Heading 1 |
| `###` 小節標題 | Heading 2 |
| 表格 | Table Grid |
| ` ```mermaid ` 區塊 | 先以 `mmdc` 匯出 PNG 再嵌入（同 FSD 規範） |
| ` ```json ` Schema 區塊 | 等寬字型（Consolas / Courier New）程式碼區塊樣式 |

## Pandoc 轉換指令

```bash
pandoc sdlc/sd/output/SD-{PROJECT_CODE}-v{VERSION}.md \
  --reference-doc=sdlc/sd/templates/SD-template.docx \
  --toc --toc-depth=3 \
  --output=sdlc/sd/output/SD-{PROJECT_CODE}-v{VERSION}.docx
```

## 樣板檔案

- `sdlc/sd/templates/SD-template.docx` — 公司標準 Word 樣板
