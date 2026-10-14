package com.library.util;

import javax.swing.JTextField;

public final class InputHelper {
    private InputHelper() { }

    public static int readInt(JTextField field) {
        try {
            return Integer.parseInt(field.getText().trim());
        } catch (NumberFormatException error) {
            return -1;
        }
    }

    public static boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
}