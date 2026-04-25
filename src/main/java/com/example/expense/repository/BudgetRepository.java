package com.example.expense.repository;

import com.example.expense.model.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {
    Optional<Budget> findByCategoryIdAndMonth(Long categoryId, YearMonth month);
    List<Budget> findByMonth(YearMonth month);
}
