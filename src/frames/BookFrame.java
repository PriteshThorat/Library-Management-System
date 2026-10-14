package src.frames;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import src.service.BooksService;

import java.awt.*;
import java.util.List;

public class BookFrame {
  private JTextField bookIdField;
  private JTextField titleField;
  private JTextField authorField;
  private JTextField isbnField;
  private JTextField quantityField;
  private JTextField searchField;

  private JTable bookTable;
  private DefaultTableModel tableModel;

  public BookFrame () {
    JFrame frame = new JFrame("Manage Books");
    frame.setTitle("Library Management System - Books");
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    initializeUI(frame);
    loadBooks(frame);

    frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
    frame.setVisible(true);
  }

  private void initializeUI(JFrame frame) {
      frame.setLayout(new BorderLayout(10, 10));

      JLabel heading = new JLabel("Book Management", SwingConstants.CENTER);
      heading.setFont(new Font("Arial", Font.BOLD, 25));
      heading.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));

      frame.add(heading, BorderLayout.NORTH);

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

      frame.add(mainPanel, BorderLayout.CENTER);

      addButton.addActionListener(e -> addBook(frame));
      updateButton.addActionListener(e -> updateBook(frame));
      deleteButton.addActionListener(e -> deleteBook(frame));
      clearButton.addActionListener(e -> clearFields(frame));
      searchButton.addActionListener(e -> searchBooks(frame));
      showAllButton.addActionListener(e -> loadBooks(frame));

      bookTable.getSelectionModel().addListSelectionListener(e -> {
          if (!e.getValueIsAdjusting()) {
              fillFieldsFromTable();
          }
      });
  }

  private void addBook(JFrame frame) {
    String bookId = bookIdField.getText().trim();
    String title = titleField.getText().trim();
    String author = authorField.getText().trim();
    String isbn = isbnField.getText().trim();
    String quantityText = quantityField.getText().trim();

    if (bookId.isEmpty() || title.isEmpty() || author.isEmpty() || isbn.isEmpty() || quantityText.isEmpty()) {
      JOptionPane.showMessageDialog(frame, "Please fill in all fields.");
      return;
    }

      int quantity;

      try {
          quantity = Integer.parseInt(quantityText);

          if (quantity < 0) {
              JOptionPane.showMessageDialog(frame,  "Quantity cannot be negative.");
              return;
          }
      } catch (NumberFormatException ex) {
          JOptionPane.showMessageDialog(frame,
                  "Quantity must be a valid integer.");
          return;
      }
      
      try {
        List<String[]> books = BooksService.readBooks();

        for (String[] book : books) {
          if (book[0].equalsIgnoreCase(bookId)) {
            JOptionPane.showMessageDialog(frame,  "Book ID already exists.");

            return;
          }
        }

        books.add(new String[] {
          bookId, title, author, isbn, String.valueOf(quantity)
        });

        if (BooksService.writeBooks(books)) {
          loadBooks(frame);
          clearFields(frame);

          JOptionPane.showMessageDialog(frame,  "Book added successfully.");
        }
      } catch (Exception e) {
        System.out.println(e);
      }
  }

  private void updateBook(JFrame frame) {
    int selectedRow = bookTable.getSelectedRow();

    if (selectedRow == -1) {
      JOptionPane.showMessageDialog(frame,  "Please select a book to update.");

      return;
    }

    String bookId = bookIdField.getText().trim();
    String title = titleField.getText().trim();
    String author = authorField.getText().trim();
    String isbn = isbnField.getText().trim();
    String quantityText = quantityField.getText().trim();

    if (bookId.isEmpty() || title.isEmpty() || author.isEmpty() || isbn.isEmpty() || quantityText.isEmpty()) {
      JOptionPane.showMessageDialog(frame,  "Please fill in all fields.");
      
      return;
    }

    int quantity;

    try {
      quantity = Integer.parseInt(quantityText);

      if (quantity < 0) {
        JOptionPane.showMessageDialog(frame,  "Quantity cannot be negative.");

        return;
      }
    } catch (NumberFormatException ex) {
      JOptionPane.showMessageDialog(frame,  "Quantity must be a valid integer.");

      return;
    }

    // TODO: Future Changes
    // List<String[]> books = new ArrayList<>();

    // books.add(new String[] {
    //   bookId, title, author, isbn, String.valueOf(quantity)
    // });

    //String originalBookId = tableModel.getValueAt(selectedRow, 0).toString();

    try {
      if (BooksService.updateBook(bookId, title, author, isbn, String.valueOf(quantity))) {
        loadBooks(frame);
        clearFields(frame);

        JOptionPane.showMessageDialog(frame,  "Book updated successfully.");
      }
    } catch (Exception e) {
      System.out.println(e);
    }
  }

  private void deleteBook(JFrame frame) {
    int selectedRow = bookTable.getSelectedRow();

    if (selectedRow == -1) {
      JOptionPane.showMessageDialog(frame,  "Please select a book to delete.");
      
      return;
    }

    String bookId = tableModel.getValueAt(selectedRow, 0).toString();

    int choice = JOptionPane.showConfirmDialog(frame,  "Delete book " + bookId + "?",  "Confirm Delete",  JOptionPane.YES_NO_OPTION);

    if (choice != JOptionPane.YES_OPTION) {
      return;
    }

    try {
      if (BooksService.deleteBook(bookId)) {
        loadBooks(frame);
        clearFields(frame);

        JOptionPane.showMessageDialog(frame,  "Book deleted successfully.");
      }
    } catch (Exception e) {
      System.out.println(e);
    }
  }

  private void fillFieldsFromTable() {
    int selectedRow = bookTable.getSelectedRow();

    if (selectedRow == -1) {
        return;
    }

    bookIdField.setText(tableModel.getValueAt(selectedRow, 0).toString());
    titleField.setText(tableModel.getValueAt(selectedRow, 1).toString());
    authorField.setText(tableModel.getValueAt(selectedRow, 2).toString());
    isbnField.setText(tableModel.getValueAt(selectedRow, 3).toString());
    quantityField.setText(tableModel.getValueAt(selectedRow, 4).toString());
  }

  private void clearFields(JFrame frame) {
    bookIdField.setText("");
    titleField.setText("");
    authorField.setText("");
    isbnField.setText("");
    quantityField.setText("");
    bookTable.clearSelection();
  }

  private void loadBooks(JFrame frame) {
    tableModel.setRowCount(0);

    try {
      for (String[] book : BooksService.readBooks()) {
        tableModel.addRow(book);
      }
    } catch (Exception e) {
      System.out.println(e);
    }
  }

  private void searchBooks(JFrame frame) {
    String keyword = searchField.getText().trim().toLowerCase();

    if (keyword.isEmpty()) {
      loadBooks(frame);

      return;
    }

    tableModel.setRowCount(0);

    try {
      for (String[] book : BooksService.readBooks()) {
        String row = String.join(",", book).toLowerCase();

        if (row.contains(keyword)) {
          tableModel.addRow(book);
        }
      }
    } catch (Exception e) {
      System.out.println(e);
    }
  }
}