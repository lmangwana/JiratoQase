package utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Cleans Jira/Atlassian-style markup from text to make it readable in QASE or Markdown.
 */
public class MarkdownCleaner {

    /**
     * Cleans a block of text by removing Atlassian/Jira-specific markup.
     *
     * @param input raw Jira description, comment, or step
     * @return cleaned and human-readable text
     */
    public static String clean(String input) {
        if (input == null || input.trim().isEmpty()) return "";

        String output = input;

        // 1. Remove [~accountid:xyz]
        output = output.replaceAll("\\[~accountid:[^\\]]+\\]", "");

        // 2. Convert [Label|http://link] → [Label](http://link)
        output = output.replaceAll("\\[([^\\]|]+)\\|([^\\]]+)\\]", "[$1]($2)");

        // 3. Convert [^filename] → (Attachment: filename)
        output = output.replaceAll("\\[\\^([\\w\\-. ]+)\\]", "(Attachment: $1)");

        // 4. Remove excessive asterisks but keep readable formatting
        output = output.replaceAll("\\*{1,3}([^*]+)\\*{1,3}", "$1"); // *bold* → bold

        // 5. Remove Atlassian image tags (e.g. !filename.png|width=100,height=100!)
        output = output.replaceAll("!([^!|]+)(\\|[^!]*)?!", "(Image: $1)");

        // 6. Collapse repeated newlines
        output = output.replaceAll("(\\n\\s*){3,}", "\n\n");

        return output.trim();
    }
}