
package utils;

import io.qase.client.v1.ApiClient;
import io.qase.client.v1.api.AttachmentsApi;
import io.qase.client.v1.api.ResultsApi;
import io.qase.client.v1.models.AttachmentUploadsResponse;
import io.qase.client.v1.models.Attachmentupload;
import io.qase.client.v1.models.ResultCreate;
import io.qase.client.v1.models.ResultCreateResponse;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class QaseResultUploader {

    private static final String PROJECT_CODE = "VOTEBUD";
    private static final int RUN_ID;

    static {
        try {
            RUN_ID = Integer.parseInt(Files.readString(Path.of("qase-run-id.txt")).trim());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    ;

    // Fetch from env for security (recommended)
    private static final String QASE_API_TOKEN = System.getenv("QASE_API_TOKEN");
    public static void uploadResultWithAttachments(String title, String status, List<File> filesToUpload) {
        try {
            ApiClient client = new ApiClient();

            client.setApiKey(QASE_API_TOKEN);
            client.setBasePath("https://api.qase.io/v1");

            AttachmentsApi attachmentsApi = new AttachmentsApi(client);
            ResultsApi resultsApi = new ResultsApi(client);

            List<String> hashes = null;
            if (filesToUpload != null && !filesToUpload.isEmpty()) {
                List<File> existingFiles = filesToUpload.stream()
                        .filter(File::exists)
                        .collect(Collectors.toList());

                if (!existingFiles.isEmpty()) {
                    AttachmentUploadsResponse uploadResponse = attachmentsApi.uploadAttachment(PROJECT_CODE, existingFiles);
                    if (uploadResponse.getResult() != null) {
                        hashes = uploadResponse.getResult().stream()
                                .map(Attachmentupload::getHash)
                                .collect(Collectors.toList());
                    }
                }
            }

            ResultCreate result = new ResultCreate();
            result.status(status);

            if (hashes != null && !hashes.isEmpty()) {
                result.setAttachments(hashes);
            }

            ResultCreateResponse response = resultsApi.createResult(PROJECT_CODE, RUN_ID, result);
            if (Boolean.TRUE.equals(response.getStatus())) {
                System.out.println("✅ Result uploaded to Qase successfully.");
            } else {
                System.out.println("⚠️ Result upload may have failed. No confirmation from Qase.");
            }
        } catch (Exception e) {
            System.err.println("❌ Failed to upload result to Qase:");
            e.printStackTrace();
        }
    }

//    public static void main(String[] args) {
//        // 🔒 Replace with a valid Qase Run ID that is currently active
//        // or move RUN_ID to be passed dynamically if needed
//
//        // 📎 Create test attachment (make sure the file exists!)
//        File testFile = new File("screenshots/test-failure.png"); // adjust path if needed
//
//        if (!testFile.exists()) {
//            System.err.println("❌ Test file not found: " + testFile.getAbsolutePath());
//            return;
//        }
//
//        // 🧪 Call the upload method
//        uploadResultWithAttachments(
//                "Sample upload test from Java",
//                "passed", // OR "failed"
//                List.of(testFile)
//        );
//    }
}