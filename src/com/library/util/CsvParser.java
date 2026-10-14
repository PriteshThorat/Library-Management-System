package com.library.util;

import java.util.ArrayList;
import java.util.List;

public final class CsvParser {
    private CsvParser() { }

    public static String[] parseLine(String line) {
        if (line == null || line.isEmpty()) {
            return new String[0];
        }

        List<String> values = new ArrayList<>();
        StringBuilder value = new StringBuilder();
        boolean inQuotes = false;
        boolean quoted = false;

        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);

            if (ch == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    value.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
                quoted = true;
            } else if (ch == ',' && !inQuotes) {
                values.add(quoted
                        ? value.toString()
                        : value.toString().trim());
                value.setLength(0);
                quoted = false;
            } else {
                value.append(ch);
            }
        }

        values.add(quoted
                ? value.toString()
                : value.toString().trim());
        return values.toArray(new String[0]);
    }
}