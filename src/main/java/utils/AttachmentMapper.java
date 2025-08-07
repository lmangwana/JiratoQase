package utils;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class AttachmentMapper {

    /**
     * Maps attachment names or URLs to actual local file paths, if found.
     *
     * @param attachmentRefs extracted from description/comments (filenames or URLs)
     * @param baseDirectory local path where all attachment files are stored
     * @return Set of files that exist and match references
     */
    public static Set<File> resolveToLocalFiles(Set<String> attachmentRefs, String baseDirectory) {
        Set<File> foundFiles = new HashSet<>();

        for (String ref : attachmentRefs) {
            // Strip full URL → just get filename
            String fileName = ref.contains("/") ? ref.substring(ref.lastIndexOf("/") + 1) : ref;

            File candidate = new File(baseDirectory, fileName);
            if (candidate.exists() && candidate.isFile()) {
                foundFiles.add(candidate);
            } else {
                System.out.println("⚠️  File not found locally: " + fileName);
            }
        }

        return foundFiles;
    }
}