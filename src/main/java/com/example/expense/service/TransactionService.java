package com.example.expense.service;

import com.example.expense.dto.CategoryDTO;
import com.example.expense.dto.TransactionDTO;
import com.example.expense.exception.ResourceNotFoundException;
import com.example.expense.model.Category;
import com.example.expense.model.Transaction;
import com.example.expense.model.enums.TransactionType;
import com.example.expense.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryService categoryService;

    public TransactionService(TransactionRepository transactionRepository, CategoryService categoryService) {
        this.transactionRepository = transactionRepository;
        this.categoryService = categoryService;
    }

    public TransactionDTO create(TransactionDTO dto) {
        Category category = categoryService.getOrThrow(dto.getCategoryId());
        Transaction transaction = new Transaction();
        transaction.setAmount(dto.getAmount());
        transaction.setDescription(dto.getDescription());
        transaction.setTransactionDate(dto.getTransactionDate());
        transaction.setCategory(category);
        return toDTO(transactionRepository.save(transaction));
    }

    @Transactional(readOnly = true)
    public List<TransactionDTO> findAll(LocalDate startDate, LocalDate endDate, Long categoryId, TransactionType type) {
        List<Transaction> transactions;

        if (startDate != null && endDate != null && type != null) {
            transactions = transactionRepository.findByDateRangeAndType(startDate, endDate, type);
        } else if (startDate != null && endDate != null) {
            transactions = transactionRepository.findByTransactionDateBetween(startDate, endDate);
        } else if (categoryId != null) {
            transactions = transactionRepository.findByCategoryId(categoryId);
        } else if (type != null) {
            transactions = transactionRepository.findByCategoryType(type);
        } else {
            transactions = transactionRepository.findAll();
        }

        return transactions.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TransactionDTO findById(Long id) {
        return toDTO(getOrThrow(id));
    }

    public TransactionDTO update(Long id, TransactionDTO dto) {
        Transaction transaction = getOrThrow(id);
        Category category = categoryService.getOrThrow(dto.getCategoryId());
        transaction.setAmount(dto.getAmount());
        transaction.setDescription(dto.getDescription());
        transaction.setTransactionDate(dto.getTransactionDate());
        transaction.setCategory(category);
        return toDTO(transactionRepository.save(transaction));
    }

    public void delete(Long id) {
        getOrThrow(id);
        transactionRepository.deleteById(id);
    }

    private Transaction getOrThrow(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", id));
    }

    public TransactionDTO toDTO(Transaction t) {
        TransactionDTO dto = new TransactionDTO();
        dto.setId(t.getId());
        dto.setAmount(t.getAmount());
        dto.setDescription(t.getDescription());
        dto.setTransactionDate(t.getTransactionDate());
        dto.setCreatedAt(t.getCreatedAt());
        dto.setCategoryId(t.getCategory().getId());
        dto.setCategory(categoryService.toDTO(t.getCategory()));
        return dto;
    }
}
