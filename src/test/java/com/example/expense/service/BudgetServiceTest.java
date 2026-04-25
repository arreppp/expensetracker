package com.example.expense.service;

import com.example.expense.dto.BudgetAlertDTO;
import com.example.expense.model.Budget;
import com.example.expense.model.Category;
import com.example.expense.model.enums.TransactionType;
import com.example.expense.repository.BudgetRepository;
import com.example.expense.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BudgetServiceTest {

    @Mock
    private BudgetRepository budgetRepository;

    @Mock
    private CategoryService categoryService;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private BudgetService budgetService;

    private Category foodCategory;
    private Budget foodBudget;
    private YearMonth april2026;

    @BeforeEach
    void setUp() {
        foodCategory = new Category("Food", TransactionType.EXPENSE);
        foodCategory.setId(1L);

        april2026 = YearMonth.of(2026, 4);

        foodBudget = new Budget();
        foodBudget.setId(1L);
        foodBudget.setCategory(foodCategory);
        foodBudget.setAmount(new BigDecimal("2000.00"));
        foodBudget.setMonth(april2026);
    }

    @Test
    void getAlerts_statusExceeded_whenSpendingOver100Percent() {
        when(budgetRepository.findByMonth(april2026)).thenReturn(List.of(foodBudget));
        when(transactionRepository.sumByCategoryAndDateRange(eq(1L), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(new BigDecimal("2100.00"));

        BudgetAlertDTO result = budgetService.getAlerts(april2026);

        assertThat(result.getAlerts()).hasSize(1);
        BudgetAlertDTO.AlertItem alert = result.getAlerts().get(0);
        assertThat(alert.getStatus()).isEqualTo("EXCEEDED");
        assertThat(alert.getPercentage()).isGreaterThan(100.0);
        assertThat(alert.getMessage()).contains("exceeded");
    }

    @Test
    void getAlerts_statusWarning_whenSpendingBetween80And100Percent() {
        when(budgetRepository.findByMonth(april2026)).thenReturn(List.of(foodBudget));
        when(transactionRepository.sumByCategoryAndDateRange(eq(1L), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(new BigDecimal("1800.00")); // 90%

        BudgetAlertDTO result = budgetService.getAlerts(april2026);

        BudgetAlertDTO.AlertItem alert = result.getAlerts().get(0);
        assertThat(alert.getStatus()).isEqualTo("WARNING");
        assertThat(alert.getPercentage()).isBetween(80.0, 100.0);
    }

    @Test
    void getAlerts_statusOk_whenSpendingUnder80Percent() {
        when(budgetRepository.findByMonth(april2026)).thenReturn(List.of(foodBudget));
        when(transactionRepository.sumByCategoryAndDateRange(eq(1L), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(new BigDecimal("1000.00")); // 50%

        BudgetAlertDTO result = budgetService.getAlerts(april2026);

        BudgetAlertDTO.AlertItem alert = result.getAlerts().get(0);
        assertThat(alert.getStatus()).isEqualTo("OK");
        assertThat(alert.getPercentage()).isLessThan(80.0);
    }

    @Test
    void setBudget_createsNewBudget_whenNoneExists() {
        when(categoryService.getOrThrow(1L)).thenReturn(foodCategory);
        when(budgetRepository.findByCategoryIdAndMonth(1L, april2026)).thenReturn(Optional.empty());
        when(budgetRepository.save(any(Budget.class))).thenReturn(foodBudget);

        Budget result = budgetService.setBudget(1L, new BigDecimal("2000.00"), april2026);

        assertThat(result.getAmount()).isEqualByComparingTo("2000.00");
        verify(budgetRepository).save(any(Budget.class));
    }

    @Test
    void setBudget_updatesExistingBudget_whenAlreadyExists() {
        when(categoryService.getOrThrow(1L)).thenReturn(foodCategory);
        when(budgetRepository.findByCategoryIdAndMonth(1L, april2026)).thenReturn(Optional.of(foodBudget));
        when(budgetRepository.save(any(Budget.class))).thenReturn(foodBudget);

        budgetService.setBudget(1L, new BigDecimal("2500.00"), april2026);

        verify(budgetRepository, times(1)).save(any(Budget.class));
    }
}
