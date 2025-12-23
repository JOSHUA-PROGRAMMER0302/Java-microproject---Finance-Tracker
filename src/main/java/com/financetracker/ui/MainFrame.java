package com.financetracker.ui;

import com.financetracker.dao.TransactionDAO;
import com.financetracker.model.Transaction;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class MainFrame extends JFrame {

    private TransactionDAO dao = new TransactionDAO();
    private DefaultTableModel tableModel;
    private JTable table;

    private JTextField dateField;
    private JTextField descField;
    private JTextField categoryField;
    private JTextField amountField;
    private JComboBox<String> typeCombo;

    private JLabel totalIncomeLabel;
    private JLabel totalExpenseLabel;

    private AnalyticsPanel analyticsPanel;
    private JPanel transactionPanel;

    public MainFrame() {
        // Apply theme
        UITheme.apply();

        setTitle("💸 Finance Tracker Dashboard");
        setSize(1000, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();
        loadTransactions();
        updateTotals();
    }

    private void initUI() {
        // ===== Sidebar Navigation =====
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new GridLayout(6, 1, 0, 10));
        sidebar.setBackground(new Color(25, 42, 86));
        sidebar.setBorder(new EmptyBorder(30, 15, 30, 15));

        JLabel appTitle = new JLabel("Finance Tracker", JLabel.CENTER);
        appTitle.setForeground(Color.WHITE);
        appTitle.setFont(new Font("Segoe UI Semibold", Font.BOLD, 20));
        sidebar.add(appTitle);

        JButton transactionsBtn = createSidebarButton("Transactions");
        JButton analyticsBtn = createSidebarButton("Analytics");
        JButton exitBtn = createSidebarButton("Exit");

        sidebar.add(transactionsBtn);
        sidebar.add(analyticsBtn);
        sidebar.add(new JLabel()); // spacer
        sidebar.add(exitBtn);

        // ===== Header Panel =====
        JPanel header = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, new Color(66, 133, 244), getWidth(), getHeight(), new Color(30, 82, 222));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        header.setPreferredSize(new Dimension(0, 60));
        JLabel headerTitle = new JLabel("💰 Dashboard Overview", JLabel.CENTER);
        headerTitle.setForeground(Color.WHITE);
        headerTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        header.add(headerTitle);

        // ===== Transaction Panel =====
        JPanel inputPanel = new JPanel(new GridLayout(2, 6, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Add Transaction"));
        inputPanel.setBackground(new Color(250, 250, 255));

        dateField = new JTextField(LocalDate.now().toString());
        descField = new JTextField();
        categoryField = new JTextField();
        amountField = new JTextField();
        typeCombo = new JComboBox<>(new String[]{"INCOME", "EXPENSE"});

        JButton addButton = createStyledButton("Add");
        addButton.addActionListener(this::onAdd);

        inputPanel.add(new JLabel("Date (YYYY-MM-DD):"));
        inputPanel.add(new JLabel("Description:"));
        inputPanel.add(new JLabel("Category:"));
        inputPanel.add(new JLabel("Amount:"));
        inputPanel.add(new JLabel("Type:"));
        inputPanel.add(new JLabel(""));
        inputPanel.add(dateField);
        inputPanel.add(descField);
        inputPanel.add(categoryField);
        inputPanel.add(amountField);
        inputPanel.add(typeCombo);
        inputPanel.add(addButton);

        String[] columns = {"ID", "Date", "Description", "Category", "Amount", "Type"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(table);

        JButton deleteButton = createStyledButton("Delete Selected");
        deleteButton.addActionListener(this::onDelete);

        totalIncomeLabel = new JLabel("Total Income: ₹0.00");
        totalExpenseLabel = new JLabel("Total Expense: ₹0.00");
        totalIncomeLabel.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
        totalExpenseLabel.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));

        JPanel totalPanel = new JPanel(new GridLayout(1, 2));
        totalPanel.add(totalIncomeLabel);
        totalPanel.add(totalExpenseLabel);

        transactionPanel = new JPanel(new BorderLayout(10, 10));
        transactionPanel.setBackground(Color.WHITE);
        transactionPanel.add(inputPanel, BorderLayout.NORTH);
        transactionPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(deleteButton, BorderLayout.WEST);
        bottomPanel.add(totalPanel, BorderLayout.EAST);
        transactionPanel.add(bottomPanel, BorderLayout.SOUTH);

        // ===== Analytics Tab =====
        analyticsPanel = new AnalyticsPanel(dao);

        // ===== Tabs =====
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Transactions", transactionPanel);
        tabs.addTab("Analytics", analyticsPanel);
        tabs.addTab("Smart Insights", new InsightsPanel(dao));

        // ===== Main Layout =====
        setLayout(new BorderLayout());
        add(header, BorderLayout.NORTH);
        add(sidebar, BorderLayout.WEST);
        add(tabs, BorderLayout.CENTER);

        // Sidebar navigation events
        transactionsBtn.addActionListener(e -> tabs.setSelectedIndex(0));
        analyticsBtn.addActionListener(e -> tabs.setSelectedIndex(1));
        exitBtn.addActionListener(e -> System.exit(0));
    }

    private JButton createSidebarButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(new Color(52, 73, 94));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(41, 128, 185));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(52, 73, 94));
            }
        });
        return btn;
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(new Color(66, 133, 244));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(48, 102, 190));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(66, 133, 244));
            }
        });
        return btn;
    }

    private void onAdd(ActionEvent e) {
        try {
            LocalDate date = LocalDate.parse(dateField.getText().trim());
            String desc = descField.getText().trim();
            String category = categoryField.getText().trim();
            BigDecimal amount = new BigDecimal(amountField.getText().trim());
            String type = (String) typeCombo.getSelectedItem();

            Transaction t = new Transaction(date, desc, category, amount, type);
            dao.save(t);

            JOptionPane.showMessageDialog(this, "Transaction added successfully!");
            loadTransactions();
            updateTotals();
            analyticsPanel.refreshCharts();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error adding transaction: " + ex.getMessage());
        }
    }

    private void onDelete(ActionEvent e) {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a row to delete!");
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        try {
            if (dao.deleteById(id)) {
                JOptionPane.showMessageDialog(this, "Transaction deleted!");
                loadTransactions();
                updateTotals();
                analyticsPanel.refreshCharts();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error deleting: " + ex.getMessage());
        }
    }

    private void loadTransactions() {
        try {
            tableModel.setRowCount(0);
            List<Transaction> list = dao.findAll();
            for (Transaction t : list) {
                tableModel.addRow(new Object[]{
                        t.getId(),
                        t.getDate(),
                        t.getDescription(),
                        t.getCategory(),
                        t.getAmount(),
                        t.getType()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load data: " + e.getMessage());
        }
    }

    private void updateTotals() {
        try {
            BigDecimal income = dao.getTotalByType("INCOME");
            BigDecimal expense = dao.getTotalByType("EXPENSE");
            totalIncomeLabel.setText("Total Income: ₹" + income);
            totalExpenseLabel.setText("Total Expense: ₹" + expense);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ✅ Launch
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
