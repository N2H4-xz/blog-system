package com.blog.backend.support;

public final class TextSummaryUtils {
    private TextSummaryUtils() {
    }

    public static String summarize(String markdown) {
        String plain = markdown == null ? "" : markdown
                .replaceAll("`{1,3}.*?`{1,3}", " ")
                .replaceAll("#+\\s*", "")
                .replaceAll("[*_>\\-]", " ")
                .replaceAll("\\[(.*?)]\\((.*?)\\)", "$1")
                .replaceAll("\\s+", " ")
                .trim();
        if (plain.length() <= 140) {
            return plain;
        }
        return plain.substring(0, 140) + "...";
    }
}
