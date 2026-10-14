package src.frames;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import src.service.IssueRecordsService;

import java.awt.*;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class IssueReturnFrame extends JFrame {
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
        setSize(1050, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initializeUI();
        loadIssues();

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setVisible(true);
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));

        JLabel heading = new JLabel(
                "Book Issue & Return Management",
                SwingConstants.CENTER);

        heading.setFont(new Font("Arial", Font.BOLD, 24));
        heading.setBorder(
                BorderFactory.createEmptyBorder(15, 0, 10, 0));

        add(heading, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(10, 15, 15, 15));

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(
                BorderFactory.createTitledBorder("Issue Details"));

        issueIdField = new JTextField();
        bookIdField = new JTextField();
        memberIdField = new JTextField();
        issueDateField = new JTextField(LocalDate.now().toString());
        dueDateField = new JTextField(
                LocalDate.now().plusDays(14).toString());

        formPanel.add(new JLabel("Issue ID:"));
        formPanel.add(issueIdField);

        formPanel.add(new JLabel("Book ID:"));
        formPanel.add(bookIdField);

        formPanel.add(new JLabel("Member ID:"));
        formPanel.add(memberIdField);

        formPanel.add(new JLabel("Issue Date (YYYY-MM-DD):"));
        formPanel.add(issueDateField);

        formPanel.add(new JLabel("Due Date (YYYY-MM-DD):"));
        formPanel.add(dueDateField);

        JButton issueButton = new JButton("Issue Book");
        JButton returnButton = new JButton("Return Book");
        JButton clearButton = new JButton("Clear");
        JButton refreshButton = new JButton("Refresh");

        JPanel buttonPanel = new JPanel(
                new FlowLayout(FlowLayout.CENTER));

        buttonPanel.add(issueButton);
        buttonPanel.add(returnButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(refreshButton);

        JPanel topPanel = new JPanel(new BorderLayout(5, 10));
        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        JPanel searchPanel = new JPanel(
                new FlowLayout(FlowLayout.LEFT));

        searchField = new JTextField(20);

        JButton searchButton = new JButton("Search");
        JButton showAllButton = new JButton("Show All");

        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(showAllButton);

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
        issueTable.setRowHeight(25);
        issueTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);
        issueTable.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(issueTable);

        JPanel tablePanel = new JPanel(new BorderLayout(5, 10));
        tablePanel.add(searchPanel, BorderLayout.NORTH);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);

        issueButton.addActionListener(e -> issueBook());
        returnButton.addActionListener(e -> returnBook());
        clearButton.addActionListener(e -> clearFields());
        refreshButton.addActionListener(e -> loadIssues());
        searchButton.addActionListener(e -> searchIssues());
        showAllButton.addActionListener(e -> loadIssues());

        issueTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                fillFieldsFromTable();
            }
        });
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
                showMessage("Issue saved, but book quantity could not be updated. " + "Check books.csv before issuing another copy.");

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
              if (IssueRecordsService.updateBookQuantityAfterReturn(bookId)) {
                  showMessage("Return saved, but book quantity could not be updated. " + "Check books.csv before processing another return.");
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

    private String getStatus(String[] issue) {
        if (!issue[5].trim().isEmpty()) {
            return "Returned";
        }

        try {
            LocalDate dueDate = LocalDate.parse(issue[4]);

            if (LocalDate.now().isAfter(dueDate)) {
                return "Overdue";
            }

            return "Issued";
        } catch (DateTimeParseException ex) {
            return "Invalid Due Date";
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
                    if (value.toLowerCase().contains(keyword)) {
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
}