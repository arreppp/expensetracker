package com.example.expense.controller;

import com.example.expense.dto.BudgetAlertDTO;
import com.example.expense.model.Budget;
import com.example.expense.service.BudgetService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;

@RestController
@RequestMapping("/api/budgets")
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @PostMapping
    public ResponseEntity<Budget> setBudget(@Valid @RequestBody BudgetAlertDTO.BudgetRequest request) {
        Budget budget = budgetService.setBudget(
                request.getCategoryId(),
                request.getAmount(),
                request.getMonth()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(budget);
    }

    @GetMapping("/alerts")
    public ResponseEntity<BudgetAlertDTO> getAlerts(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        YearMonth yearMonth = (year != null && month != null)
                ? YearMonth.of(year, month)
                : YearMonth.now();
        return ResponseEntity.ok(budgetService.getAlerts(yearMonth));
    }
}
