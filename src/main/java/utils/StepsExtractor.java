package utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Extracts "Steps to Reproduce" from Jira-style descriptions or comments.
 */
public class StepsExtractor {

    /**
     * Tries to extract a clean list of steps from the given text.
     *
     * @param description the raw Jira description
     * @return a list of steps (e.g., ["Navigate to page", "Click button"])
     */
    public static List<String> extractSteps(String description) {
        List<String> steps = new ArrayList<>();
        if (description == null || description.isEmpty()) return steps;

        // Normalize line endings
        String[] lines = description.split("\\r?\\n");

        boolean insideStepsSection = false;

        for (String line : lines) {
            line = line.trim();

            // Detect "Steps to Reproduce" header
            if (line.toLowerCase().contains("steps to reproduce")) {
                insideStepsSection = true;
                continue;
            }

            if (insideStepsSection) {
                // Stop if we hit another section (e.g. "Expected Result")
                if (line.toLowerCase().startsWith("*expected") || line.toLowerCase().startsWith("expected")) {
                    break;
                }

                // Extract actionable step lines
                if (line.startsWith("#") || line.startsWith("*") || line.matches("^\\d+\\.\\s+.*")) {
                    // Clean and strip prefix
                    String cleaned = line.replaceFirst("^(#|\\*|\\d+\\.)\\s*", "").trim();
                    if (!cleaned.isEmpty()) {
                        steps.add(MarkdownCleaner.clean(cleaned));
                    }
                }
            }
        }

        return steps;
    }
}