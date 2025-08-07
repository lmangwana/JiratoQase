package parser;

import model.JiraTicket;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * Parses a Jira-exported CSV file and builds a list of JiraTicket objects.
 * Handles multiple comment columns and basic field validation.
 */
public class JiraCsvParser {

    /**
     * Parses the input Jira CSV and returns a list of structured JiraTicket objects.
     *
     * @param csvFilePath path to the exported Jira CSV file
     * @return List of JiraTicket objects
     * @throws IOException if the file is not found or unreadable
     */
    public List<JiraTicket> parseCsv(String csvFilePath) throws IOException {
        List<JiraTicket> tickets = new ArrayList<>();

        try (Reader reader = new FileReader(csvFilePath);
             CSVParser csvParser = CSVFormat.DEFAULT
                     .withFirstRecordAsHeader()
                     .withIgnoreEmptyLines()
                     .parse(reader)) {

            System.out.println("✅ Headers Detected in File:");
            List<String> headers = csvParser.getHeaderNames();
            headers.forEach(System.out::println);


            // 🔍 Precompute all column indexes whose headers start with "Comments"
            List<Integer> commentColumnIndexes = new ArrayList<>();
            for (int i = 0; i < headers.size(); i++) {
                String header = headers.get(i).toLowerCase();
                if (header.startsWith("comments")) {
                    commentColumnIndexes.add(i);
                }
            }

            // 🔁 Parse each CSV record and collect fields
            for (CSVRecord record : csvParser) {
                String issueKey = record.get("Issue key").trim();
                String summary = record.get("Summary").trim();
                String description = record.get("Description").trim();

                JiraTicket ticket = new JiraTicket(issueKey, summary, description);

                // ✅ Add all comment fields from known "Comments" indexes
                for (int index : commentColumnIndexes) {
                    String value = record.get(index);
                    if (value != null && !value.trim().isEmpty() && !ticket.getComments().contains(value.trim())) {
                        ticket.addComment(value.trim());
                    }
                }

                // You could optionally detect attachments in future here

                tickets.add(ticket);
            }
        }

        return tickets;
    }
}