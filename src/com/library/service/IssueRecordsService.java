package com.library.service;

import com.library.config.EnvConfig;
import com.library.model.IssueRecord;
import com.library.util.CsvParser;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public final class IssueRecordsService {
    private static final String HEADER = "id,book_id,member_id,issue_date,due_date,return_date";
    private IssueRecordsService() {
    }

    public static List<IssueRecord> readAll() throws IOException {
        List<IssueRecord> records = new ArrayList<>();
        File file = new File(EnvConfig.getIssueRecordsFilename());

        if (!file.exists()) {
            return records;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean header = true;

            while ((line = reader.readLine()) != null) {
                if (header) {
                    header = false;
                    continue;
                }

                String[] row = CsvParser.parseLine(line);
                if (row.length >= 6) {
                    records.add(new IssueRecord(
                            row[0], row[1], row[2], row[3], row[4], row[5]));
                }
            }
        }

        return records;
    }

    public static IssueRecord activeForBook(String bookId) throws IOException {
        for (IssueRecord record : readAll()) {
            if (record.getBookId().equalsIgnoreCase(bookId)
                    && record.getReturnDate().equalsIgnoreCase("Not_Returned")) {
                return record;
            }
        }
        return null;
    }

    public static boolean isBookIssued(String bookId) throws IOException {
        return activeForBook(bookId) != null;
    }

    public static void writeAll(List<IssueRecord> records) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(EnvConfig.getIssueRecordsFilename()))) {
            writer.write(HEADER);
            writer.newLine();

            for (IssueRecord record : records) {
                writer.write(row(record.toRow()));
                writer.newLine();
            }
        }
    }

    public static int count() throws IOException {
        return readAll().size();
    }

    public static int overdueCount() throws IOException {
        int count = 0;

        for (IssueRecord record : readAll()) {
            boolean notReturned = record.getReturnDate().equals("Not_Returned");
            boolean pastDueDate = LocalDate.now().isAfter(
                    LocalDate.parse(record.getDueDate()));

            if (notReturned && pastDueDate) {
                count++;
            }
        }

        return count;
    }

    public static String nextId() throws IOException {
        return String.format("I%03d", readAll().size() + 1);
    }

    private static String row(String[] values) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < values.length; i++) {
            if (i > 0) {
                result.append(',');
            }
            result.append(values[i]);
        }

        return result.toString();
    }
}