package com.financetracker.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Transaction {
    private int id;
    private LocalDate date;
    private String description;
    private String category;
    private BigDecimal amount;
    private String type; // "INCOME" or "EXPENSE"

    // 🔹 Constructors
    public Transaction() {}

    public Transaction(LocalDate date, String description, String category, BigDecimal amount, String type) {
        this.date = date;
        this.description = description;
        this.category = category;
        this.amount = amount;
        this.type = type;
    }

    public Transaction(int id, LocalDate date, String description, String category, BigDecimal amount, String type) {
        this.id = id;
        this.date = date;
        this.description = description;
        this.category = category;
        this.amount = amount;
        this.type = type;
    }

    // 🔹 Getters & Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    // 🔹 For debugging or console display
    @Override
    public String toString() {
        return String.format("Transaction{id=%d, date=%s, description='%s', category='%s', amount=%s, type='%s'}",
                id, date, description, category, amount, type);
    }
}
