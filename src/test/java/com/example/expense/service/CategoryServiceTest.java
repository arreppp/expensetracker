package com.example.expense.service;

import com.example.expense.dto.CategoryDTO;
import com.example.expense.exception.ResourceNotFoundException;
import com.example.expense.model.Category;
import com.example.expense.model.enums.TransactionType;
import com.example.expense.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category category;
    private CategoryDTO categoryDTO;

    @BeforeEach
    void setUp() {
        category = new Category("Food", TransactionType.EXPENSE);
        category.setId(1L);

        categoryDTO = new CategoryDTO(null, "Food", TransactionType.EXPENSE);
    }

    @Test
    void create_savesAndReturnsDTO() {
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        CategoryDTO result = categoryService.create(categoryDTO);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Food");
        assertThat(result.getType()).isEqualTo(TransactionType.EXPENSE);
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void findAll_returnsAllCategories() {
        when(categoryRepository.findAll()).thenReturn(List.of(category));

        List<CategoryDTO> result = categoryService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Food");
    }

    @Test
    void findById_returnsDTO_whenFound() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        CategoryDTO result = categoryService.findById(1L);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void findById_throwsResourceNotFoundException_whenNotFound() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void update_updatesFieldsAndReturnsDTO() {
        CategoryDTO updateDTO = new CategoryDTO(null, "Groceries", TransactionType.EXPENSE);
        Category updated = new Category("Groceries", TransactionType.EXPENSE);
        updated.setId(1L);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenReturn(updated);

        CategoryDTO result = categoryService.update(1L, updateDTO);

        assertThat(result.getName()).isEqualTo("Groceries");
    }

    @Test
    void delete_callsDeleteById_whenExists() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        categoryService.delete(1L);

        verify(categoryRepository).deleteById(1L);
    }

    @Test
    void delete_throwsResourceNotFoundException_whenNotFound() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
