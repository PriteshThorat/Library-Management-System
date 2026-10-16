package src.frames;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import src.config.ConstantsConfig;
import src.service.BooksService;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

public class BookFrame {
  private static final Color CONTENT_BG = new Color(245, 247, 250);
  private static final Color BORDER = new Color(226, 232, 240);
  private static final Color TEXT = new Color(33, 37, 41);
  private static final Color MUTED = new Color(120, 130, 145);
  private static final Color FIELD_BG = new Color(248, 250, 252);

  private static final Color BLUE = new Color(26, 115, 232);
  private static final Color GREEN = new Color(52, 168, 83);
  private static final Color RED = new Color(220, 53, 69);
  private static final Color GRAY = new Color(108, 117, 125);

  private JTextField bookIdField;
  private JTextField titleField;
  private JTextField authorField;
  private JTextField isbnField;
  private JTextField quantityField;
  private JTextField searchField;

  private JTable bookTable;
  private DefaultTableModel tableModel;

  public BookFrame() {
    JFrame frame = new JFrame("Manage Books");
    frame.setTitle("Library Management System - Books");
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    initializeUI(frame);
    loadBooks(frame);

    frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
    frame.setVisible(true);
  }

  private void initializeUI(JFrame frame) {
    try {
      frame.setLayout(new BorderLayout());

      JPanel content = new JPanel(new BorderLayout(0, 20));
      content.setBackground(CONTENT_BG);
      content.setBorder(new EmptyBorder(24, 30, 28, 30));
      content.add(createFormCard(), BorderLayout.NORTH);
      content.add(createTableCard(), BorderLayout.CENTER);

      frame.add(new Header("Library Management System - Books"), BorderLayout.NORTH);
      frame.add(new Sidebar(frame, "Books"), BorderLayout.WEST);
      frame.add(content, BorderLayout.CENTER);
    } catch (Exception e) {
      System.out.println(e);
    }
  }

  private JPanel createFormCard() throws Exception {
      RoundedPanel card = new RoundedPanel(16, Color.WHITE, BORDER);
      card.setLayout(new BorderLayout(0, 18));
      card.setBorder(new EmptyBorder(24, 28, 24, 28));

      JLabel title = new JLabel("Book Details");
      title.setFont(new Font("Arial", Font.BOLD, 18));
      title.setForeground(TEXT);
      card.add(title, BorderLayout.NORTH);

      bookIdField = createField(ConstantsConfig.getBookId());
      titleField = createField();
      authorField = createField();
      isbnField = createField();
      quantityField = createField();

      JPanel form = new JPanel(new GridBagLayout());
      form.setOpaque(false);

      GridBagConstraints gc = new GridBagConstraints();
      gc.insets = new Insets(8, 6, 8, 6);

      addField(form, gc, 0, "Book ID:", bookIdField);
      addField(form, gc, 1, "Title:", titleField);
      addField(form, gc, 2, "Author:", authorField);
      addField(form, gc, 3, "ISBN:", isbnField);
      addField(form, gc, 4, "Quantity:", quantityField);

      JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
      buttons.setOpaque(false);

      JButton addButton = new RoundedButton("Add Book", BLUE);
      JButton updateButton = new RoundedButton("Update Book", GREEN);
      JButton deleteButton = new RoundedButton("Delete Book", RED);
      JButton clearButton = new RoundedButton("Clear", GRAY);

      buttons.add(addButton);
      buttons.add(updateButton);
      buttons.add(deleteButton);
      buttons.add(clearButton);

      JPanel body = new JPanel(new BorderLayout(0, 16));
      body.setOpaque(false);
      body.add(form, BorderLayout.CENTER);
      body.add(buttons, BorderLayout.SOUTH);

      card.add(body, BorderLayout.CENTER);

      addButton.addActionListener(e -> addBook((JFrame) SwingUtilities.getWindowAncestor(card)));
      updateButton.addActionListener(e -> updateBook((JFrame) SwingUtilities.getWindowAncestor(card)));
      deleteButton.addActionListener(e -> deleteBook((JFrame) SwingUtilities.getWindowAncestor(card)));
      clearButton.addActionListener(e -> clearFields((JFrame) SwingUtilities.getWindowAncestor(card)));

      return card;
  }

  private JPanel createTableCard() {
      RoundedPanel card = new RoundedPanel(16, Color.WHITE, BORDER);
      card.setLayout(new BorderLayout(0, 16));
      card.setBorder(new EmptyBorder(24, 28, 24, 28));

      JPanel top = new JPanel(new BorderLayout());
      top.setOpaque(false);

      JLabel title = new JLabel("Book List");
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
      bookTable.setRowHeight(32);
      bookTable.setFont(new Font("Arial", Font.PLAIN, 14));
      bookTable.setForeground(TEXT);
      bookTable.setGridColor(BORDER);
      bookTable.setShowVerticalLines(false);
      bookTable.setIntercellSpacing(new Dimension(0, 0));
      bookTable.setSelectionBackground(new Color(232, 240, 254));
      bookTable.setSelectionForeground(TEXT);
      bookTable.setFocusable(false);
      bookTable.getTableHeader().setReorderingAllowed(false);

      JTableHeader header = bookTable.getTableHeader();
      header.setFont(new Font("Arial", Font.BOLD, 13));
      header.setForeground(MUTED);
      header.setBackground(new Color(248, 250, 252));
      header.setPreferredSize(new Dimension(0, 40));
      header.setBorder(new EmptyBorder(0, 0, 0, 0));

      JScrollPane scrollPane = new JScrollPane(bookTable);
      scrollPane.setBorder(BorderFactory.createLineBorder(BORDER));
      scrollPane.getViewport().setBackground(Color.WHITE);

      card.add(scrollPane, BorderLayout.CENTER);

      searchButton.addActionListener(e -> searchBooks((JFrame) SwingUtilities.getWindowAncestor(card)));
      showAllButton.addActionListener(e -> loadBooks((JFrame) SwingUtilities.getWindowAncestor(card)));

      bookTable.getSelectionModel().addListSelectionListener(e -> {
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
    field.setPreferredSize(new Dimension(280, 38));
    field.setBorder(BorderFactory.createCompoundBorder(new RoundedLineBorder(BORDER, 8, 1), new EmptyBorder(6, 12, 6, 12)));

    return field;
  }

  private JTextField createField(String bookId) {
    JTextField field = new JTextField(bookId);
    field.setFont(new Font("Arial", Font.PLAIN, 14));
    field.setForeground(TEXT);
    field.setBackground(FIELD_BG);
    field.setCaretColor(TEXT);
    field.setPreferredSize(new Dimension(280, 38));
    field.setBorder(BorderFactory.createCompoundBorder(new RoundedLineBorder(BORDER, 8, 1), new EmptyBorder(6, 12, 6, 12)));

    return field;
  }

  private void addBook(JFrame frame) {
    String title = titleField.getText().trim();
    String author = authorField.getText().trim();
    String isbn = isbnField.getText().trim();
    String quantity = quantityField.getText().trim();

    if (title.isEmpty() || author.isEmpty() || isbn.isEmpty() || quantity.isEmpty()) {
      JOptionPane.showMessageDialog(frame, "Please fill in all fields.");

      return;
    }

      try {
        if (BooksService.writeBooks(title, author, isbn, String.valueOf(quantity))) {
          loadBooks(frame);
          clearFields(frame);

          JOptionPane.showMessageDialog(frame,  "Book added successfully.");
        }
      } catch (Exception e) {
        System.out.println(e);

        JOptionPane.showMessageDialog(frame, e.getMessage());
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