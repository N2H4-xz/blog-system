package com.blog.backend.support;

import java.text.Normalizer;
import java.util.Locale;

public final class SlugUtils {
    private SlugUtils() {
    }

    public static String toSlug(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^\\p{IsAlphabetic}\\p{IsDigit}]+", "-")
                .replaceAll("(^-|-$)", "");
        return normalized.isBlank() ? "post" : normalized;
    }
}
