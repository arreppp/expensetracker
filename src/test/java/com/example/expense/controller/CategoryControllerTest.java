package com.example.expense.controller;

import com.example.expense.dto.CategoryDTO;
import com.example.expense.exception.ResourceNotFoundException;
import com.example.expense.model.enums.TransactionType;
import com.example.expense.service.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoryService categoryService;

    @Test
    void POST_categories_returns201_withCreatedDTO() throws Exception {
        CategoryDTO request = new CategoryDTO(null, "Food", TransactionType.EXPENSE);
        CategoryDTO response = new CategoryDTO(1L, "Food", TransactionType.EXPENSE);

        when(categoryService.create(any(CategoryDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Food"))
                .andExpect(jsonPath("$.type").value("EXPENSE"));
    }

    @Test
    void POST_categories_returns400_whenNameBlank() throws Exception {
        CategoryDTO request = new CategoryDTO(null, "", TransactionType.EXPENSE);

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void GET_categories_returnsAll() throws Exception {
        List<CategoryDTO> categories = List.of(
                new CategoryDTO(1L, "Food", TransactionType.EXPENSE),
                new CategoryDTO(2L, "Salary", TransactionType.INCOME)
        );

        when(categoryService.findAll()).thenReturn(categories);

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Food"))
                .andExpect(jsonPath("$[1].name").value("Salary"));
    }

    @Test
    void GET_categories_byId_returns200_whenFound() throws Exception {
        CategoryDTO response = new CategoryDTO(1L, "Food", TransactionType.EXPENSE);
        when(categoryService.findById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void GET_categories_byId_returns404_whenNotFound() throws Exception {
        when(categoryService.findById(99L)).thenThrow(new ResourceNotFoundException("Category", 99L));

        mockMvc.perform(get("/api/categories/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void PUT_categories_returns200_withUpdatedDTO() throws Exception {
        CategoryDTO request = new CategoryDTO(null, "Groceries", TransactionType.EXPENSE);
        CategoryDTO response = new CategoryDTO(1L, "Groceries", TransactionType.EXPENSE);

        when(categoryService.update(eq(1L), any(CategoryDTO.class))).thenReturn(response);

        mockMvc.perform(put("/api/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Groceries"));
    }

    @Test
    void DELETE_categories_returns204() throws Exception {
        doNothing().when(categoryService).delete(1L);

        mockMvc.perform(delete("/api/categories/1"))
                .andExpect(status().isNoContent());
    }
}
