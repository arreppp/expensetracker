package com.example.expense.service;

import com.example.expense.dto.MonthlySummaryDTO;
import com.example.expense.model.enums.TransactionType;
import com.example.expense.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class SummaryService {

    private final TransactionRepository transactionRepository;

    public SummaryService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public MonthlySummaryDTO getMonthlySummary(int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate start = yearMonth.atDay(1);
        LocalDate end = yearMonth.atEndOfMonth();

        BigDecimal totalIncome = transactionRepository.sumByDateRangeAndType(start, end, TransactionType.INCOME);
        BigDecimal totalExpenses = transactionRepository.sumByDateRangeAndType(start, end, TransactionType.EXPENSE);
        BigDecimal netBalance = totalIncome.subtract(totalExpenses);

        List<Object[]> raw = transactionRepository.sumExpensesByCategoryAndDateRange(start, end);
        List<MonthlySummaryDTO.CategorySpendingDTO> expensesByCategory = raw.stream()
                .map(row -> new MonthlySummaryDTO.CategorySpendingDTO((String) row[0], (BigDecimal) row[1]))
                .collect(Collectors.toList());

        MonthlySummaryDTO summary = new MonthlySummaryDTO();
        summary.setMonth(yearMonth.toString());
        summary.setTotalIncome(totalIncome);
        summary.setTotalExpenses(totalExpenses);
        summary.setNetBalance(netBalance);
        summary.setExpensesByCategory(expensesByCategory);
        return summary;
    }

    public List<MonthlySummaryDTO.CategorySpendingDTO> getCategorySpending(int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate start = yearMonth.atDay(1);
        LocalDate end = yearMonth.atEndOfMonth();

        List<Object[]> raw = transactionRepository.sumExpensesByCategoryAndDateRange(start, end);
        return raw.stream()
                .map(row -> new MonthlySummaryDTO.CategorySpendingDTO((String) row[0], (BigDecimal) row[1]))
                .collect(Collectors.toList());
    }
}
