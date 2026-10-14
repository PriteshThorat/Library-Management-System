package src.frames;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import src.service.MembersService;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

public class MemberFrame extends JFrame {
    private static final Color CONTENT_BG = new Color(245, 247, 250);
    private static final Color BORDER = new Color(226, 232, 240);
    private static final Color TEXT = new Color(33, 37, 41);
    private static final Color MUTED = new Color(120, 130, 145);
    private static final Color FIELD_BG = new Color(248, 250, 252);

    private static final Color BLUE = new Color(26, 115, 232);
    private static final Color GREEN = new Color(52, 168, 83);
    private static final Color RED = new Color(220, 53, 69);
    private static final Color GRAY = new Color(108, 117, 125);

    private JTextField memberIdField;
    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextField searchField;

    private JTable memberTable;
    private DefaultTableModel tableModel;

    public MemberFrame() {
        setTitle("Library Management System - Members");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initializeUI();
        loadMembers();

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

        add(new Header("Library Management System - Members"), BorderLayout.NORTH);
        add(new Sidebar(this, "Members"), BorderLayout.WEST);
        add(content, BorderLayout.CENTER);
    }

    private JPanel createFormCard() {
        RoundedPanel card = new RoundedPanel(16, Color.WHITE, BORDER);
        card.setLayout(new BorderLayout(0, 18));
        card.setBorder(new EmptyBorder(24, 28, 24, 28));

        JLabel title = new JLabel("Member Details");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setForeground(TEXT);
        card.add(title, BorderLayout.NORTH);

        memberIdField = createField();
        nameField = createField();
        emailField = createField();
        phoneField = createField();

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(8, 6, 8, 6);

        addField(form, gc, 0, "Member ID:", memberIdField);
        addField(form, gc, 1, "Name:", nameField);
        addField(form, gc, 2, "Email:", emailField);
        addField(form, gc, 3, "Phone:", phoneField);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        buttons.setOpaque(false);

        JButton addButton = new RoundedButton("Add Member", BLUE);
        JButton updateButton = new RoundedButton("Update Member", GREEN);
        JButton deleteButton = new RoundedButton("Delete Member", RED);
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

        addButton.addActionListener(e -> addMember());
        updateButton.addActionListener(e -> updateMember());
        deleteButton.addActionListener(e -> deleteMember());
        clearButton.addActionListener(e -> clearFields());

        return card;
    }

    private JPanel createTableCard() {
        RoundedPanel card = new RoundedPanel(16, Color.WHITE, BORDER);
        card.setLayout(new BorderLayout(0, 16));
        card.setBorder(new EmptyBorder(24, 28, 24, 28));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel title = new JLabel("Member List");
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
            "Member ID", "Name", "Email", "Phone"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        memberTable = new JTable(tableModel);
        memberTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        memberTable.setRowHeight(32);
        memberTable.setFont(new Font("Arial", Font.PLAIN, 14));
        memberTable.setForeground(TEXT);
        memberTable.setGridColor(BORDER);
        memberTable.setShowVerticalLines(false);
        memberTable.setIntercellSpacing(new Dimension(0, 0));
        memberTable.setSelectionBackground(new Color(232, 240, 254));
        memberTable.setSelectionForeground(TEXT);
        memberTable.setFocusable(false);
        memberTable.getTableHeader().setReorderingAllowed(false);

        JTableHeader header = memberTable.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 13));
        header.setForeground(MUTED);
        header.setBackground(new Color(248, 250, 252));
        header.setPreferredSize(new Dimension(0, 40));
        header.setBorder(new EmptyBorder(0, 0, 0, 0));

        JScrollPane scrollPane = new JScrollPane(memberTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER));
        scrollPane.getViewport().setBackground(Color.WHITE);

        card.add(scrollPane, BorderLayout.CENTER);

        searchButton.addActionListener(e -> searchMembers());
        showAllButton.addActionListener(e -> loadMembers());

        memberTable.getSelectionModel().addListSelectionListener(e -> {
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
        field.setBorder(BorderFactory.createCompoundBorder(
                new RoundedLineBorder(BORDER, 8, 1),
                new EmptyBorder(6, 12, 6, 12)));
        return field;
    }

    private void addMember() {
        String memberId = memberIdField.getText().trim();
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();

        if (memberId.isEmpty() || name.isEmpty()
                || email.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill in all fields.");
            return;
        }

        if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid email address.");
            return;
        }

        if (!phone.matches("\\d{10}")) {
            JOptionPane.showMessageDialog(this,
                    "Phone number must contain 10 digits.");
            return;
        }

        try {
            List<String[]> members = MembersService.readMembers();

            for (String[] member : members) {
                if (member[0].equalsIgnoreCase(memberId)) {
                    JOptionPane.showMessageDialog(this,
                            "Member ID already exists.");
                    return;
                }
            }

            members.add(new String[] { memberId, name, email, phone });

            if (MembersService.writeMembers(members)) {
                loadMembers();
                clearFields();

                JOptionPane.showMessageDialog(this, "Member added successfully.");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void updateMember() {
        int selectedRow = memberTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a member to update.");

            return;
        }

        String memberId = memberIdField.getText().trim();
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();

        if (memberId.isEmpty() || name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");

            return;
        }

        if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address.");

            return;
        }

        if (!phone.matches("\\d{10}")) {
            JOptionPane.showMessageDialog(this, "Phone number must contain 10 digits.");

            return;
        }

        try {
            if (MembersService.updateMember(memberId, name, email, phone)) {
                loadMembers();
                clearFields();

                JOptionPane.showMessageDialog(this, "Member updated successfully.");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void deleteMember() {
        int selectedRow = memberTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a member to delete.");

            return;
        }

        String memberId = tableModel.getValueAt(selectedRow, 0).toString();

        int choice = JOptionPane.showConfirmDialog(this, "Delete member " + memberId + "?", "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            if (MembersService.deleteMember(memberId)) {
                loadMembers();
                clearFields();

                JOptionPane.showMessageDialog(this, "Member deleted successfully.");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void searchMembers() {
        String keyword = searchField.getText().trim().toLowerCase();
        tableModel.setRowCount(0);
        String[] member = MembersService.searchMember(keyword);
        if (member != null && member.length > 0) {
            tableModel.addRow(member);
        }
    }

    private void fillFieldsFromTable() {
        int selectedRow = memberTable.getSelectedRow();

        if (selectedRow == -1) {
            return;
        }

        memberIdField.setText(tableModel.getValueAt(selectedRow, 0).toString());
        nameField.setText(tableModel.getValueAt(selectedRow, 1).toString());
        emailField.setText(tableModel.getValueAt(selectedRow, 2).toString());
        phoneField.setText(tableModel.getValueAt(selectedRow, 3).toString());
    }

    private void clearFields() {
        memberIdField.setText("");
        nameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        searchField.setText("");
        memberTable.clearSelection();
    }

    private void loadMembers() {
        try {
            tableModel.setRowCount(0);

            for (String[] member : MembersService.readMembers()) {
                tableModel.addRow(member);
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
