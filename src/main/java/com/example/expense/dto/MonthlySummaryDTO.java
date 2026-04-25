package com.example.expense.dto;

import java.math.BigDecimal;
import java.util.List;

public class MonthlySummaryDTO {

    private String month;
    private BigDecimal totalIncome;
    private BigDecimal totalExpenses;
    private BigDecimal netBalance;
    private List<CategorySpendingDTO> expensesByCategory;

    public MonthlySummaryDTO() {}

    public String getMonth() { return month; }
    public void setMonth(String month) { this.month = month; }

    public BigDecimal getTotalIncome() { return totalIncome; }
    public void setTotalIncome(BigDecimal totalIncome) { this.totalIncome = totalIncome; }

    public BigDecimal getTotalExpenses() { return totalExpenses; }
    public void setTotalExpenses(BigDecimal totalExpenses) { this.totalExpenses = totalExpenses; }

    public BigDecimal getNetBalance() { return netBalance; }
    public void setNetBalance(BigDecimal netBalance) { this.netBalance = netBalance; }

    public List<CategorySpendingDTO> getExpensesByCategory() { return expensesByCategory; }
    public void setExpensesByCategory(List<CategorySpendingDTO> expensesByCategory) {
        this.expensesByCategory = expensesByCategory;
    }

    public static class CategorySpendingDTO {
        private String categoryName;
        private BigDecimal amount;

        public CategorySpendingDTO() {}

        public CategorySpendingDTO(String categoryName, BigDecimal amount) {
            this.categoryName = categoryName;
            this.amount = amount;
        }

        public String getCategoryName() { return categoryName; }
        public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
    }
}
