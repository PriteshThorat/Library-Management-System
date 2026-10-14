package com.library.frames;

import com.library.model.Book;
import com.library.service.BooksService;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

public class BookFrame extends JFrame {
    private final JTextField id = new JTextField();
    private final JTextField title = new JTextField();
    private final JTextField author = new JTextField();
    private final JTextField isbn = new JTextField();
    private final JTextField quantity = new JTextField();
    private final JTextField search = new JTextField();

    private final DefaultTableModel model = new DefaultTableModel(
            new String[] { "ID", "Title", "Author", "ISBN", "Quantity" }, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable table = new JTable(model);

    public BookFrame() {
        super("Library Management System - Books");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(245, 247, 250));
        build();
        load();
    }

    private void build() {
        setLayout(new BorderLayout(12, 12));

        JPanel form = new JPanel(new GridLayout(5, 2, 8, 8));
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createTitledBorder("Book Details"));
        addField(form, "ID", id);
        addField(form, "Title", title);
        addField(form, "Author", author);
        addField(form, "ISBN", isbn);
        addField(form, "Quantity", quantity);

        JPanel buttons = new JPanel();
        buttons.setBackground(Color.WHITE);
        JButton add = new JButton("Add Book");
        JButton update = new JButton("Update Book");
        JButton delete = new JButton("Delete Book");
        JButton clear = new JButton("Clear");
        buttons.add(add);
        buttons.add(update);
        buttons.add(delete);
        buttons.add(clear);

        JPanel north = new JPanel(new BorderLayout(8, 8));
        north.setBackground(new Color(245, 247, 250));
        north.add(form, BorderLayout.CENTER);
        north.add(buttons, BorderLayout.SOUTH);
        add(north, BorderLayout.NORTH);

        JPanel searchPanel = new JPanel();
        searchPanel.setBackground(Color.WHITE);
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(search);
        JButton find = new JButton("Search");
        JButton all = new JButton("Show All");
        searchPanel.add(find);
        searchPanel.add(all);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));

        add.addActionListener(event -> run(this::addBook));
        update.addActionListener(event -> run(this::updateBook));
        delete.addActionListener(event -> run(this::deleteBook));
        clear.addActionListener(event -> clear());
        find.addActionListener(event -> run(this::filter));
        all.addActionListener(event -> load());
        table.getSelectionModel().addListSelectionListener(event -> fill());

        JPanel content = new JPanel(new BorderLayout());
        content.add(searchPanel, BorderLayout.NORTH);
        content.add(new JScrollPane(table), BorderLayout.CENTER);
        add(content, BorderLayout.CENTER);
    }

    private void addField(JPanel panel, String label, JTextField field) {
        panel.add(new JLabel(label + ":"));
        panel.add(field);
    }

    private void addBook() throws IOException {
        if (empty(id, title) || !validQuantity()) {
            return;
        }
        if (BooksService.findById(id.getText().trim()) != null) {
            error("ID already exists");
            return;
        }
        List<Book> books = BooksService.readAll();
        books.add(new Book(id.getText().trim(), title.getText().trim(),
                author.getText().trim(), isbn.getText().trim(),
                Integer.parseInt(quantity.getText().trim())));
        BooksService.writeAll(books);
        load();
        clear();
    }

    private void updateBook() throws IOException {
        int row = table.getSelectedRow();
        if (row < 0 || empty(id, title) || !validQuantity()) {
            error("Select a row and complete the fields");
            return;
        }
        List<Book> books = BooksService.readAll();
        Book book = books.get(row);
        book.setId(id.getText().trim());
        book.setTitle(title.getText().trim());
        book.setAuthor(author.getText().trim());
        book.setIsbn(isbn.getText().trim());
        book.setQuantity(Integer.parseInt(quantity.getText().trim()));
        BooksService.writeAll(books);
        load();
        clear();
    }

    private void deleteBook() throws IOException {
        int row = table.getSelectedRow();
        if (row < 0) {
            error("Select a book");
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "Delete selected book?", "Confirm",
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        }
        List<Book> books = BooksService.readAll();
        books.remove(row);
        BooksService.writeAll(books);
        load();
        clear();
    }

    private void filter() throws IOException {
        String key = search.getText().trim().toLowerCase();
        model.setRowCount(0);
        for (Book book : BooksService.readAll()) {
            String row = String.join(" ", book.toRow()).toLowerCase();
            if (row.contains(key)) {
                model.addRow(book.toRow());
            }
        }
    }

    private void load() {
        try {
            model.setRowCount(0);
            for (Book book : BooksService.readAll()) {
                model.addRow(book.toRow());
            }
        } catch (IOException error) {
            fail(error);
        }
    }

    private void fill() {
        int row = table.getSelectedRow();
        if (row < 0) {
            return;
        }
        id.setText(value(row, 0));
        title.setText(value(row, 1));
        author.setText(value(row, 2));
        isbn.setText(value(row, 3));
        quantity.setText(value(row, 4));
    }

    private String value(int row, int column) {
        return model.getValueAt(row, column).toString();
    }

    private boolean validQuantity() {
        try {
            if (Integer.parseInt(quantity.getText().trim()) < 0) {
                throw new NumberFormatException();
            }
            return true;
        } catch (NumberFormatException error) {
            error("Quantity must be a non-negative integer");
            return false;
        }
    }

    private boolean empty(JTextField... fields) {
        for (JTextField field : fields) {
            if (field.getText().trim().isEmpty()) {
                error("Required fields are missing");
                return true;
            }
        }
        return false;
    }

    private void clear() {
        id.setText("");
        title.setText("");
        author.setText("");
        isbn.setText("");
        quantity.setText("");
        table.clearSelection();
    }

    private void run(IoAction action) {
        try {
            action.run();
        } catch (IOException error) {
            fail(error);
        }
    }

    private void error(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void fail(IOException error) {
        System.err.println(error.getMessage());
        error(error.getMessage());
    }

    @FunctionalInterface
    private interface IoAction {
        void run() throws IOException;
    }
}