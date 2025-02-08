package ru.backend.academy.hometask4.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;
import ru.backend.academy.hometask4.config.NonDatabaseCondition;
import ru.backend.academy.hometask4.converter.CategoryConverter;
import ru.backend.academy.hometask4.dto.CategoryDto;
import ru.backend.academy.hometask4.dto.NewOrUpdateCategoryDto;
import ru.backend.academy.hometask4.model.Category;
import ru.backend.academy.hometask4.repository.CategoryRepository;
import ru.backend.academy.hometask4.service.CategoryService;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Conditional(NonDatabaseCondition.class)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    private final CategoryConverter categoryConverter;
    @Override
    public List<CategoryDto> readAllCategories() {
        return categoryRepository.getAllCategories().stream().map(categoryConverter::convertToDto).toList();
    }

    @Override
    public CategoryDto createNewCategory(NewOrUpdateCategoryDto categoryDto) {
        Category newCategory = categoryConverter.convertToNewEntity(categoryDto);
        return categoryConverter.convertToDto(categoryRepository.addNewCategory(newCategory));
    }

    @Override
    public CategoryDto updateCategory(String categoryId, NewOrUpdateCategoryDto categoryDto) {
        Category category = categoryConverter.convertToNewEntity(categoryDto);
        category.setId(categoryId);
        return categoryConverter.convertToDto(categoryRepository.updateCategoryById(categoryId, category));
    }

    @Override
    public void deleteCategory(String categoryId) {
        categoryRepository.deleteCategoryById(categoryId);
    }

}
