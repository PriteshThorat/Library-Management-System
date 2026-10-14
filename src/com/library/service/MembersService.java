package com.library.service;

import com.library.config.EnvConfig;
import com.library.model.Member;
import com.library.util.CsvParser;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class MembersService {
    private static final String HEADER = "id,name,email,phone";
    private MembersService() {
    }

    public static List<Member> readAll() throws IOException {
        List<Member> members = new ArrayList<>();
        File file = new File(EnvConfig.getMembersFilename());

        if (!file.exists()) {
            return members;
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
                if (row.length >= 4) {
                    members.add(new Member(row[0], row[1], row[2], row[3]));
                }
            }
        }

        return members;
    }

    public static Member findById(String id) throws IOException {
        for (Member member : readAll()) {
            if (member.getId().equalsIgnoreCase(id)) {
                return member;
            }
        }
        return null;
    }

    public static void writeAll(List<Member> members) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(EnvConfig.getMembersFilename()))) {
            writer.write(HEADER);
            writer.newLine();

            for (Member member : members) {
                writer.write(row(member.toRow()));
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