# PDF 中文字体

PDF 导出尝试从 classpath 加载：

1. `fonts/NotoSansSC-Regular.ttf`
2. `fonts/NotoSansSC-Regular.otf`

仓库内已附带 **Noto Sans SC Regular（TrueType，SIL OFL 1.1）**，许可证见 `OFL.txt`。
注意：PDFBox 2.x（openhtmltopdf 1.0.10 依赖）不支持 CFF 轮廓的 `.otf` 字体，
请优先使用 `glyf` 轮廓的 `.ttf` 文件，否则会回退到 Helvetica，中文可能无法显示。

如需替换字体，直接放置同名的 `.ttf` 即可，无需修改代码。
