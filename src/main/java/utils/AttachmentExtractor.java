package utils;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Extracts media filenames and Jira attachment URLs from descriptions and comments.
 */
public class AttachmentExtractor {

    // Matches !filename.ext|...!
    private static final Pattern EXCLAMATION_PATTERN =
            Pattern.compile("!([^|\\n\\r]+\\.(png|jpg|jpeg|mov|mp4|gif|mp3|pdf))[^!]*!");

    // Matches [^filename.ext]
    private static final Pattern BRACKETS_PATTERN =
            Pattern.compile("\\[\\^([^\\]]+\\.(png|jpg|jpeg|mov|mp4|gif|mp3|pdf))]");

    // Matches Jira-hosted attachment URLs
    private static final Pattern URL_PATTERN =
            Pattern.compile("https://[^\\s]+/attachment/content/\\d+");

    /**
     * Extracts all embedded media filenames and Jira-hosted URLs from the text.
     *
     * @param text Jira description or comment
     * @return Set of unique filenames and/or URLs
     */
    public static Set<String> extractAttachmentReferences(String text) {
        Set<String> attachments = new HashSet<>();
        if (text == null || text.isEmpty()) return attachments;

        Matcher exclamationMatcher = EXCLAMATION_PATTERN.matcher(text);
        while (exclamationMatcher.find()) {
            attachments.add(exclamationMatcher.group(1).trim());
        }

        Matcher bracketsMatcher = BRACKETS_PATTERN.matcher(text);
        while (bracketsMatcher.find()) {
            attachments.add(bracketsMatcher.group(1).trim());
        }

        Matcher urlMatcher = URL_PATTERN.matcher(text);
        while (urlMatcher.find()) {
            attachments.add(urlMatcher.group().trim());
        }

        return attachments;
    }
}
