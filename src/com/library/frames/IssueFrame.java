package com.library.frames;

import com.library.model.Book;
import com.library.model.IssueRecord;
import com.library.service.BooksService;
import com.library.service.IssueRecordsService;
import com.library.service.MembersService;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class IssueFrame extends JFrame {
    private final JTextField bookId = new JTextField();
    private final JTextField memberId = new JTextField();
    private final DefaultTableModel model = new DefaultTableModel(
            new String[] { "ID", "Book ID", "Member ID", "Issue Date", "Due Date", "Return Date" }, 0);

    public IssueFrame() {
        super("Library Management System - Issue / Return");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(850, 500);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(245, 247, 250));

        JPanel form = new JPanel(new GridLayout(1, 4, 8, 8));
        form.setBackground(Color.WHITE);
        form.add(new JLabel("Book ID:"));
        form.add(bookId);
        form.add(new JLabel("Member ID:"));
        form.add(memberId);

        JButton issue = new JButton("Issue Book");
        JButton returnBook = new JButton("Return Book");
        JPanel buttons = new JPanel();
        buttons.setBackground(Color.WHITE);
        buttons.add(issue);
        buttons.add(returnBook);

        JPanel top = new JPanel(new BorderLayout(8, 8));
        top.setBackground(new Color(245, 247, 250));
        top.add(form, BorderLayout.CENTER);
        top.add(buttons, BorderLayout.SOUTH);
        add(top, BorderLayout.NORTH);

        JTable table = new JTable(model);
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        add(new JScrollPane(table), BorderLayout.CENTER);

        issue.addActionListener(event -> run(this::issue));
        returnBook.addActionListener(event -> run(this::returnBook));
        load();
    }

    private void issue() throws IOException {
        Book found = BooksService.findById(bookId.getText().trim());
        boolean memberExists = MembersService.findById(memberId.getText().trim()) != null;

        if (found == null || !memberExists) {
            error("Book or member does not exist");
            return;
        }

        if (found.getQuantity() <= 0 || IssueRecordsService.isBookIssued(found.getId())) {
            error("Book is unavailable");
            return;
        }

        List<Book> books = BooksService.readAll();
        Book book = findBook(books, found.getId());
        book.setQuantity(book.getQuantity() - 1);

        List<IssueRecord> records = IssueRecordsService.readAll();
        LocalDate today = LocalDate.now();
        records.add(new IssueRecord(
                IssueRecordsService.nextId(),
                book.getId(),
                memberId.getText().trim(),
                today.toString(),
                today.plusDays(14).toString(),
                "Not_Returned"));

        BooksService.writeAll(books);
        IssueRecordsService.writeAll(records);
        load();
    }

    private void returnBook() throws IOException {
        IssueRecord record = IssueRecordsService.activeForBook(bookId.getText().trim());
        if (record == null) {
            error("No active issue found for this book");
            return;
        }

        List<Book> books = BooksService.readAll();
        Book book = findBook(books, bookId.getText().trim());
        if (book == null) {
            error("Book does not exist");
            return;
        }

        record.setReturnDate(LocalDate.now().toString());
        book.setQuantity(book.getQuantity() + 1);
        List<IssueRecord> records = IssueRecordsService.readAll();
        for (IssueRecord item : records) {
            if (item.getId().equals(record.getId())) {
                item.setReturnDate(record.getReturnDate());
            }
        }

        BooksService.writeAll(books);
        IssueRecordsService.writeAll(records);
        load();
    }

    private Book findBook(List<Book> books, String id) {
        for (Book book : books) {
            if (book.getId().equalsIgnoreCase(id)) {
                return book;
            }
        }
        return null;
    }

    private void load() {
        try {
            model.setRowCount(0);
            for (IssueRecord record : IssueRecordsService.readAll()) {
                model.addRow(record.toRow());
            }
        } catch (IOException error) {
            fail(error);
        }
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