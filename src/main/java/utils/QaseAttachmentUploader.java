package utils;

import io.qase.api.*;
import io.qase.client.ApiException;
import io.qase.client.model.AttachmentHash;
import io.qase.client.v1.ApiClient;
import io.qase.client.v1.api.AttachmentsApi;
import io.qase.client.v1.models.AttachmentUploadsResponse;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QaseAttachmentUploader {

    private final AttachmentsApi attachmentsApi;

    public QaseAttachmentUploader(String qaseToken) {
        ApiClient client = new ApiClient();
        client.setApiKey(qaseToken);
        this.attachmentsApi = new AttachmentsApi(client);
    }

    /**
     * Uploads all provided files to QASE and returns a list of hashes
     *
     * @param files list of files to upload
     * @return list of QASE hashes
     */
    public List<String> uploadFiles(List<File> files, String projectCode) {
        List<String> hashes = new ArrayList<>();

        for (File file : files) {
            try {
                List<File> singleFileList = Collections.singletonList(file);

                // Upload and get the response
                AttachmentUploadsResponse response = attachmentsApi.uploadAttachment(projectCode, singleFileList);

                // Extract hash from first result item (assuming one file uploaded)
                if (response.getResult() != null && !response.getResult().isEmpty()) {
                    String hash = response.getResult().get(0).getHash();
                    hashes.add(hash);
                    System.out.println("✅ Uploaded: " + file.getName() + " | Hash: " + hash);
                } else {
                    System.out.println("⚠️ No hash returned for: " + file.getName());
                }

            } catch (io.qase.client.v1.ApiException e) {
                System.out.println("❌ Failed to upload " + file.getName() + ": " + e.getMessage());
            }
        }

        return hashes;
    }
}