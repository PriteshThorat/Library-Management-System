package com.library.frames;

import com.library.model.Member;
import com.library.service.MembersService;
import com.library.util.CsvParser;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.FlowLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;

public class BulkImportFrame extends JFrame {
    private final JTextArea log = new JTextArea(18, 55);
    private final JButton importButton = new JButton("Import");
    private final JLabel summary = new JLabel("Choose a CSV file");
    private File file;

    public BulkImportFrame() {
        super("Library Management System - Bulk Import");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(650, 450);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(245, 247, 250));

        JButton choose = new JButton("Choose CSV File");
        importButton.setEnabled(false);
        log.setFont(new Font("Monospaced", Font.PLAIN, 13));
        log.setEditable(false);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setBackground(Color.WHITE);
        top.add(choose);
        top.add(importButton);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(log), BorderLayout.CENTER);
        summary.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 12, 8, 12));
        add(summary, BorderLayout.SOUTH);

        choose.addActionListener(e -> choose());
        importButton.addActionListener(e -> importMembers());
    }

    private void choose() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("CSV or TSV files", "csv", "tsv", "txt"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            file = chooser.getSelectedFile();
            importButton.setEnabled(true);
            log.setText("");
            summary.setText(file.getName());
        }
    }

    private void importMembers() {
        int imported = 0;
        int duplicate = 0;
        int invalid = 0;
        int total = 0;

        try {
            List<Member> members = MembersService.readAll();
            Set<String> ids = new HashSet<>();
            for (Member member : members) {
                ids.add(member.getId().trim().toLowerCase());
            }

            try (BufferedReader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
                String line;
                int row = 0;
                boolean first = true;
                while ((line = reader.readLine()) != null) {
                    row++;
                    if (line.trim().isEmpty()) {
                        continue;
                    }
                    if (first && isHeader(line)) {
                        first = false;
                        continue;
                    }
                    first = false;
                    total++;

                    String[] values = CsvParser.parseLine(line.replace('\t', ','));
                    if (values.length < 4 || values[0].trim().isEmpty() || values[1].trim().isEmpty()) {
                        invalid++;
                        log.append("[INVALID] row " + row + "\n");
                        continue;
                    }

                    String id = values[0].trim();
                    if (!ids.add(id.toLowerCase())) {
                        duplicate++;
                        log.append("[DUP] " + id + "\n");
                        continue;
                    }

                    Member member = new Member(id, values[1].trim(), values[2].trim(), values[3].trim());
                    members.add(member);
                    imported++;
                    log.append("[OK] " + member.getId() + " " + member.getName() + "\n");
                }
            }

            if (imported > 0) {
                MembersService.writeAll(members);
            }
            String message = "Imported: " + imported + "\nSkipped (duplicate): " + duplicate
                    + "\nSkipped (invalid): " + invalid + "\nTotal rows: " + total;
            summary.setText(message.replace('\n', ' '));
            JOptionPane.showMessageDialog(this, message, "Import complete", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException error) {
            fail(error);
        }
    }

    private boolean isHeader(String line) {
        String[] values = CsvParser.parseLine(line.replace('\t', ','));
        return values.length >= 2 && values[0].trim().equalsIgnoreCase("id")
                && values[1].trim().equalsIgnoreCase("name");
    }

    private void fail(IOException error) {
        System.err.println(error.getMessage());
        JOptionPane.showMessageDialog(this, error.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}