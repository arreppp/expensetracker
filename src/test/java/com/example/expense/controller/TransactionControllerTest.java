package com.example.expense.controller;

import com.example.expense.dto.CategoryDTO;
import com.example.expense.dto.TransactionDTO;
import com.example.expense.exception.ResourceNotFoundException;
import com.example.expense.model.enums.TransactionType;
import com.example.expense.service.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TransactionService transactionService;

    private TransactionDTO sampleTransaction;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());

        CategoryDTO category = new CategoryDTO(3L, "Food", TransactionType.EXPENSE);

        sampleTransaction = new TransactionDTO();
        sampleTransaction.setId(15L);
        sampleTransaction.setAmount(new BigDecimal("1500.00"));
        sampleTransaction.setDescription("Grocery shopping at Tesco");
        sampleTransaction.setTransactionDate(LocalDate.of(2026, 4, 25));
        sampleTransaction.setCategoryId(3L);
        sampleTransaction.setCategory(category);
        sampleTransaction.setCreatedAt(LocalDateTime.of(2026, 4, 25, 14, 30, 0));
    }

    @Test
    void POST_transactions_returns201_withCreatedDTO() throws Exception {
        TransactionDTO request = new TransactionDTO();
        request.setAmount(new BigDecimal("1500.00"));
        request.setDescription("Grocery shopping at Tesco");
        request.setTransactionDate(LocalDate.of(2026, 4, 25));
        request.setCategoryId(3L);

        when(transactionService.create(any(TransactionDTO.class))).thenReturn(sampleTransaction);

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(15))
                .andExpect(jsonPath("$.amount").value(1500.00))
                .andExpect(jsonPath("$.category.name").value("Food"));
    }

    @Test
    void POST_transactions_returns400_whenAmountMissing() throws Exception {
        TransactionDTO request = new TransactionDTO();
        request.setDescription("No amount");
        request.setTransactionDate(LocalDate.now());
        request.setCategoryId(1L);

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void GET_transactions_returnsAll() throws Exception {
        when(transactionService.findAll(null, null, null, null)).thenReturn(List.of(sampleTransaction));

        mockMvc.perform(get("/api/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].description").value("Grocery shopping at Tesco"));
    }

    @Test
    void GET_transactions_byId_returns200_whenFound() throws Exception {
        when(transactionService.findById(15L)).thenReturn(sampleTransaction);

        mockMvc.perform(get("/api/transactions/15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(15));
    }

    @Test
    void GET_transactions_byId_returns404_whenNotFound() throws Exception {
        when(transactionService.findById(99L)).thenThrow(new ResourceNotFoundException("Transaction", 99L));

        mockMvc.perform(get("/api/transactions/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void DELETE_transactions_returns204() throws Exception {
        doNothing().when(transactionService).delete(15L);

        mockMvc.perform(delete("/api/transactions/15"))
                .andExpect(status().isNoContent());
    }
}
