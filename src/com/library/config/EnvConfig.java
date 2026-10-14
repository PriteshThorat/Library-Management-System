package com.library.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public final class EnvConfig {
    private static final Properties VALUES = new Properties();

    static {
        try (FileInputStream input = new FileInputStream(".env")) {
            VALUES.load(input);
        } catch (IOException error) {
            System.err.println("Could not read .env: " + error.getMessage());
        }
    }

    private EnvConfig() {
    }

    private static String value(String key) {
        return VALUES.getProperty(key, "").trim();
    }

    public static String getUsername() {
        return value("DB_USERNAME");
    }

    public static String getPassword() {
        return value("DB_PASSWORD");
    }

    public static String getBooksFilename() {
        return value("DB_BOOKS_FILENAME");
    }

    public static String getMembersFilename() {
        return value("DB_MEMBERS_FILENAME");
    }

    public static String getIssueRecordsFilename() {
        return value("DB_ISSUE_RECORDS_FILENAME");
    }
}