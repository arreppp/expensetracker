package com.example.expense.repository;

import com.example.expense.model.Transaction;
import com.example.expense.model.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByTransactionDateBetween(LocalDate startDate, LocalDate endDate);

    List<Transaction> findByCategoryId(Long categoryId);

    List<Transaction> findByCategoryType(TransactionType type);

    @Query("SELECT t FROM Transaction t WHERE t.transactionDate BETWEEN :start AND :end AND t.category.type = :type")
    List<Transaction> findByDateRangeAndType(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end,
            @Param("type") TransactionType type);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
           "WHERE t.transactionDate BETWEEN :start AND :end AND t.category.type = :type")
    BigDecimal sumByDateRangeAndType(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end,
            @Param("type") TransactionType type);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
           "WHERE t.category.id = :categoryId AND t.transactionDate BETWEEN :start AND :end")
    BigDecimal sumByCategoryAndDateRange(
            @Param("categoryId") Long categoryId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end);

    @Query("SELECT t.category.name, SUM(t.amount) FROM Transaction t " +
           "WHERE t.transactionDate BETWEEN :start AND :end AND t.category.type = 'EXPENSE' " +
           "GROUP BY t.category.name")
    List<Object[]> sumExpensesByCategoryAndDateRange(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end);
}
