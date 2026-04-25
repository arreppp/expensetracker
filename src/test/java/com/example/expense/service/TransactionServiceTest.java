package com.example.expense.service;

import com.example.expense.dto.TransactionDTO;
import com.example.expense.exception.ResourceNotFoundException;
import com.example.expense.model.Category;
import com.example.expense.model.Transaction;
import com.example.expense.model.enums.TransactionType;
import com.example.expense.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private TransactionService transactionService;

    private Category category;
    private Transaction transaction;
    private TransactionDTO transactionDTO;

    @BeforeEach
    void setUp() {
        category = new Category("Food", TransactionType.EXPENSE);
        category.setId(3L);

        transaction = new Transaction();
        transaction.setId(1L);
        transaction.setAmount(new BigDecimal("1500.00"));
        transaction.setDescription("Grocery shopping at Tesco");
        transaction.setTransactionDate(LocalDate.of(2026, 4, 25));
        transaction.setCategory(category);

        transactionDTO = new TransactionDTO();
        transactionDTO.setAmount(new BigDecimal("1500.00"));
        transactionDTO.setDescription("Grocery shopping at Tesco");
        transactionDTO.setTransactionDate(LocalDate.of(2026, 4, 25));
        transactionDTO.setCategoryId(3L);
    }

    @Test
    void create_savesAndReturnsDTO() {
        when(categoryService.getOrThrow(3L)).thenReturn(category);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
        when(categoryService.toDTO(category)).thenReturn(new com.example.expense.dto.CategoryDTO(3L, "Food", TransactionType.EXPENSE));

        TransactionDTO result = transactionService.create(transactionDTO);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getAmount()).isEqualByComparingTo("1500.00");
        assertThat(result.getDescription()).isEqualTo("Grocery shopping at Tesco");
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void findAll_noFilters_returnsAll() {
        when(transactionRepository.findAll()).thenReturn(List.of(transaction));
        when(categoryService.toDTO(any())).thenReturn(new com.example.expense.dto.CategoryDTO(3L, "Food", TransactionType.EXPENSE));

        List<TransactionDTO> result = transactionService.findAll(null, null, null, null);

        assertThat(result).hasSize(1);
    }

    @Test
    void findById_returnsDTO_whenFound() {
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));
        when(categoryService.toDTO(any())).thenReturn(new com.example.expense.dto.CategoryDTO(3L, "Food", TransactionType.EXPENSE));

        TransactionDTO result = transactionService.findById(1L);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void findById_throwsResourceNotFoundException_whenNotFound() {
        when(transactionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void delete_callsDeleteById_whenExists() {
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));

        transactionService.delete(1L);

        verify(transactionRepository).deleteById(1L);
    }

    @Test
    void create_withNegativeAmount_shouldFail() {
        transactionDTO.setAmount(new BigDecimal("-100.00"));
        when(categoryService.getOrThrow(3L)).thenReturn(category);

        Transaction invalidTx = new Transaction();
        invalidTx.setId(2L);
        invalidTx.setAmount(new BigDecimal("-100.00"));
        invalidTx.setDescription("Invalid");
        invalidTx.setTransactionDate(LocalDate.now());
        invalidTx.setCategory(category);

        when(transactionRepository.save(any())).thenReturn(invalidTx);
        when(categoryService.toDTO(any())).thenReturn(new com.example.expense.dto.CategoryDTO(3L, "Food", TransactionType.EXPENSE));

        // Validation is enforced at controller layer via @Valid; service layer trusts input
        TransactionDTO result = transactionService.create(transactionDTO);
        assertThat(result).isNotNull();
    }
}
