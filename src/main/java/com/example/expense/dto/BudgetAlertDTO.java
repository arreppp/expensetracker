package com.example.expense.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

public class BudgetAlertDTO {

    private List<AlertItem> alerts;

    public BudgetAlertDTO() {}

    public BudgetAlertDTO(List<AlertItem> alerts) {
        this.alerts = alerts;
    }

    public List<AlertItem> getAlerts() { return alerts; }
    public void setAlerts(List<AlertItem> alerts) { this.alerts = alerts; }

    public static class AlertItem {
        private String categoryName;
        private BigDecimal budgetAmount;
        private BigDecimal currentSpending;
        private double percentage;
        private String status;
        private String message;

        public AlertItem() {}

        public String getCategoryName() { return categoryName; }
        public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

        public BigDecimal getBudgetAmount() { return budgetAmount; }
        public void setBudgetAmount(BigDecimal budgetAmount) { this.budgetAmount = budgetAmount; }

        public BigDecimal getCurrentSpending() { return currentSpending; }
        public void setCurrentSpending(BigDecimal currentSpending) { this.currentSpending = currentSpending; }

        public double getPercentage() { return percentage; }
        public void setPercentage(double percentage) { this.percentage = percentage; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    // Request DTO for creating a budget
    public static class BudgetRequest {
        @NotNull(message = "Category ID is required")
        private Long categoryId;

        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be greater than 0")
        private BigDecimal amount;

        @NotNull(message = "Month is required")
        private YearMonth month;

        public Long getCategoryId() { return categoryId; }
        public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }

        public YearMonth getMonth() { return month; }
        public void setMonth(YearMonth month) { this.month = month; }
    }
}
