package com.financetracker.ui;

import com.financetracker.dao.TransactionDAO;
import com.financetracker.model.Transaction;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.PieChart;
import org.knowm.xchart.PieChartBuilder;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.style.PieStyler;
import org.knowm.xchart.style.CategoryStyler;
import org.knowm.xchart.style.Styler;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class AnalyticsPanel extends JPanel {
    private final TransactionDAO dao;

    public AnalyticsPanel(TransactionDAO dao) {
        this.dao = dao;
        setLayout(new GridLayout(1, 2, 20, 20));
        add(new JLabel("Loading charts...", SwingConstants.CENTER));
        refreshCharts();
    }

    public void refreshCharts() {
        removeAll();

        try {
            List<Transaction> transactions = dao.findAll();

            if (transactions == null || transactions.isEmpty()) {
                add(new JLabel("No data available", SwingConstants.CENTER));
                add(new JLabel(""));
                revalidate();
                repaint();
                return;
            }

            // --- Pie Chart: Expense by Category ---
            Map<String, BigDecimal> categoryTotals = transactions.stream()
                    .filter(t -> t.getType() != null && t.getType().equalsIgnoreCase("EXPENSE"))
                    .collect(Collectors.groupingBy(
                            t -> {
                                String c = t.getCategory();
                                return (c == null || c.isBlank()) ? "Uncategorized" : c;
                            },
                            Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)
                    ));

            PieChart pieChart = new PieChartBuilder()
                    .width(500).height(350)
                    .title("Expenses by Category")
                    .build();

            PieStyler pieStyler = pieChart.getStyler();
            pieStyler.setLegendVisible(true);
            pieStyler.setCircular(true);
            pieStyler.setPlotContentSize(0.8);

            // Older versions don’t support annotations — skip those safely
            for (Map.Entry<String, BigDecimal> e : categoryTotals.entrySet()) {
                if (e.getValue() != null && e.getValue().compareTo(BigDecimal.ZERO) > 0) {
                    pieChart.addSeries(e.getKey(), e.getValue());
                }
            }

            // --- Bar Chart: Income vs Expense over time ---
            Map<String, BigDecimal> incomeTotals = new TreeMap<>();
            Map<String, BigDecimal> expenseTotals = new TreeMap<>();

            for (Transaction t : transactions) {
                String dateKey = (t.getDate() == null) ? "unknown" : t.getDate().toString();
                if ("INCOME".equalsIgnoreCase(t.getType())) {
                    incomeTotals.merge(dateKey, t.getAmount(), BigDecimal::add);
                } else {
                    expenseTotals.merge(dateKey, t.getAmount(), BigDecimal::add);
                }
            }

            Set<String> allDates = new TreeSet<>();
            allDates.addAll(incomeTotals.keySet());
            allDates.addAll(expenseTotals.keySet());
            List<String> dates = new ArrayList<>(allDates);

            List<Double> incomeValues = dates.stream()
                    .map(d -> incomeTotals.getOrDefault(d, BigDecimal.ZERO).doubleValue())
                    .toList();

            List<Double> expenseValues = dates.stream()
                    .map(d -> expenseTotals.getOrDefault(d, BigDecimal.ZERO).doubleValue())
                    .toList();

            CategoryChart barChart = new CategoryChartBuilder()
                    .width(600).height(350)
                    .title("Income vs Expense")
                    .xAxisTitle("Date")
                    .yAxisTitle("Amount")
                    .build();

            CategoryStyler catStyler = barChart.getStyler();
            catStyler.setLegendPosition(Styler.LegendPosition.InsideNW);
            catStyler.setPlotGridLinesVisible(false);

            barChart.addSeries("Income", dates, incomeValues);
            barChart.addSeries("Expense", dates, expenseValues);

            add(new XChartPanel<>(pieChart));
            add(new XChartPanel<>(barChart));

        } catch (SQLException e) {
            removeAll();
            add(new JLabel("Error loading analytics: " + e.getMessage(), SwingConstants.CENTER));
        }

        revalidate();
        repaint();
    }
}
