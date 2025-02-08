package ru.backend.academy.hometask4.repository.category;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.backend.academy.hometask4.exception.already_exists.CategoryAlreadyExistsException;
import ru.backend.academy.hometask4.exception.not_found.CategoryNotFoundException;
import ru.backend.academy.hometask4.model.Category;
import ru.backend.academy.hometask4.repository.impl.CategoryRepositoryCsvFileImpl;
import ru.backend.academy.hometask4.util.CsvUtil;
import ru.backend.academy.hometask4.util.UniqueIdGenerator;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryRepositoryCsvFileImplTest {

    @Mock
    private CsvUtil csvUtil;

    @InjectMocks
    private CategoryRepositoryCsvFileImpl categoryRepository;

    private Category category;

    @BeforeEach
    void setUp() {
        category = Category.builder()
                .id(UniqueIdGenerator.generateUniqueId())
                .url("example_category")
                .title("example category")
                .build();
    }


    @Nested
    @DisplayName("Testing getAllCategories method")
    class GetAllCategoriesTests {

        @Test
        @DisplayName("Should return a list of categories when the CSV file contains multiple categories")
        void return_list_of_categories_when_csv_file_contains_multiple_categories() throws Exception {
            String[] validArray1 = new String[]{UniqueIdGenerator.generateUniqueId(), "example category1", "example_category1"};
            String[] validArray2 = new String[]{UniqueIdGenerator.generateUniqueId(), "example category2", "example_category2"};
            when(csvUtil.readAllLines()).thenReturn(Arrays.asList(validArray1, validArray2));
            List<Category> categories = categoryRepository.getAllCategories();
            assertEquals(2, categories.size());
            assertEquals(validArray1[0], categories.get(0).getId());
            assertEquals(validArray1[2], categories.get(0).getUrl());
            assertEquals(validArray2[0], categories.get(1).getId());
            assertEquals(validArray2[2], categories.get(1).getUrl());
        }

        @Test
        @DisplayName("Should return an empty list when the CSV file is empty")
        void return_empty_list_when_csv_file_is_empty() {
            when(csvUtil.readAllLines()).thenReturn(Collections.emptyList());
            List<Category> categories = categoryRepository.getAllCategories();
            assertTrue(categories.isEmpty());
        }
    }

    @Nested
    @DisplayName("Testing addNewCategory method")
    class AddNewCategoryTests {

        @Test
        @DisplayName("Should return the added category")
        void return_added_category() {
            when(csvUtil.returnLineWithUniqueColumnValue(2, category.getUrl())).thenReturn(new String[0]);
            Category result = categoryRepository.addNewCategory(category);
            assertEquals(category, result);
            verify(csvUtil, times(1)).writeAllLines(anyList(), anyBoolean());
        }

        @Test
        @DisplayName("Should throw CategoryAlreadyExistsException when the category already exists")
        void throw_category_already_exists_exception_when_category_already_exists() {
            when(csvUtil.returnLineWithUniqueColumnValue(2, category.getUrl())).thenReturn(new String[]{category.getId(), category.getTitle(), category.getUrl()});
            assertThrows(CategoryAlreadyExistsException.class, () -> categoryRepository.addNewCategory(category));
            verify(csvUtil, times(1)).returnLineWithUniqueColumnValue(2, category.getUrl());
        }

        @Test
        @DisplayName("Should throw CategoryAlreadyExistsException when the category already exists")
        void do_not_throw_category_already_exists_exception_when_category_already_exists() {
            when(csvUtil.returnLineWithUniqueColumnValue(2, category.getUrl())).thenReturn(new String[]{category.getId(), category.getUrl(), category.getTitle() + "_"});
            doNothing().when(csvUtil).writeAllLines(anyList(), anyBoolean());
            Category result = categoryRepository.addNewCategory(category);
            assertEquals(result.getTitle(), category.getTitle());
            verify(csvUtil, times(1)).returnLineWithUniqueColumnValue(anyInt(), anyString());
        }

    }


    @Nested
    @DisplayName("Testing updateCategoryById method")
    class UpdateCategoryByIdTests {

        @Test
        @DisplayName("Should invoke csvUtil methods with correct arguments")
        void invoke_csv_util_methods_with_correct_arguments() {
            String categoryId = UniqueIdGenerator.generateUniqueId();
            Category updatedCategory = new Category(categoryId, "Updated_Category", "Updated Category");
            List<String[]> deletedLines = new ArrayList<>();
            when(csvUtil.deleteLine(categoryId)).thenReturn(deletedLines);
            when(csvUtil.returnLineWithUniqueColumnValue(2, "Updated_Category")).thenReturn(new String[0]);
            categoryRepository.updateCategoryById(categoryId, updatedCategory);
            verify(csvUtil, times(1)).deleteLine(categoryId);
            verify(csvUtil, times(1)).writeAllLines(anyList(), eq(false));
        }

        @Test
        @DisplayName("Should throw CategoryAlreadyExistsException for a category with an existing URL")
        void throw_exception_for_existing_url_and_same_title() {
            String id = UniqueIdGenerator.generateUniqueId();
            Category category1 = new Category(id, "Category_1", "Category 1");
            when(csvUtil.returnLineWithUniqueColumnValue(2, category1.getUrl())).thenReturn(new String[]{id, "Category 1", "Category_1"});
            assertThrows(CategoryAlreadyExistsException.class, () -> {
                categoryRepository.updateCategoryById(category1.getId(), category1);
            });
        }

        @Test
        @DisplayName("Should throw CategoryAlreadyExistsException for a category with an existing URL")
        void do_not_throw_exception_for_existing_url_with_another_titles() {
            String id = UniqueIdGenerator.generateUniqueId();
            Category category1 = new Category(id, "Category_1", "Category 1");
            when(csvUtil.returnLineWithUniqueColumnValue(2, category1.getUrl())).thenReturn(new String[]{id, "Category_1", "Категори 1"});
            categoryRepository.updateCategoryById(category1.getId(), category1);

        }

        @Test
        @DisplayName("Should return the updated category")
        void return_updated_category() {
            String categoryId = UniqueIdGenerator.generateUniqueId();
            Category updatedCategory = new Category(categoryId, "Updated_Category", "Updated Category");
            List<String[]> deletedLines = new ArrayList<>();
            when(csvUtil.deleteLine(categoryId)).thenReturn(deletedLines);
            when(csvUtil.returnLineWithUniqueColumnValue(2, "Updated_Category")).thenReturn(new String[0]);
            Category result = categoryRepository.updateCategoryById(categoryId, updatedCategory);
            assertEquals(updatedCategory, result);
        }

        @Test
        @DisplayName("Should throw CategoryNotFoundException when the category does not exist")
        void throw_category_not_found_exception_when_category_does_not_exist() {
            String categoryId = UniqueIdGenerator.generateUniqueId();
            Category updatedCategory = new Category(categoryId, "Updated_Category", "Updated Category");
            when(csvUtil.deleteLine(categoryId)).thenThrow(CategoryNotFoundException.class);
            assertThrows(CategoryNotFoundException.class, () -> categoryRepository.updateCategoryById(categoryId, updatedCategory));
        }
    }


    @Nested
    @DisplayName("Testing deleteCategoryById method")
    class DeleteCategoryByIdTests {

        @Test
        @DisplayName("Should invoke csvUtil methods with correct arguments")
        void delete_existing_category_by_id() {
            String categoryId = UniqueIdGenerator.generateUniqueId();
            List<String[]> updatedLines = new ArrayList<>();
            when(csvUtil.deleteLine(categoryId)).thenReturn(updatedLines);
            categoryRepository.deleteCategoryById(categoryId);
            verify(csvUtil, times(1)).deleteLine(categoryId);
            verify(csvUtil, times(1)).writeAllLines(updatedLines, false);
        }

        @Test
        @DisplayName("Should throw CategoryNotFoundException when the category does not exist")
        void throw_category_not_found_exception_when_category_does_not_exist() {
            String categoryId = UniqueIdGenerator.generateUniqueId();
            when(csvUtil.deleteLine(categoryId)).thenThrow(CategoryNotFoundException.class);
            assertThrows(CategoryNotFoundException.class, () -> categoryRepository.deleteCategoryById(categoryId));
        }
    }

    @Nested
    @DisplayName("Testing findCategoryById method")
    class FindCategoryByIdTests {

        @Test
        @DisplayName("Should invoke csvUtil methods with correct arguments")
        void find_existing_category_by_id() {
            String categoryId = UniqueIdGenerator.generateUniqueId();
            Category category1 = new Category(categoryId, "Category_1", "Category 1");
            String[] categoryLine = new String[]{category1.getId(), category1.getTitle(), category1.getUrl()};
            when(csvUtil.returnLineWithUniqueColumnValue(0, categoryId)).thenReturn(categoryLine);
            assertEquals(categoryRepository.findCategoryById(categoryId), category1);
        }

        @Test
        @DisplayName("Should throw CategoryNotFoundException when the category does not exist")
        void throw_category_not_found_exception_when_category_does_not_exist() {
            String categoryId = UniqueIdGenerator.generateUniqueId();
            when(csvUtil.returnLineWithUniqueColumnValue(0, categoryId)).thenReturn(new String[0]);
            assertThrows(CategoryNotFoundException.class, () -> categoryRepository.findCategoryById(categoryId));
        }

    }


}