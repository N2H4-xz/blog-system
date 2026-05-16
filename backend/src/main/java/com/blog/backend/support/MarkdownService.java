package com.blog.backend.support;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import java.util.Objects;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Component;

@Component
public class MarkdownService {
    private final Parser parser = Parser.builder().build();
    private final HtmlRenderer renderer = HtmlRenderer.builder().build();

    public String render(String markdown) {
        String safeMarkdown = Objects.requireNonNullElse(markdown, "");
        String html = renderer.render(parser.parse(safeMarkdown));
        return Jsoup.clean(html, Safelist.relaxed().addTags("hr", "pre", "code"));
    }
}
