package src.frames;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class BookFrame extends JFrame {

    private JTextField bookIdField;
    private JTextField titleField;
    private JTextField authorField;
    private JTextField isbnField;
    private JTextField quantityField;
    private JTextField searchField;

    private JTable bookTable;
    private DefaultTableModel tableModel;

    private final String FILE_PATH = "books.csv";

    public BookFrame() {
        setTitle("Library Management System - Books");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initializeUI();
        loadBooks();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));

        JLabel heading = new JLabel("Book Management", SwingConstants.CENTER);
        heading.setFont(new Font("Arial", Font.BOLD, 25));
        heading.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));

        add(heading, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Book Details"));

        bookIdField = new JTextField();
        titleField = new JTextField();
        authorField = new JTextField();
        isbnField = new JTextField();
        quantityField = new JTextField();

        formPanel.add(new JLabel("Book ID:"));
        formPanel.add(bookIdField);

        formPanel.add(new JLabel("Title:"));
        formPanel.add(titleField);

        formPanel.add(new JLabel("Author:"));
        formPanel.add(authorField);

        formPanel.add(new JLabel("ISBN:"));
        formPanel.add(isbnField);

        formPanel.add(new JLabel("Quantity:"));
        formPanel.add(quantityField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JButton addButton = new JButton("Add Book");
        JButton updateButton = new JButton("Update Book");
        JButton deleteButton = new JButton("Delete Book");
        JButton clearButton = new JButton("Clear");

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);

        JPanel leftPanel = new JPanel(new BorderLayout(5, 10));
        leftPanel.add(formPanel, BorderLayout.CENTER);
        leftPanel.add(buttonPanel, BorderLayout.SOUTH);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        JButton showAllButton = new JButton("Show All");

        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(showAllButton);

        String[] columns = {
            "Book ID", "Title", "Author", "ISBN", "Quantity"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        bookTable = new JTable(tableModel);
        bookTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookTable.setRowHeight(25);
        bookTable.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(bookTable);

        JPanel tablePanel = new JPanel(new BorderLayout(5, 10));
        tablePanel.add(searchPanel, BorderLayout.NORTH);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(leftPanel, BorderLayout.NORTH);
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);

        addButton.addActionListener(e -> addBook());
        updateButton.addActionListener(e -> updateBook());
        deleteButton.addActionListener(e -> deleteBook());
        clearButton.addActionListener(e -> clearFields());
        searchButton.addActionListener(e -> searchBooks());
        showAllButton.addActionListener(e -> loadBooks());

        bookTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                fillFieldsFromTable();
            }
        });
    }

    private void addBook() {
        String bookId = bookIdField.getText().trim();
        String title = titleField.getText().trim();
        String author = authorField.getText().trim();
        String isbn = isbnField.getText().trim();
        String quantityText = quantityField.getText().trim();

        if (bookId.isEmpty() || title.isEmpty()
                || author.isEmpty() || isbn.isEmpty()
                || quantityText.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill in all fields.");
            return;
        }

        int quantity;

        try {
            quantity = Integer.parseInt(quantityText);

            if (quantity < 0) {
                JOptionPane.showMessageDialog(this,
                        "Quantity cannot be negative.");
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Quantity must be a valid integer.");
            return;
        }

        List<String[]> books = readBooks();

        for (String[] book : books) {
            if (book[0].equalsIgnoreCase(bookId)) {
                JOptionPane.showMessageDialog(this,
                        "Book ID already exists.");
                return;
            }
        }

        books.add(new String[] {
            bookId, title, author, isbn, String.valueOf(quantity)
        });

        if (writeBooks(books)) {
            loadBooks();
            clearFields();

            JOptionPane.showMessageDialog(this,
                    "Book added successfully.");
        }
    }

    private void updateBook() {
        int selectedRow = bookTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a book to update.");
            return;
        }

        String bookId = bookIdField.getText().trim();
        String title = titleField.getText().trim();
        String author = authorField.getText().trim();
        String isbn = isbnField.getText().trim();
        String quantityText = quantityField.getText().trim();

        if (bookId.isEmpty() || title.isEmpty()
                || author.isEmpty() || isbn.isEmpty()
                || quantityText.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill in all fields.");
            return;
        }

        int quantity;

        try {
            quantity = Integer.parseInt(quantityText);

            if (quantity < 0) {
                JOptionPane.showMessageDialog(this,
                        "Quantity cannot be negative.");
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Quantity must be a valid integer.");
            return;
        }

        String originalBookId = tableModel.getValueAt(
                selectedRow, 0).toString();

        List<String[]> books = readBooks();

        for (String[] book : books) {
            if (!book[0].equalsIgnoreCase(originalBookId)
                    && book[0].equalsIgnoreCase(bookId)) {
                JOptionPane.showMessageDialog(this,
                        "Another book already uses this Book ID.");
                return;
            }
        }

        boolean updated = false;

        for (String[] book : books) {
            if (book[0].equalsIgnoreCase(originalBookId)) {
                book[0] = bookId;
                book[1] = title;
                book[2] = author;
                book[3] = isbn;
                book[4] = String.valueOf(quantity);
                updated = true;
                break;
            }
        }

        if (updated && writeBooks(books)) {
            loadBooks();
            clearFields();

            JOptionPane.showMessageDialog(this,
                    "Book updated successfully.");
        }
    }

    private void deleteBook() {
        int selectedRow = bookTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a book to delete.");
            return;
        }

        String bookId = tableModel.getValueAt(
                selectedRow, 0).toString();

        int choice = JOptionPane.showConfirmDialog(this,
                "Delete book " + bookId + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        List<String[]> books = readBooks();
        books.removeIf(book -> book[0].equalsIgnoreCase(bookId));

        if (writeBooks(books)) {
            loadBooks();
            clearFields();

            JOptionPane.showMessageDialog(this,
                    "Book deleted successfully.");
        }
    }

    private void searchBooks() {
        String keyword = searchField.getText().trim().toLowerCase();

        tableModel.setRowCount(0);

        for (String[] book : readBooks()) {
            boolean matches = false;

            for (String value : book) {
                if (value.toLowerCase().contains(keyword)) {
                    matches = true;
                    break;
                }
            }

            if (matches) {
                tableModel.addRow(book);
            }
        }
    }

    private void fillFieldsFromTable() {
        int selectedRow = bookTable.getSelectedRow();

        if (selectedRow == -1) {
            return;
        }

        bookIdField.setText(
                tableModel.getValueAt(selectedRow, 0).toString());
        titleField.setText(
                tableModel.getValueAt(selectedRow, 1).toString());
        authorField.setText(
                tableModel.getValueAt(selectedRow, 2).toString());
        isbnField.setText(
                tableModel.getValueAt(selectedRow, 3).toString());
        quantityField.setText(
                tableModel.getValueAt(selectedRow, 4).toString());
    }

    private void clearFields() {
        bookIdField.setText("");
        titleField.setText("");
        authorField.setText("");
        isbnField.setText("");
        quantityField.setText("");
        bookTable.clearSelection();
    }

    private void loadBooks() {
        tableModel.setRowCount(0);

        for (String[] book : readBooks()) {
            tableModel.addRow(book);
        }
    }

    private List<String[]> readBooks() {
        List<String[]> books = new ArrayList<>();
        File file = new File(FILE_PATH);

        if (!file.exists()) {
            return books;
        }

        try (BufferedReader reader = new BufferedReader(
                new FileReader(file))) {

            String line = reader.readLine();

            if (line == null) {
                return books;
            }

            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",", -1);

                if (data.length == 5) {
                    books.add(data);
                }
            }

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error reading books.csv: " + ex.getMessage());
        }

        return books;
    }

    private boolean writeBooks(List<String[]> books) {
        try (PrintWriter writer = new PrintWriter(
                new FileWriter(FILE_PATH))) {

            writer.println("Book ID,Title,Author,isbn,quantity");

            for (String[] book : books) {
                writer.println(String.join(",", book));
            }

            return true;

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error saving books.csv: " + ex.getMessage());
            return false;
        }
    }
}