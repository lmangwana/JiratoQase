package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single ticket (issue) exported from Jira.
 * Holds essential fields needed to transform into a QASE-compatible test case.
 */
public class JiraTicket {
    private String issueKey;
    private String summary;
    private String description;
    private List<String> comments = new ArrayList<>();
    private List<String> attachments = new ArrayList<>();

    /**
     * Constructs a JiraTicket with basic fields.
     *
     * @param issueKey    Jira issue key (e.g. "IV-1796")
     * @param summary     Jira summary/title of the ticket
     * @param description Jira description field, often contains UAC and environment info
     */
    public JiraTicket(String issueKey, String summary, String description) {
        this.issueKey = issueKey;
        this.summary = summary;
        this.description = description;
    }

    /** @return the Jira issue key */
    public String getIssueKey() {
        return issueKey;
    }

    /** @return the Jira summary (title) */
    public String getSummary() {
        return summary;
    }

    /** @return the full Jira description */
    public String getDescription() {
        return description;
    }

    /** @return the list of all extracted Jira comments */
    public List<String> getComments() {
        return comments;
    }

    /** @return the list of attachment references or file names */
    public List<String> getAttachments() {
        return attachments;
    }

    /**
     * Adds a new comment to the ticket.
     *
     * @param comment Raw comment text extracted from a Jira row
     */
    public void addComment(String comment) {
        if (comment != null && !comment.trim().isEmpty()) {
            this.comments.add(comment);
        }
    }

    /**
     * Adds a new attachment reference (usually a filename or inline Jira media link).
     *
     * @param attachment Media reference (e.g., screenshots, screen recordings)
     */
    public void addAttachment(String attachment) {
        if (attachment != null && !attachment.trim().isEmpty()) {
            this.attachments.add(attachment);
        }
    }
}