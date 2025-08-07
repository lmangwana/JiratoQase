package utils;

import io.qase.client.v1.ApiClient;
import io.qase.client.v1.api.RunsApi;
import io.qase.client.v1.models.IdResponse;
import io.qase.client.v1.models.RunCreate;
import io.qase.client.v1.models.RunResponse;

import java.nio.file.Files;
import java.nio.file.Path;

import static io.qase.api.utils.TestUtils.QASE_API_TOKEN;

public class QaseRunCreator {

    private static final String PROJECT_CODE = "VOTEBUD";
    private static final String C = System.getenv("QASE_API_TOKEN");

    public static void main(String[] args) {
        try {
            ApiClient client = new ApiClient();
            client.setApiKey(C);
            client.setBasePath("https://api.qase.io/v1");

            RunsApi runsApi = new RunsApi(client);

            // Build the run
            RunCreate run = new RunCreate();
            run.setTitle("Sprint 31 – Upload Test Run"); // Customize title
            run.setDescription("Automated test result upload for Sprint 31");
            //run.setSuiteId(83);

            // Create the run
            IdResponse response = runsApi.createRun(PROJECT_CODE, run);

            if (Boolean.TRUE.equals(response.getStatus())) {
                Integer runId = response.getResult().getId().intValue();
                System.out.println("✅ Qase Run Created with ID: " + runId);
                Files.writeString(Path.of("qase-run-id.txt"), String.valueOf(runId)); // 👈 write to file
            } else {
                System.out.println("⚠️ Qase Run creation returned no ID.");
            }

        } catch (Exception e) {
            System.err.println("❌ Failed to create Qase Run:");
            e.printStackTrace();
        }
    }
}