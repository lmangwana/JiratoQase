import model.JiraTicket;
import parser.JiraCsvParser;
import qase.QaseCsvbuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import utils.AttachmentExtractor;
import utils.AttachmentMapper;

/**
 * Main entry point to test Jira CSV parsing and validate extracted data.
 */
public class Main {
    public static void main(String[] args) {
        // Update the path to point to your exported Jira CSV file
        String csvPath = "src/main/resources/Sprint31.csv";
        String baseFolder = (args.length > 1)
                ? args[1]
                : Optional.ofNullable(System.getenv("ATTACH_BASE"))
                .orElse(Paths.get(System.getProperty("user.home"), "Downloads").toString());
        JiraCsvParser parser = new JiraCsvParser();

        try {
            List<JiraTicket> tickets = parser.parseCsv(csvPath);

            System.out.println("✅ Parsed " + tickets.size() + " tickets\n");

            // Print a preview of each ticket for verification
            for (JiraTicket ticket : tickets) {
                String title = ticket.getIssueKey() + " " + ticket.getIssueKey();
                System.out.println("🔹 Title: " + title);
                System.out.println("   Description: " + ticket.getDescription());
                System.out.println("  📌 Comments:");
                for (String comment : ticket.getComments()) {
                    System.out.println("     - " + comment);
                }
                System.out.println("------------------------------------------------\n");
            }


            for (JiraTicket ticket : tickets) {
                Set<String> attachments = AttachmentExtractor.extractAttachmentReferences(ticket.getDescription());

                attachments.addAll(ticket.getComments().stream()
                        .flatMap(comment -> AttachmentExtractor.extractAttachmentReferences(comment).stream())
                        .collect(Collectors.toSet()));

                System.out.println("📎 Found attachments for " + ticket.getSummary() + ":");
                if (attachments.isEmpty()) {
                    System.out.println("   ❌ No attachments found");
                } else {
                    attachments.forEach(file -> System.out.println("   - " + file));
                }
            }

            //Mapping Filenames/URLS to local files
            //String baseFolder = "/Users/Users/luthomangwana/Downloads/";

            for (JiraTicket ticket : tickets) {
                Set<String> attachments = AttachmentExtractor.extractAttachmentReferences(ticket.getDescription());

                attachments.addAll(ticket.getComments().stream()
                        .flatMap(comment -> AttachmentExtractor.extractAttachmentReferences(comment).stream())
                        .collect(Collectors.toSet()));

                Set<File> foundFiles = AttachmentMapper.resolveToLocalFiles(attachments, baseFolder);

                System.out.println("📎 Local files found for " + ticket.getIssueKey() + ":");
                if (foundFiles.isEmpty()) {
                    System.out.println("   ❌ None found");
                } else {
                    foundFiles.forEach(file -> System.out.println("   - " + file.getAbsolutePath()));
                }
            }

            // Add this at the end of your main method after printing tickets:
            QaseCsvbuilder exporter = new QaseCsvbuilder();
            String outputPath = "src/main/output/qase-sprint31-upload.csv";
            exporter.writeQaseCsv(tickets, outputPath);
            System.out.println("✅ QASE CSV exported to: " + outputPath);

        } catch (IOException e) {
            System.err.println("❌ Failed to parse Jira CSV: " + e.getMessage());
        }
    }
}