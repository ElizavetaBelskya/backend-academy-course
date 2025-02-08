package ru.backend.academy.hometask4.service.impl;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.backend.academy.hometask4.converter.CategoryConverter;
import ru.backend.academy.hometask4.model.Category;
import ru.backend.academy.hometask4.dto.category.CategoryDto;
import ru.backend.academy.hometask4.dto.category.NewOrUpdateCategoryDto;
import ru.backend.academy.hometask4.exception.already_exists.CategoryAlreadyExistsException;
import ru.backend.academy.hometask4.exception.not_found.CategoryNotFoundException;
import ru.backend.academy.hometask4.repository.CategoryRepository;
import ru.backend.academy.hometask4.service.CategoryService;
import ru.backend.academy.hometask4.util.UniqueIdGenerator;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    @PostConstruct
    private void addDefaultCategory() {
        if (categoryRepository.findByUrl(categoryRepository.defaultCategory.getUrl()).isEmpty()) {
            categoryRepository.save(categoryRepository.defaultCategory);
        }
    }

    private final CategoryRepository categoryRepository;

    private final CategoryConverter categoryConverter;

    @Override
    public List<CategoryDto> readAllCategories() {
        return categoryRepository.findAll().stream().map(categoryConverter::convertToDto).toList();
    }

    @Override
    public CategoryDto createNewCategory(NewOrUpdateCategoryDto categoryDto) {
        Category newCategory = categoryConverter.convertToNewDatabaseEntity(categoryDto);
        newCategory.setId(UniqueIdGenerator.generateUniqueId());
        checkUniqueUrlAndTitle(newCategory);
        return categoryConverter.convertToDto(categoryRepository.save(newCategory));
    }

    @Override
    public CategoryDto updateCategory(String categoryId, NewOrUpdateCategoryDto categoryDto) {
        Category categoryToUpdate = categoryConverter.convertToNewDatabaseEntity(categoryDto);
        categoryToUpdate.setId(categoryId);
        checkUniqueUrlAndTitle(categoryToUpdate);
        if (categoryRepository.findById(categoryId).isPresent()) {
            return categoryConverter.convertToDto(categoryRepository.save(categoryToUpdate));
        }  else {
            throw new CategoryNotFoundException(categoryId);
        }
    }

    @Override
    public void deleteCategory(String categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(()
                -> new CategoryNotFoundException(categoryId));
        categoryRepository.delete(category);
    }

    private void checkUniqueUrlAndTitle(Category category) {
        String url = category.getUrl();
        Optional<Category> sameCategory = categoryRepository.findByUrl(url);
        if (sameCategory.isPresent()) {
            if (category.getTitle().equals(sameCategory.get().getTitle())) {
                throw new CategoryAlreadyExistsException();
            } else {
                category.setUrl(category.getUrl() + "_" + category.hashCode());
            }
        }
    }

}
