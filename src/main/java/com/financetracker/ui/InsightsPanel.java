package com.financetracker.ui;

import com.financetracker.dao.TransactionDAO;
import com.financetracker.model.Transaction;
import org.knowm.xchart.*;
import org.knowm.xchart.style.markers.None;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.style.Styler;
import org.knowm.xchart.style.markers.None;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * AI-Powered Smart Insights + Predictive Expense Graph Panel
 * Shows trend analysis, growth insights, and future expense prediction.
 */
public class InsightsPanel extends JPanel {

    private TransactionDAO dao;
    private JTextArea insightsArea;
    private JPanel chartPanel;

    public InsightsPanel(TransactionDAO dao) {
        this.dao = dao;
        setLayout(new BorderLayout(10, 10));
        setBackground(UITheme.background());

        insightsArea = new JTextArea();
        insightsArea.setEditable(false);
        insightsArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        insightsArea.setBackground(Color.WHITE);
        insightsArea.setBorder(BorderFactory.createTitledBorder("AI Smart Insights"));

        chartPanel = new JPanel(new BorderLayout());
        chartPanel.setBorder(BorderFactory.createTitledBorder("Predictive Spending Graph"));

        add(new JScrollPane(insightsArea), BorderLayout.CENTER);
        add(chartPanel, BorderLayout.SOUTH);

        refreshInsights();
    }

    public void refreshInsights() {
        try {
            List<Transaction> transactions = dao.findAll();

            if (transactions.isEmpty()) {
                insightsArea.setText("Not enough data for insights yet. Add some transactions!");
                chartPanel.removeAll();
                chartPanel.revalidate();
                chartPanel.repaint();
                return;
            }

            // Group expenses by month
            Map<String, BigDecimal> monthlyExpense = transactions.stream()
                    .filter(t -> t.getType().equalsIgnoreCase("EXPENSE"))
                    .collect(Collectors.groupingBy(
                            t -> t.getDate().getYear() + "-" + t.getDate().getMonthValue(),
                            Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)
                    ));

            List<String> months = new ArrayList<>(monthlyExpense.keySet());
            Collections.sort(months);

            List<Double> values = months.stream()
                    .map(m -> monthlyExpense.get(m).doubleValue())
                    .collect(Collectors.toList());

            // --- Simple trend analysis ---
            double growth = 0;
            if (values.size() >= 2) {
                double last = values.get(values.size() - 1);
                double prev = values.get(values.size() - 2);
                growth = ((last - prev) / prev) * 100;
            }

            StringBuilder sb = new StringBuilder("💡 Smart Insights\n\n");
            sb.append("🗓 Total Months Tracked: ").append(months.size()).append("\n");
            sb.append("💸 Average Monthly Expense: ₹")
                    .append(String.format("%.2f", values.stream().mapToDouble(v -> v).average().orElse(0)))
                    .append("\n");

            if (growth > 0)
                sb.append("📈 Your expenses increased by ").append(String.format("%.1f", growth)).append("% last month.\n");
            else if (growth < 0)
                sb.append("📉 Your expenses decreased by ").append(String.format("%.1f", -growth)).append("% last month.\n");
            else
                sb.append("➡️ Your spending was stable last month.\n");

            // --- Simple prediction (linear regression) ---
            double predicted = predictNext(values);
            sb.append("\n🤖 Prediction: You might spend around ₹")
                    .append(String.format("%.2f", predicted))
                    .append(" next month if current trends continue.\n");

            sb.append("\n💬 Tip: Try reviewing your top 3 categories weekly to control growing expenses.");

            insightsArea.setText(sb.toString());

            // --- Predictive chart ---
            drawChart(months, values, predicted);

        } catch (SQLException e) {
            insightsArea.setText("Error fetching data: " + e.getMessage());
        }
    }

    private void drawChart(List<String> months, List<Double> values, double predicted) {
        // Build chart
        XYChart chart = new XYChartBuilder()
                .width(700).height(350)
                .title("Expense Trend & AI Prediction")
                .xAxisTitle("Month")
                .yAxisTitle("Amount (₹)")
                .build();

        // Styler
        chart.getStyler().setLegendVisible(true);
        chart.getStyler().setMarkerSize(6);
        chart.getStyler().setChartBackgroundColor(Color.WHITE);
        chart.getStyler().setPlotBackgroundColor(Color.WHITE);
        chart.getStyler().setDecimalPattern("#,###.##");
        chart.getStyler().setXAxisTicksVisible(true);
        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNE);

        // Convert month labels to numeric X values (1,2,3...)
        List<Double> xVals = new ArrayList<>();
        for (int i = 0; i < months.size(); i++) {
            xVals.add((double) (i + 1));
        }

        // Add actual expense series (blue)
        XYSeries actualSeries = chart.addSeries("Expense", xVals, values);
        actualSeries.setLineColor(new Color(52, 120, 246));
        actualSeries.setMarker(new None());

        // Prepare an initial (short) prediction series that will be animated
        // Start it as identical to actual series (so animation extends it)
        List<Double> initialPredX = new ArrayList<>(xVals);
        List<Double> initialPredY = new ArrayList<>(values);
        XYSeries predictionSeries = chart.addSeries("AI Prediction", initialPredX, initialPredY);
        predictionSeries.setLineColor(Color.RED);
        predictionSeries.setMarker(new None());
        // correct way to set render style for XYSeries
        predictionSeries.setXYSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line);

        // Custom x-axis labels via formatter (works in 3.8.x)
        chart.setCustomXAxisTickLabelsFormatter(value -> {
            int idx = (int) Math.round(value) - 1;
            if (idx >= 0 && idx < months.size()) {
                return months.get(idx);
            } else if (idx == months.size()) {
                return "Next";
            } else {
                return "";
            }
        });

        // Put chart into panel
        chartPanel.removeAll();
        XChartPanel<XYChart> xChartPanel = new XChartPanel<>(chart);
        chartPanel.add(xChartPanel, BorderLayout.CENTER);
        chartPanel.revalidate();
        chartPanel.repaint();

        // ---- Animate prediction line smoothly ----
        final int totalSteps = 30;
        final int[] step = {0};
        double lastY = values.get(values.size() - 1);
        double targetY = predicted;
        double nextX = xVals.size() + 1.0;

        javax.swing.Timer animator = new javax.swing.Timer(40, null); // 25 FPS-ish
        animator.addActionListener(ev -> {
            step[0]++;
            double progress = Math.min(1.0, step[0] / (double) totalSteps);

            List<Double> animX = new ArrayList<>(xVals);
            List<Double> animY = new ArrayList<>(values);

            // interpolated predicted Y
            double currY = lastY + (targetY - lastY) * progress;
            animX.add(nextX);
            animY.add(currY);

            // update series (XChart will re-render on repaint)
            chart.updateXYSeries("AI Prediction", animX, animY, null);
            xChartPanel.repaint();

            if (progress >= 1.0) {
                ((javax.swing.Timer) ev.getSource()).stop();
            }
        });
        animator.start();
    }




    private double predictNext(List<Double> values) {
        // Simple linear regression: y = a + bx
        int n = values.size();
        if (n < 2) return values.get(n - 1);

        double sumX = 0, sumY = 0, sumXY = 0, sumXX = 0;
        for (int i = 0; i < n; i++) {
            sumX += i;
            sumY += values.get(i);
            sumXY += i * values.get(i);
            sumXX += i * i;
        }

        double b = (n * sumXY - sumX * sumY) / (n * sumXX - sumX * sumX);
        double a = (sumY - b * sumX) / n;

        return a + b * n; // next point (n)
    }
}

