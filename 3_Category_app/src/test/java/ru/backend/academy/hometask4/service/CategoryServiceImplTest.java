package ru.backend.academy.hometask4.service;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.backend.academy.hometask4.converter.CategoryConverter;
import ru.backend.academy.hometask4.dto.CategoryDto;
import ru.backend.academy.hometask4.dto.NewOrUpdateCategoryDto;
import ru.backend.academy.hometask4.exception.already_exists.CategoryAlreadyExistsException;
import ru.backend.academy.hometask4.exception.not_found.CategoryNotFoundException;
import ru.backend.academy.hometask4.model.Category;
import ru.backend.academy.hometask4.repository.CategoryRepository;
import ru.backend.academy.hometask4.service.impl.CategoryServiceImpl;
import ru.backend.academy.hometask4.util.UniqueIdGenerator;

import java.util.Collections;
import java.util.List;
import java.lang.String;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryConverter categoryConverter;

    @InjectMocks
    private CategoryServiceImpl categoryService;


    @Nested
    @DisplayName("Testing readAllCategories method")
    class ReadAllCategoriesTests {

        @Test
        @DisplayName("Should return a list with one category when only one category exists")
        void should_return_list_with_one_category_when_only_one_category_exists() {
            Category category = new Category();
            CategoryDto categoryDto = new CategoryDto();
            categoryDto.setId(category.getId());
            categoryDto.setTitle(category.getTitle());
            categoryDto.setUrl(category.getUrl());
            when(categoryRepository.getAllCategories()).thenReturn(Collections.singletonList(category));
            when(categoryConverter.convertToDto(category)).thenReturn(categoryDto);
            List<CategoryDto> result = categoryService.readAllCategories();
            List<CategoryDto> expectedList = List.of(categoryDto);
            assertEquals(expectedList, result);
            verify(categoryRepository, times(1)).getAllCategories();
            verify(categoryConverter, times(1)).convertToDto(category);
        }


    }

    @Nested
    @DisplayName("Testing create_new_category method")
    class CreateNewCategoryTests {

        @Test
        @DisplayName("Should return added category when category added successfully")
        void should_return_added_category_when_category_added_successfully() {
            NewOrUpdateCategoryDto categoryDto = new NewOrUpdateCategoryDto("new category");
            Category newCategory = Category.builder().url("new_category").title("new category").build();
            when(categoryConverter.convertToNewEntity(categoryDto)).thenReturn(newCategory);

            String addedCategoryId = UniqueIdGenerator.generateUniqueId();
            Category addedCategory = new Category(addedCategoryId, newCategory.getUrl(), newCategory.getTitle());
            CategoryDto addedCategoryDto = new CategoryDto(addedCategory.getId(), addedCategory.getUrl(), addedCategory.getTitle());

            when(categoryRepository.addNewCategory(newCategory)).thenReturn(addedCategory);
            when(categoryConverter.convertToDto(addedCategory)).thenReturn(addedCategoryDto);

            CategoryDto result = categoryService.createNewCategory(categoryDto);

            assertEquals(addedCategoryDto, result);

            verify(categoryConverter, times(1)).convertToNewEntity(categoryDto);
            verify(categoryRepository, times(1)).addNewCategory(newCategory);
            verify(categoryConverter, times(1)).convertToDto(addedCategory);
        }

        @Test
        @DisplayName("Should throw exception when category already exists")
        void should_throw_exception_when_category_already_exists() {
            Category existingCategory = Category.builder().url("existing_category").build();
            when(categoryConverter.convertToNewEntity(any())).thenReturn(existingCategory);
            when(categoryRepository.addNewCategory(existingCategory)).thenThrow(CategoryAlreadyExistsException.class);

            assertThrows(CategoryAlreadyExistsException.class, () -> categoryService.createNewCategory(any()));

            verify(categoryRepository, times(1)).addNewCategory(existingCategory);
        }
    }



    @Nested
    @DisplayName("Testing updateCategory method")
    class UpdateCategoryTests {

        @Test
        @DisplayName("Should return updated category when category updated successfully")
        void should_return_updated_category_when_category_updated_correct() {
            NewOrUpdateCategoryDto categoryDto = new NewOrUpdateCategoryDto("new category");
            Category categoryForUpdate = Category.builder().url("new_category").build();
            when(categoryConverter.convertToNewEntity(categoryDto)).thenReturn(categoryForUpdate);

            String categoryId = UniqueIdGenerator.generateUniqueId();
            Category updatedCategory = new Category(categoryId, categoryForUpdate.getUrl(), categoryForUpdate.getTitle());
            CategoryDto updatedCategoryDto = new CategoryDto(updatedCategory.getId(), updatedCategory.getUrl(), updatedCategory.getTitle());

            when(categoryRepository.updateCategoryById(categoryId, categoryForUpdate)).thenReturn(updatedCategory);
            when(categoryConverter.convertToDto(updatedCategory)).thenReturn(updatedCategoryDto);

            CategoryDto result = categoryService.updateCategory(categoryId, categoryDto);

            assertEquals(updatedCategoryDto, result);

            verify(categoryConverter, times(1)).convertToNewEntity(categoryDto);
            verify(categoryRepository, times(1)).updateCategoryById(categoryId, categoryForUpdate);
            verify(categoryConverter, times(1)).convertToDto(updatedCategory);
        }

        @Test
        @DisplayName("Should throw exception when category not found")
        void should_throw_exception_when_category_not_found() {
            NewOrUpdateCategoryDto categoryDto = new NewOrUpdateCategoryDto("new category");
            Category categoryForUpdate = Category.builder().url("new_category").build();
            when(categoryConverter.convertToNewEntity(categoryDto)).thenReturn(categoryForUpdate);

            String categoryId = UniqueIdGenerator.generateUniqueId();
            when(categoryRepository.updateCategoryById(categoryId, categoryForUpdate)).thenThrow(CategoryNotFoundException.class);

            assertThrows(CategoryNotFoundException.class, () -> categoryService.updateCategory(categoryId, categoryDto));

            verify(categoryConverter, times(1)).convertToNewEntity(categoryDto);
            verify(categoryRepository, times(1)).updateCategoryById(categoryId, categoryForUpdate);
        }
    }


    @Nested
    @DisplayName("Testing deleteCategory method")
    class DeleteCategoryTests {

        @Test
        @DisplayName("Should throw exception when category not found")
        void should_throw_exception_when_category_not_found() {
            String categoryId = UniqueIdGenerator.generateUniqueId();
            doThrow(new CategoryNotFoundException(categoryId)).when(categoryRepository).deleteCategoryById(categoryId);
            assertThrows(CategoryNotFoundException.class, () -> categoryService.deleteCategory(categoryId));
            verify(categoryRepository, times(1)).deleteCategoryById(categoryId);
        }

        @Test
        @DisplayName("Should delete category successfully")
        void should_delete_correct_category() {
            String categoryId = UniqueIdGenerator.generateUniqueId();
            doNothing().when(categoryRepository).deleteCategoryById(categoryId);
            categoryService.deleteCategory(categoryId);
            verify(categoryRepository, times(1)).deleteCategoryById(categoryId);
        }
    }



}
