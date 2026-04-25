package com.example.expense.service;

import com.example.expense.dto.CategoryDTO;
import com.example.expense.exception.ResourceNotFoundException;
import com.example.expense.model.Category;
import com.example.expense.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public CategoryDTO create(CategoryDTO dto) {
        Category category = new Category(dto.getName(), dto.getType());
        return toDTO(categoryRepository.save(category));
    }

    @Transactional(readOnly = true)
    public List<CategoryDTO> findAll() {
        return categoryRepository.findAll()
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CategoryDTO findById(Long id) {
        return toDTO(getOrThrow(id));
    }

    public CategoryDTO update(Long id, CategoryDTO dto) {
        Category category = getOrThrow(id);
        category.setName(dto.getName());
        category.setType(dto.getType());
        return toDTO(categoryRepository.save(category));
    }

    public void delete(Long id) {
        getOrThrow(id);
        categoryRepository.deleteById(id);
    }

    public Category getOrThrow(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));
    }

    public CategoryDTO toDTO(Category category) {
        return new CategoryDTO(category.getId(), category.getName(), category.getType());
    }
}
