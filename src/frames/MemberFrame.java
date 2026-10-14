package src.frames;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import src.service.MembersService;

import java.awt.*;
import java.util.List;

public class MemberFrame extends JFrame {
    private JTextField memberIdField;
    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextField searchField;

    private JTable memberTable;
    private DefaultTableModel tableModel;

    public MemberFrame() {
        setTitle("Library Management System - Members");
        setSize(850, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initializeUI();
        loadMembers();

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setVisible(true);
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));

        JLabel heading = new JLabel(
                "Member Management", SwingConstants.CENTER);
        heading.setFont(new Font("Arial", Font.BOLD, 25));
        heading.setBorder(
                BorderFactory.createEmptyBorder(15, 0, 10, 0));

        add(heading, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(10, 15, 15, 15));

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(
                BorderFactory.createTitledBorder("Member Details"));

        memberIdField = new JTextField();
        nameField = new JTextField();
        emailField = new JTextField();
        phoneField = new JTextField();

        formPanel.add(new JLabel("Member ID:"));
        formPanel.add(memberIdField);

        formPanel.add(new JLabel("Name:"));
        formPanel.add(nameField);

        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);

        formPanel.add(new JLabel("Phone:"));
        formPanel.add(phoneField);

        JPanel buttonPanel = new JPanel(
                new FlowLayout(FlowLayout.CENTER));

        JButton addButton = new JButton("Add Member");
        JButton updateButton = new JButton("Update Member");
        JButton deleteButton = new JButton("Delete Member");
        JButton clearButton = new JButton("Clear");

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);

        JPanel leftPanel = new JPanel(new BorderLayout(5, 10));
        leftPanel.add(formPanel, BorderLayout.CENTER);
        leftPanel.add(buttonPanel, BorderLayout.SOUTH);

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
            "Member ID", "Name", "Email", "Phone"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        memberTable = new JTable(tableModel);
        memberTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);
        memberTable.setRowHeight(25);
        memberTable.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(memberTable);

        JPanel tablePanel = new JPanel(new BorderLayout(5, 10));
        tablePanel.add(searchPanel, BorderLayout.NORTH);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(leftPanel, BorderLayout.NORTH);
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);

        addButton.addActionListener(e -> addMember());
        updateButton.addActionListener(e -> updateMember());
        deleteButton.addActionListener(e -> deleteMember());
        clearButton.addActionListener(e -> clearFields());
        searchButton.addActionListener(e -> searchMembers());
        showAllButton.addActionListener(e -> loadMembers());

        memberTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                fillFieldsFromTable();
            }
        });
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

          members.add(new String[] {memberId, name, email, phone});

          if (MembersService.writeMembers(members)) {
            loadMembers();
            clearFields();

            JOptionPane.showMessageDialog(this,  "Member added successfully.");
          }
        } catch (Exception e) {
          System.out.println(e);
        }
    }

    private void updateMember() {
        int selectedRow = memberTable.getSelectedRow();

        if (selectedRow == -1) {
          JOptionPane.showMessageDialog(this,  "Please select a member to update.");
          
          return;
        }

        String memberId = memberIdField.getText().trim();
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();

        if (memberId.isEmpty() || name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
          JOptionPane.showMessageDialog(this,  "Please fill in all fields.");
          
          return;
        }

        if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
          JOptionPane.showMessageDialog(this,  "Please enter a valid email address.");
          
          return;
        }

        if (!phone.matches("\\d{10}")) {
          JOptionPane.showMessageDialog(this,  "Phone number must contain 10 digits.");
          
          return;
        }

        try {
          if (MembersService.updateMember(memberId, name, email, phone)) {
            loadMembers();
            clearFields();

            JOptionPane.showMessageDialog(this,  "Member updated successfully.");
          }
        } catch (Exception e) {
          System.out.println(e);
        }
    }

    private void deleteMember() {
        int selectedRow = memberTable.getSelectedRow();

        if (selectedRow == -1) {
          JOptionPane.showMessageDialog(this,  "Please select a member to delete.");
          
          return;
        }

        String memberId = tableModel.getValueAt(selectedRow, 0).toString();

        int choice = JOptionPane.showConfirmDialog(this, "Delete member " + memberId + "?",  "Confirm Delete",  JOptionPane.YES_NO_OPTION);

        if (choice != JOptionPane.YES_OPTION) {
          return;
        }

        try {
          if (MembersService.deleteMember(memberId)) {
            loadMembers();
            clearFields();

            JOptionPane.showMessageDialog(this,  "Member deleted successfully.");
          }
        } catch (Exception e) {
          System.out.println(e);
        }
    }

    private void searchMembers() {
      String keyword = searchField.getText().trim().toLowerCase();

      tableModel.setRowCount(0);

      String[] member = MembersService.searchMember(keyword);

      tableModel.addRow(member);
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
}