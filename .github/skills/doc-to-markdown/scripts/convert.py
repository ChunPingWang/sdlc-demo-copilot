#!/usr/bin/env python3
"""將 sdlc/inputs/raw/ 下的 PDF/Word/Excel/PPT 檔案在本地端批次轉換為 Markdown。

用法：
    python convert.py <input-file-or-dir> [--out-dir sdlc/inputs]

轉換策略：
    - .pdf            -> docling（表格/標題結構萃取）
    - .docx/.xlsx/.pptx -> markitdown（速度快、Office 結構保留良好）

本腳本僅示範轉換流程骨架；實際 Docling / MarkItDown 套件需另行安裝：
    pip install docling markitdown
"""
import argparse
import sys
from pathlib import Path

SUPPORTED = {".pdf", ".docx", ".xlsx", ".pptx"}


def convert_pdf(src: Path, out_dir: Path) -> Path:
    from docling.document_converter import DocumentConverter

    result = DocumentConverter().convert(str(src))
    markdown = result.document.export_to_markdown()
    dest = out_dir / f"{src.stem}.md"
    dest.write_text(markdown, encoding="utf-8")
    return dest


def convert_office(src: Path, out_dir: Path) -> Path:
    from markitdown import MarkItDown

    result = MarkItDown().convert(str(src))
    dest = out_dir / f"{src.stem}.md"
    dest.write_text(result.text_content, encoding="utf-8")
    return dest


def convert_one(src: Path, out_dir: Path) -> Path:
    if src.suffix.lower() == ".pdf":
        return convert_pdf(src, out_dir)
    if src.suffix.lower() in {".docx", ".xlsx", ".pptx"}:
        return convert_office(src, out_dir)
    raise ValueError(f"不支援的檔案類型：{src.suffix}")


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("input", type=Path, help="輸入檔案或資料夾")
    parser.add_argument("--out-dir", type=Path, default=Path("sdlc/inputs"))
    args = parser.parse_args()

    args.out_dir.mkdir(parents=True, exist_ok=True)
    targets = [args.input] if args.input.is_file() else sorted(
        p for p in args.input.rglob("*") if p.suffix.lower() in SUPPORTED
    )
    if not targets:
        print(f"找不到可轉換的檔案：{args.input}", file=sys.stderr)
        return 1

    for src in targets:
        dest = convert_one(src, args.out_dir)
        print(f"✅ {src} -> {dest}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
