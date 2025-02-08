package ru.backend.academy.hometask4.service;

import ru.backend.academy.hometask4.dto.CategoryDto;
import ru.backend.academy.hometask4.dto.NewOrUpdateCategoryDto;

import java.util.List;

public interface CategoryService {
    List<CategoryDto> readAllCategories();

    CategoryDto createNewCategory(NewOrUpdateCategoryDto categoryDto);

    CategoryDto updateCategory(String categoryId, NewOrUpdateCategoryDto categoryDto);

    void deleteCategory(String categoryId);
}
