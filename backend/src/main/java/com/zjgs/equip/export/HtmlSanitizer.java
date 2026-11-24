package com.zjgs.equip.export;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 轻量 HTML 清洗：仅保留富文本常用标签，输出良构 XHTML，防止脚本/事件属性注入。
 * openhtmltopdf 使用 XML 解析，因此这里同时负责补齐/闭合标签。
 */
public final class HtmlSanitizer {
    private static final Set<String> ALLOWED_TAGS = Set.of(
            "h1", "h2", "h3", "h4", "h5", "h6",
            "p", "br", "hr", "div", "span",
            "strong", "b", "em", "i", "u", "s", "del", "sub", "sup",
            "ul", "ol", "li", "blockquote", "pre", "code",
            "table", "thead", "tbody", "tfoot", "tr", "th", "td",
            "a", "img", "figure", "figcaption");
    private static final Set<String> VOID_TAGS = Set.of("br", "hr", "img");
    private static final Set<String> GLOBAL_ATTRS = Set.of("title");
    private static final Pattern TAG_PATTERN = Pattern.compile("<[^>]*>");
    private static final Pattern TAG_NAME_PATTERN = Pattern.compile("^</?\\s*([a-zA-Z][a-zA-Z0-9]*)");
    private static final Pattern ATTR_PATTERN = Pattern.compile(
            "([a-zA-Z_:][-a-zA-Z0-9_:.]*)\\s*=\\s*(\"([^\"]*)\"|'([^']*)'|([^\\s\"'>/]+))");

    private HtmlSanitizer() {
    }

    public static String sanitize(String html) {
        if (html == null || html.isBlank()) {
            return "";
        }
        String cleaned = html
                .replaceAll("(?is)<(script|style|iframe|object|embed|link|meta|form)[^>]*>.*?</\\1\\s*>", "")
                .replaceAll("(?is)</?(script|style|iframe|object|embed|link|meta|form)[^>]*>", "");

        StringBuilder out = new StringBuilder(cleaned.length() + 32);
        Deque<String> stack = new ArrayDeque<>();
        Matcher matcher = TAG_PATTERN.matcher(cleaned);
        int last = 0;
        while (matcher.find()) {
            out.append(escape(cleaned.substring(last, matcher.start())));
            appendTag(matcher.group(), out, stack);
            last = matcher.end();
        }
        out.append(escape(cleaned.substring(last)));
        while (!stack.isEmpty()) {
            out.append("</").append(stack.pop()).append('>');
        }
        return out.toString();
    }

    private static void appendTag(String rawTag, StringBuilder out, Deque<String> stack) {
        Matcher nameMatcher = TAG_NAME_PATTERN.matcher(rawTag);
        if (!nameMatcher.find()) {
            out.append(escape(rawTag));
            return;
        }
        String name = nameMatcher.group(1).toLowerCase(Locale.ROOT);
        boolean closing = rawTag.startsWith("</");
        if (!ALLOWED_TAGS.contains(name)) {
            out.append(escape(rawTag));
            return;
        }
        if (closing) {
            // 只关闭仍在栈中的标签，忽略多余闭合标签，保证 XHTML 良构。
            if (!stack.contains(name)) {
                return;
            }
            while (!stack.isEmpty()) {
                String open = stack.pop();
                out.append("</").append(open).append('>');
                if (open.equals(name)) {
                    break;
                }
            }
            return;
        }

        String attrs = buildAttributes(name, rawTag, nameMatcher.end());
        out.append('<').append(name).append(attrs);
        boolean selfClosing = rawTag.endsWith("/>");
        if (VOID_TAGS.contains(name) || selfClosing) {
            out.append("/>");
            return;
        }
        out.append('>');
        stack.push(name);
    }

    private static String buildAttributes(String tagName, String rawTag, int nameEnd) {
        int end = rawTag.endsWith("/>") ? rawTag.length() - 2 : rawTag.length() - 1;
        if (nameEnd >= end) {
            return "";
        }
        String attrText = rawTag.substring(nameEnd, end);
        StringBuilder attrs = new StringBuilder();
        Matcher matcher = ATTR_PATTERN.matcher(attrText);
        while (matcher.find()) {
            String key = matcher.group(1).toLowerCase(Locale.ROOT);
            String value = matcher.group(3) != null ? matcher.group(3)
                    : matcher.group(4) != null ? matcher.group(4) : matcher.group(5);
            if (value == null || key.startsWith("on") || !isAllowedAttribute(tagName, key)) {
                continue;
            }
            if (("href".equals(key) && !isSafeLink(value))
                    || ("src".equals(key) && !isSafeImage(value))) {
                continue;
            }
            attrs.append(' ').append(key).append("=\"").append(escapeAttribute(value)).append('"');
        }
        return attrs.toString();
    }

    private static boolean isAllowedAttribute(String tagName, String key) {
        if (GLOBAL_ATTRS.contains(key)) {
            return true;
        }
        return switch (tagName) {
            case "a" -> "href".equals(key) || "target".equals(key);
            case "img" -> "src".equals(key) || "alt".equals(key) || "width".equals(key) || "height".equals(key);
            case "td", "th" -> "colspan".equals(key) || "rowspan".equals(key) || "align".equals(key);
            default -> false;
        };
    }

    private static boolean isSafeLink(String value) {
        String url = value.trim().toLowerCase(Locale.ROOT);
        return url.startsWith("http://") || url.startsWith("https://") || url.startsWith("mailto:")
                || url.startsWith("/") || url.startsWith("#");
    }

    private static boolean isSafeImage(String value) {
        String url = value.trim().toLowerCase(Locale.ROOT);
        return url.startsWith("http://") || url.startsWith("https://")
                || url.startsWith("data:image/png;base64,") || url.startsWith("data:image/jpeg;base64,")
                || url.startsWith("data:image/gif;base64,") || url.startsWith("data:image/webp;base64,")
                || url.startsWith("/");
    }

    public static String escape(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private static String escapeAttribute(String text) {
        return escape(text);
    }
}
