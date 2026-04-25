package com.example.expense.service;

import com.example.expense.dto.BudgetAlertDTO;
import com.example.expense.exception.ResourceNotFoundException;
import com.example.expense.model.Budget;
import com.example.expense.model.Category;
import com.example.expense.repository.BudgetRepository;
import com.example.expense.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final CategoryService categoryService;
    private final TransactionRepository transactionRepository;

    public BudgetService(BudgetRepository budgetRepository,
                         CategoryService categoryService,
                         TransactionRepository transactionRepository) {
        this.budgetRepository = budgetRepository;
        this.categoryService = categoryService;
        this.transactionRepository = transactionRepository;
    }

    public Budget setBudget(Long categoryId, BigDecimal amount, YearMonth month) {
        Category category = categoryService.getOrThrow(categoryId);
        Budget budget = budgetRepository.findByCategoryIdAndMonth(categoryId, month)
                .orElse(new Budget());
        budget.setCategory(category);
        budget.setAmount(amount);
        budget.setMonth(month);
        return budgetRepository.save(budget);
    }

    @Transactional(readOnly = true)
    public BudgetAlertDTO getAlerts(YearMonth month) {
        List<Budget> budgets = budgetRepository.findByMonth(month);

        List<BudgetAlertDTO.AlertItem> alerts = budgets.stream().map(budget -> {
            LocalDate start = month.atDay(1);
            LocalDate end = month.atEndOfMonth();

            BigDecimal spending = transactionRepository
                    .sumByCategoryAndDateRange(budget.getCategory().getId(), start, end);

            double percentage = budget.getAmount().compareTo(BigDecimal.ZERO) == 0 ? 0 :
                    spending.multiply(BigDecimal.valueOf(100))
                            .divide(budget.getAmount(), 1, RoundingMode.HALF_UP)
                            .doubleValue();

            String status;
            String message;
            if (percentage >= 100) {
                BigDecimal overage = spending.subtract(budget.getAmount());
                status = "EXCEEDED";
                message = "Budget exceeded by RM " + overage.setScale(2, RoundingMode.HALF_UP);
            } else if (percentage >= 80) {
                status = "WARNING";
                message = "Approaching budget limit";
            } else {
                status = "OK";
                message = "Within budget";
            }

            BudgetAlertDTO.AlertItem item = new BudgetAlertDTO.AlertItem();
            item.setCategoryName(budget.getCategory().getName());
            item.setBudgetAmount(budget.getAmount());
            item.setCurrentSpending(spending);
            item.setPercentage(percentage);
            item.setStatus(status);
            item.setMessage(message);
            return item;
        }).collect(Collectors.toList());

        return new BudgetAlertDTO(alerts);
    }
}
