package com.library.frames;

import com.library.model.Member;
import com.library.service.MembersService;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.IOException;
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

public class MemberFrame extends JFrame {
    private final JTextField id = new JTextField();
    private final JTextField name = new JTextField();
    private final JTextField email = new JTextField();
    private final JTextField phone = new JTextField();
    private final JTextField search = new JTextField();

    private final DefaultTableModel model = new DefaultTableModel(
            new String[] { "ID", "Name", "Email", "Phone" }, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable table = new JTable(model);

    public MemberFrame() {
        super("Library Management System - Members");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(800, 550);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(245, 247, 250));
        build();
        load();
    }

    private void build() {
        setLayout(new BorderLayout(12, 12));

        JPanel form = new JPanel(new GridLayout(4, 2, 8, 8));
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createTitledBorder("Member Details"));
        addField(form, "ID", id);
        addField(form, "Name", name);
        addField(form, "Email", email);
        addField(form, "Phone", phone);

        JPanel buttons = new JPanel();
        buttons.setBackground(Color.WHITE);
        JButton add = new JButton("Add Member");
        JButton update = new JButton("Update Member");
        JButton delete = new JButton("Delete Member");
        JButton clear = new JButton("Clear");
        buttons.add(add);
        buttons.add(update);
        buttons.add(delete);
        buttons.add(clear);

        JPanel top = new JPanel(new BorderLayout(8, 8));
        top.setBackground(new Color(245, 247, 250));
        top.add(form, BorderLayout.CENTER);
        top.add(buttons, BorderLayout.SOUTH);
        add(top, BorderLayout.NORTH);

        JPanel filter = new JPanel();
        filter.setBackground(Color.WHITE);
        filter.add(new JLabel("Search:"));
        filter.add(search);
        JButton find = new JButton("Search");
        JButton all = new JButton("Show All");
        filter.add(find);
        filter.add(all);

        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(Color.WHITE);
        center.add(filter, BorderLayout.NORTH);
        center.add(new JScrollPane(table), BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        add.addActionListener(event -> run(this::addMember));
        update.addActionListener(event -> run(this::updateMember));
        delete.addActionListener(event -> run(this::deleteMember));
        clear.addActionListener(event -> clear());
        find.addActionListener(event -> run(this::filter));
        all.addActionListener(event -> load());
        table.getSelectionModel().addListSelectionListener(event -> fill());
    }

    private void addField(JPanel panel, String label, JTextField field) {
        panel.add(new JLabel(label + ":"));
        panel.add(field);
    }

    private void addMember() throws IOException {
        if (required()) {
            return;
        }
        if (MembersService.findById(id.getText().trim()) != null) {
            error("ID already exists");
            return;
        }
        List<Member> members = MembersService.readAll();
        members.add(member());
        MembersService.writeAll(members);
        load();
        clear();
    }

    private void updateMember() throws IOException {
        int row = table.getSelectedRow();
        if (row < 0 || required()) {
            error("Select a row and provide ID and name");
            return;
        }
        List<Member> members = MembersService.readAll();
        Member existing = members.get(row);
        Member updated = member();
        existing.setId(updated.getId());
        existing.setName(updated.getName());
        existing.setEmail(updated.getEmail());
        existing.setPhone(updated.getPhone());
        MembersService.writeAll(members);
        load();
        clear();
    }

    private void deleteMember() throws IOException {
        int row = table.getSelectedRow();
        if (row < 0) {
            error("Select a member");
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "Delete selected member?", "Confirm",
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        }
        List<Member> members = MembersService.readAll();
        members.remove(row);
        MembersService.writeAll(members);
        load();
        clear();
    }

    private void filter() throws IOException {
        String key = search.getText().trim().toLowerCase();
        model.setRowCount(0);
        for (Member member : MembersService.readAll()) {
            if (String.join(" ", member.toRow()).toLowerCase().contains(key)) {
                model.addRow(member.toRow());
            }
        }
    }

    private void load() {
        try {
            model.setRowCount(0);
            for (Member member : MembersService.readAll()) {
                model.addRow(member.toRow());
            }
        } catch (IOException error) {
            fail(error);
        }
    }

    private Member member() {
        return new Member(id.getText().trim(), name.getText().trim(),
                email.getText().trim(), phone.getText().trim());
    }

    private boolean required() {
        if (id.getText().trim().isEmpty() || name.getText().trim().isEmpty()) {
            error("ID and name are required");
            return true;
        }
        return false;
    }

    private void fill() {
        int row = table.getSelectedRow();
        if (row < 0) {
            return;
        }
        id.setText(model.getValueAt(row, 0).toString());
        name.setText(model.getValueAt(row, 1).toString());
        email.setText(model.getValueAt(row, 2).toString());
        phone.setText(model.getValueAt(row, 3).toString());
    }

    private void clear() {
        id.setText("");
        name.setText("");
        email.setText("");
        phone.setText("");
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