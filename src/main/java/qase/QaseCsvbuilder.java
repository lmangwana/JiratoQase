package qase;

import model.JiraTicket;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import utils.MarkdownCleaner;
import utils.StepsExtractor;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Converts JiraTicket objects into QASE test case rows and exports as a CSV file.
 */
public class QaseCsvbuilder {

    /**
     * Writes a QASE-compatible CSV file from JiraTicket data.
     *
     * @param tickets       List of Jira tickets
     * @param outputCsvPath Path to write the final QASE CSV file
     * @throws IOException if file cannot be written
     */
    public void writeQaseCsv(List<JiraTicket> tickets, String outputCsvPath) throws IOException {
        try (CSVPrinter printer = new CSVPrinter(
                new FileWriter(outputCsvPath),
                CSVFormat.DEFAULT.withHeader(
                        "title", "description", "steps_actions", "steps_result",
                        "tags", "priority", "automation", "status", "steps_type"
                )
        )) {
            for (JiraTicket ticket : tickets) {
                String title = ticket.getIssueKey() + " " + ticket.getSummary();
                String description = MarkdownCleaner.clean(ticket.getDescription());

                //String description = ticket.getDescription();

                // Placeholder action
                //String stepsActions = "1. \"Verify that " + title + "\"";

                List<String> extractedSteps = StepsExtractor.extractSteps(ticket.getDescription());

                StringBuilder stepsActionsBuilder = new StringBuilder();
                if (!extractedSteps.isEmpty()) {
                    for (int i = 0; i < extractedSteps.size(); i++) {
                        stepsActionsBuilder.append(i + 1).append(". ").append(extractedSteps.get(i)).append("\n");
                    }
                } else {
                    stepsActionsBuilder.append("1. Verify that ").append(title);
                }

                String stepsActions = stepsActionsBuilder.toString().trim();

                // Merge all comments into one block
                StringBuilder stepsResult = new StringBuilder();
                int step = 1;
                for (String comment : ticket.getComments()) {
                    stepsResult.append(step++).append(". ").append(comment.trim()).append("\n");
                }

                // Tags can include sprint, project, module
                String tags = "sprint31, votebud";

                // QASE-required fields
                String priority = "high";
                String automation = "manual";
                String status = "actual";
                String stepsType = "classic";

                printer.printRecord(
                        title,
                        description,
                        stepsActions,
                        stepsResult.toString().trim(),
                        tags,
                        priority,
                        automation,
                        status,
                        stepsType
                );
            }
        }
    }
}