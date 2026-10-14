package com.library;

import com.library.config.FileCreation;
import com.library.frames.LoginFrame;
import java.io.IOException;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        try {
            FileCreation.createBooksFile();
            FileCreation.createMembersFile();
            FileCreation.createIssueRecordsFile();

            SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
        } catch (IOException error) {
            System.err.println(error.getMessage());
            JOptionPane.showMessageDialog(
                    null,
                    error.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}