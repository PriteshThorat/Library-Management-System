package com.library.service;

import com.library.config.EnvConfig;
import com.library.model.Book;
import com.library.util.CsvParser;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class BooksService {
    private static final String HEADER = "id,title,author,isbn,quantity";
    private BooksService() {
    }

    public static List<Book> readAll() throws IOException {
        List<Book> books = new ArrayList<>();
        File file = new File(EnvConfig.getBooksFilename());

        if (!file.exists()) {
            return books;
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
                if (row.length >= 5) {
                    books.add(new Book(
                            row[0], row[1], row[2], row[3], Integer.parseInt(row[4])));
                }
            }
        }

        return books;
    }

    public static Book findById(String id) throws IOException {
        for (Book book : readAll()) {
            if (book.getId().equalsIgnoreCase(id)) {
                return book;
            }
        }
        return null;
    }

    public static void writeAll(List<Book> books) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(EnvConfig.getBooksFilename()))) {
            writer.write(HEADER);
            writer.newLine();

            for (Book book : books) {
                writer.write(row(book.toRow()));
                writer.newLine();
            }
        }
    }

    public static int count() throws IOException {
        return readAll().size();
    }

    private static String row(String[] values) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < values.length; i++) {
            if (i > 0) {
                result.append(',');
            }

            String value = values[i];
            if (value.contains(",") || value.contains("\"")) {
                result.append('"')
                        .append(value.replace("\"", "\"\""))
                        .append('"');
            } else {
                result.append(value);
            }
        }

        return result.toString();
    }
}