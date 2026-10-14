package com.library.config;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public final class FileCreation {
    private FileCreation() { }

    public static void createBooksFile() throws IOException {
        create(EnvConfig.getBooksFilename(), "id,title,author,isbn,quantity");
    }

    public static void createMembersFile() throws IOException {
        create(EnvConfig.getMembersFilename(), "id,name,email,phone");
    }

    public static void createIssueRecordsFile() throws IOException {
        create(EnvConfig.getIssueRecordsFilename(),
                "id,book_id,member_id,issue_date,due_date,return_date");
    }

    private static void create(String filename, String header) throws IOException {
        if (filename.isEmpty()) throw new IOException("Missing filename in .env");
        File file = new File(filename);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            throw new IOException("Could not create " + parent);
        }

        if (!file.exists()) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(header);
                writer.newLine();
            }
        }
    }
}