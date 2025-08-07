import model.JiraTicket;
import parser.JiraCsvParser;
import qase.QaseCsvbuilder;
import utils.AttachmentExtractor;
import utils.AttachmentMapper;
import utils.QaseResultUploader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Main entry point: parse Jira CSV, preview, resolve attachments,
 * export Qase-ready CSV, and upload results+attachments to Qase.
 */
public class Main {
    public static void main(String[] args) {
        // 1) Inputs (CLI/env fallbacks)
        String csvPath = (args.length > 0) ? args[0] : "src/main/resources/Sprint31.csv";
        String baseFolder = (args.length > 1)
                ? args[1]
                : Optional.ofNullable(System.getenv("ATTACH_BASE"))
                .orElse(Paths.get(System.getProperty("user.home"), "Downloads").toString());

        JiraCsvParser parser = new JiraCsvParser();

        try {
            List<JiraTicket> tickets = parser.parseCsv(csvPath);
            System.out.println("✅ Parsed " + tickets.size() + " tickets\n");

            // 2) Preview tickets (sanity check)
            for (JiraTicket ticket : tickets) {
                String title = ticket.getIssueKey() + " " + ticket.getSummary(); // fixed
                System.out.println("🔹 Title: " + title);
                System.out.println("   Description: " + ticket.getDescription());
                System.out.println("  📌 Comments:");
                for (String comment : ticket.getComments()) {
                    System.out.println("     - " + comment);
                }
                System.out.println("------------------------------------------------\n");
            }

            // 3) Resolve attachment references & map to local files (skip missing)
            Map<JiraTicket, Set<File>> ticketFiles = new LinkedHashMap<>();

            for (JiraTicket ticket : tickets) {
                Set<String> refs = new LinkedHashSet<>();
                refs.addAll(AttachmentExtractor.extractAttachmentReferences(ticket.getDescription()));
                refs.addAll(ticket.getComments().stream()
                        .flatMap(c -> AttachmentExtractor.extractAttachmentReferences(c).stream())
                        .collect(Collectors.toSet()));

                Set<File> foundFiles = AttachmentMapper.resolveToLocalFiles(refs, baseFolder);
                ticketFiles.put(ticket, foundFiles);

                System.out.println("📎 Local files found for " + ticket.getIssueKey() + ":");
                if (foundFiles.isEmpty()) {
                    System.out.println("   ❌ None found");
                } else {
                    foundFiles.forEach(f -> System.out.println("   - " + f.getAbsolutePath()));
                }
            }

            // 4) Export a Qase-ready CSV snapshot (for archival/import if needed)
            QaseCsvbuilder exporter = new QaseCsvbuilder();
            String outputPath = "src/main/output/qase-sprint31-upload.csv";
            exporter.writeQaseCsv(tickets, outputPath);
            System.out.println("✅ QASE CSV exported to: " + outputPath);

            // 5) Upload results + attachments to Qase (uses run id from qase-run-id.txt inside QaseResultUploader)
            System.out.println("\n🚀 Uploading results to Qase...");
            for (JiraTicket ticket : tickets) {
                String title = ticket.getIssueKey() + " " + ticket.getSummary();
                // Simple status heuristic: mark passed if any comment has a ✅ or the word "Pass"; else failed
                String status = ticket.getComments().stream().anyMatch(c ->
                        c.contains("✅") || c.toLowerCase().contains("pass")) ? "passed" : "failed";

                List<File> files = new ArrayList<>(ticketFiles.getOrDefault(ticket, Collections.emptySet()));
                // QaseResultUploader will silently skip if list is empty or files don't exist
                QaseResultUploader.uploadResultWithAttachments(title, status, files);
            }
            System.out.println("🎉 Done. Check your Qase run for uploaded results.");

        } catch (IOException e) {
            System.err.println("❌ Failed to parse Jira CSV: " + e.getMessage());
        }
    }
}