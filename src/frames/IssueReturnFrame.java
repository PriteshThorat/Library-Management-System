package src.frames;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import src.service.IssueRecordsService;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class IssueReturnFrame extends JFrame {
    private static final Color CONTENT_BG = new Color(245, 247, 250);
    private static final Color BORDER = new Color(226, 232, 240);
    private static final Color TEXT = new Color(33, 37, 41);
    private static final Color MUTED = new Color(120, 130, 145);
    private static final Color FIELD_BG = new Color(248, 250, 252);

    private static final Color BLUE = new Color(26, 115, 232);
    private static final Color GREEN = new Color(52, 168, 83);
    private static final Color ORANGE = new Color(245, 124, 0);
    private static final Color GRAY = new Color(108, 117, 125);

    private JTextField issueIdField;
    private JTextField bookIdField;
    private JTextField memberIdField;
    private JTextField issueDateField;
    private JTextField dueDateField;
    private JTextField searchField;

    private JTable issueTable;
    private DefaultTableModel tableModel;

    public IssueReturnFrame() {
        setTitle("Library Management System - Issue / Return");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initializeUI();
        loadIssues();

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setVisible(true);
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        JPanel content = new JPanel(new BorderLayout(0, 20));
        content.setBackground(CONTENT_BG);
        content.setBorder(new EmptyBorder(24, 30, 28, 30));
        content.add(createFormCard(), BorderLayout.NORTH);
        content.add(createTableCard(), BorderLayout.CENTER);

        add(new Header("Library Management System - Issue / Return"), BorderLayout.NORTH);
        add(new Sidebar(this, "Issue / Return"), BorderLayout.WEST);
        add(content, BorderLayout.CENTER);
    }

    private JPanel createFormCard() {
        RoundedPanel card = new RoundedPanel(16, Color.WHITE, BORDER);
        card.setLayout(new BorderLayout(0, 18));
        card.setBorder(new EmptyBorder(24, 28, 24, 28));

        JLabel title = new JLabel("Issue Details");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setForeground(TEXT);
        card.add(title, BorderLayout.NORTH);

        issueIdField = createField();
        bookIdField = createField();
        memberIdField = createField();
        issueDateField = createField();
        dueDateField = createField();
        issueDateField.setText(LocalDate.now().toString());
        dueDateField.setText(LocalDate.now().plusDays(14).toString());

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(8, 6, 8, 6);

        addField(form, gc, 0, "Issue ID:", issueIdField);
        addField(form, gc, 1, "Book ID:", bookIdField);
        addField(form, gc, 2, "Member ID:", memberIdField);
        addField(form, gc, 3, "Issue Date (YYYY-MM-DD):", issueDateField);
        addField(form, gc, 4, "Due Date (YYYY-MM-DD):", dueDateField);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        buttons.setOpaque(false);

        JButton issueButton = new RoundedButton("Issue Book", BLUE);
        JButton returnButton = new RoundedButton("Return Book", GREEN);
        JButton clearButton = new RoundedButton("Clear", GRAY);
        JButton refreshButton = new RoundedButton("Refresh", ORANGE);

        buttons.add(issueButton);
        buttons.add(returnButton);
        buttons.add(clearButton);
        buttons.add(refreshButton);

        JPanel body = new JPanel(new BorderLayout(0, 16));
        body.setOpaque(false);
        body.add(form, BorderLayout.CENTER);
        body.add(buttons, BorderLayout.SOUTH);

        card.add(body, BorderLayout.CENTER);

        issueButton.addActionListener(e -> issueBook());
        returnButton.addActionListener(e -> returnBook());
        clearButton.addActionListener(e -> clearFields());
        refreshButton.addActionListener(e -> loadIssues());

        return card;
    }

    private JPanel createTableCard() {
        RoundedPanel card = new RoundedPanel(16, Color.WHITE, BORDER);
        card.setLayout(new BorderLayout(0, 16));
        card.setBorder(new EmptyBorder(24, 28, 24, 28));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel title = new JLabel("Issue / Return Records");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setForeground(TEXT);
        top.add(title, BorderLayout.WEST);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        searchPanel.setOpaque(false);

        searchField = new JTextField(20);
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchField.setForeground(TEXT);
        searchField.setBackground(FIELD_BG);
        searchField.setCaretColor(TEXT);
        searchField.setPreferredSize(new Dimension(220, 38));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                new RoundedLineBorder(BORDER, 8, 1),
                new EmptyBorder(6, 12, 6, 12)));

        JButton searchButton = new RoundedButton("Search", BLUE);
        JButton showAllButton = new RoundedButton("Show All", GRAY);

        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        searchLabel.setForeground(MUTED);

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(showAllButton);

        top.add(searchPanel, BorderLayout.EAST);
        card.add(top, BorderLayout.NORTH);

        String[] columns = {
            "Issue ID", "Book ID", "Member ID",
            "Issue Date", "Due Date", "Return Date", "Status"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        issueTable = new JTable(tableModel);
        issueTable.setRowHeight(32);
        issueTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        issueTable.setFont(new Font("Arial", Font.PLAIN, 14));
        issueTable.setForeground(TEXT);
        issueTable.setGridColor(BORDER);
        issueTable.setShowVerticalLines(false);
        issueTable.setIntercellSpacing(new Dimension(0, 0));
        issueTable.setSelectionBackground(new Color(232, 240, 254));
        issueTable.setSelectionForeground(TEXT);
        issueTable.setFocusable(false);
        issueTable.getTableHeader().setReorderingAllowed(false);

        JTableHeader header = issueTable.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 13));
        header.setForeground(MUTED);
        header.setBackground(new Color(248, 250, 252));
        header.setPreferredSize(new Dimension(0, 40));
        header.setBorder(new EmptyBorder(0, 0, 0, 0));

        JScrollPane scrollPane = new JScrollPane(issueTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER));
        scrollPane.getViewport().setBackground(Color.WHITE);

        card.add(scrollPane, BorderLayout.CENTER);

        searchButton.addActionListener(e -> searchIssues());
        showAllButton.addActionListener(e -> loadIssues());

        issueTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                fillFieldsFromTable();
            }
        });

        return card;
    }

    private void addField(JPanel form, GridBagConstraints gc, int row, String labelText, JTextField field) {
        gc.gridx = 0;
        gc.gridy = row;
        gc.anchor = GridBagConstraints.EAST;
        gc.fill = GridBagConstraints.NONE;
        gc.weightx = 0;

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        label.setForeground(MUTED);
        form.add(label, gc);

        gc.gridx = 1;
        gc.anchor = GridBagConstraints.WEST;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1;
        form.add(field, gc);
    }

    private JTextField createField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setForeground(TEXT);
        field.setBackground(FIELD_BG);
        field.setCaretColor(TEXT);
        field.setPreferredSize(new Dimension(320, 38));
        field.setBorder(BorderFactory.createCompoundBorder(
                new RoundedLineBorder(BORDER, 8, 1),
                new EmptyBorder(6, 12, 6, 12)));
        return field;
    }

    private void issueBook() {
        String issueId = issueIdField.getText().trim();
        String bookId = bookIdField.getText().trim();
        String memberId = memberIdField.getText().trim();
        String issueDateText = issueDateField.getText().trim();
        String dueDateText = dueDateField.getText().trim();

        if (issueId.isEmpty() || bookId.isEmpty()
                || memberId.isEmpty() || issueDateText.isEmpty()
                || dueDateText.isEmpty()) {
            showMessage("Please fill in all fields.");
            return;
        }

        LocalDate issueDate;
        LocalDate dueDate;

        try {
            issueDate = LocalDate.parse(issueDateText);
            dueDate = LocalDate.parse(dueDateText);
        } catch (DateTimeParseException ex) {
            showMessage("Dates must use YYYY-MM-DD format.");
            return;
        }

        if (dueDate.isBefore(issueDate)) {
            showMessage("Due date cannot be before issue date.");
            return;
        }

        try {
            List<String[]> issues = new ArrayList<>();

            issues.add(new String[] {
                    issueId,
                    bookId,
                    memberId,
                    issueDate.toString(),
                    dueDate.toString(),
                    "Not_Returned"
            });

            try {
                if (IssueRecordsService.issueBooks(issues)) {
                    if (!IssueRecordsService.updateBookQuantity(bookId)) {
                        showMessage("Issue saved, but book quantity could not be updated. "
                                + "Check books.csv before issuing another copy.");

                        loadIssues();

                        return;
                    }

                    loadIssues();
                    clearFields();
                    showMessage("Book issued successfully.");
                }
            } catch (Exception e) {
                System.out.println(e);
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void returnBook() {
        int selectedRow = issueTable.getSelectedRow();

        if (selectedRow == -1) {
            showMessage("Please select an issued book.");
            return;
        }

        String issueId = tableModel.getValueAt(selectedRow, 0).toString();
        String bookId = tableModel.getValueAt(selectedRow, 1).toString();

        try {
            if (IssueRecordsService.returnedIssueBook(issueId)) {
                if (!IssueRecordsService.updateBookQuantityAfterReturn(bookId)) {
                    showMessage("Return saved, but book quantity could not be updated. "
                            + "Check books.csv before processing another return.");
                    loadIssues();

                    return;
                }

                loadIssues();
                clearFields();
                showMessage("Book returned successfully.");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void loadIssues() {
        tableModel.setRowCount(0);

        try {
            for (String[] issue : IssueRecordsService.readIssuedBooks()) {
                String status = IssueRecordsService.getStatus(issue);

                tableModel.addRow(new String[] {
                        issue[0],
                        issue[1],
                        issue[2],
                        issue[3],
                        issue[4],
                        issue[5],
                        status
                });
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void searchIssues() {
        String keyword = searchField.getText()
                .trim().toLowerCase();

        tableModel.setRowCount(0);

        try {
            for (String[] issue : IssueRecordsService.readIssuedBooks()) {
                String[] row = {
                        issue[0], issue[1], issue[2],
                        issue[3], issue[4], issue[5], IssueRecordsService.getStatus(issue)
                };

                for (String value : row) {
                    if (value != null && value.toLowerCase().contains(keyword)) {
                        tableModel.addRow(row);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void fillFieldsFromTable() {
        int row = issueTable.getSelectedRow();

        if (row == -1) {
            return;
        }

        issueIdField.setText(
                tableModel.getValueAt(row, 0).toString());
        bookIdField.setText(
                tableModel.getValueAt(row, 1).toString());
        memberIdField.setText(
                tableModel.getValueAt(row, 2).toString());
        issueDateField.setText(
                tableModel.getValueAt(row, 3).toString());
        dueDateField.setText(
                tableModel.getValueAt(row, 4).toString());
    }

    private void clearFields() {
        issueIdField.setText("");
        bookIdField.setText("");
        memberIdField.setText("");
        issueDateField.setText(LocalDate.now().toString());
        dueDateField.setText(
                LocalDate.now().plusDays(14).toString());
        searchField.setText("");
        issueTable.clearSelection();
    }

    private List<String[]> readCsv(String filePath, int expectedColumns) {
        List<String[]> records = new ArrayList<>();
        File file = new File(filePath);

        if (!file.exists()) {
            return records;
        }

        try (BufferedReader reader = new BufferedReader(
                new FileReader(file))) {

            reader.readLine();

            String line;

            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",", -1);

                if (data.length == expectedColumns) {
                    records.add(data);
                }
            }

        } catch (IOException ex) {
            showMessage("Error reading " + filePath + ": "
                    + ex.getMessage());
        }

        return records;
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    private static class RoundedPanel extends JPanel {
        private final int radius;
        private final Color fill;
        private final Color border;

        RoundedPanel(int radius, Color fill, Color border) {
            this.radius = radius;
            this.fill = fill;
            this.border = border;
            setOpaque(false);
        }

        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(fill);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            if (border != null) {
                g2.setColor(border);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class RoundedButton extends JButton {
        private final Color base;

        RoundedButton(String text, Color base) {
            super(text);
            this.base = base;
            setFont(new Font("Arial", Font.BOLD, 14));
            setForeground(Color.WHITE);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(130, 38));
        }

        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getModel().isRollover() ? base.darker() : base);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class RoundedLineBorder extends javax.swing.border.AbstractBorder {
        private final Color color;
        private final int radius;
        private final int thickness;

        RoundedLineBorder(Color color, int radius, int thickness) {
            this.color = color;
            this.radius = radius;
            this.thickness = thickness;
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(thickness));
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }

        public Insets getBorderInsets(Component c) {
            return new Insets(thickness + 1, thickness + 1, thickness + 1, thickness + 1);
        }

        public Insets getBorderInsets(Component c, Insets insets) {
            insets.set(thickness + 1, thickness + 1, thickness + 1, thickness + 1);
            return insets;
        }
    }
}
