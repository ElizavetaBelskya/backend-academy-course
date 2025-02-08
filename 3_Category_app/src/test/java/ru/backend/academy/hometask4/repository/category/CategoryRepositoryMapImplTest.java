package ru.backend.academy.hometask4.repository.category;

import org.junit.jupiter.api.*;
import ru.backend.academy.hometask4.exception.already_exists.CategoryAlreadyExistsException;
import ru.backend.academy.hometask4.exception.not_found.CategoryNotFoundException;
import ru.backend.academy.hometask4.model.Category;
import ru.backend.academy.hometask4.repository.impl.CategoryRepositoryMapImpl;
import ru.backend.academy.hometask4.util.UniqueIdGenerator;

import java.util.List;
import java.lang.String;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.junit.jupiter.api.Assertions.*;

public class CategoryRepositoryMapImplTest {

    private ConcurrentMap<String, Category> concurrentMap;
    private CategoryRepositoryMapImpl categoryRepository;

    @BeforeEach
    void setUp() {
        concurrentMap = new ConcurrentHashMap<>();
        categoryRepository = new CategoryRepositoryMapImpl(concurrentMap);
    }

    @Nested
    @DisplayName("Testing getAllCategories method")
    class GetAllCategoriesTests {

        @Test
        @DisplayName("Should return a list with one category when only the default category exists")
        void return_list_with_one_category_when_only_default_category_exists() {
            List<Category> categories = categoryRepository.getAllCategories();
            assertEquals(1, categories.size());
        }

        @Test
        @DisplayName("Should return a list with the correct length after adding categories")
        void return_list_with_correct_length_after_adding_categories() {
            Category category1 = new Category(UniqueIdGenerator.generateUniqueId(), "Category_1", "Category 1");
            Category category2 = new Category(UniqueIdGenerator.generateUniqueId(), "Category_2", "Category 2");
            concurrentMap.put(category1.getId(), category1);
            concurrentMap.put(category2.getId(), category2);
            List<Category> categories = categoryRepository.getAllCategories();
            assertEquals(3, categories.size());
        }

        @Test
        @DisplayName("Should return a list with the correct length after deleting categories")
        void return_list_with_correct_length_after_deleting_categories() {
            String categoryId1 = categoryRepository.addNewCategory(new Category(null, "Category", "Category")).getId();
            categoryRepository.deleteCategoryById(categoryId1);
            List<Category> categories = categoryRepository.getAllCategories();
            assertEquals(1, categories.size());
        }

    }

    @Nested
    @DisplayName("Testing addNewCategory method")
    class AddNewCategoryTests {

        @Test
        @DisplayName("Should return added category")
        void return_added_category() {
            Category newCategory = new Category(UniqueIdGenerator.generateUniqueId(), "New_category", "New category");
            Category addedCategory = categoryRepository.addNewCategory(newCategory);
            assertNotNull(addedCategory);
            assertEquals(newCategory, addedCategory);
        }

        @Test
        @DisplayName("Should assign an ID to the category")
        void assign_id_to_category() {
            Category newCategory = Category.builder().url("New_category").build();
            Category addedCategory = categoryRepository.addNewCategory(newCategory);
            assertNotNull(addedCategory);
            assertNotNull(addedCategory.getId());
        }

        @Test
        @DisplayName("Should throw CategoryAlreadyExistsException for a category with an existing URL")
        void throw_exception_for_existing_url() {
            Category category1 = new Category(UniqueIdGenerator.generateUniqueId(), "Category_1", "Category");
            Category category2 = new Category(UniqueIdGenerator.generateUniqueId(), category1.getUrl(), category1.getTitle());
            categoryRepository.addNewCategory(category1);
            assertThrows(CategoryAlreadyExistsException.class, () -> categoryRepository.addNewCategory(category2));
        }

        @Test
        @DisplayName("Should throw CategoryAlreadyExistsException for a category with an existing URL and same title")
        void throw_exception_for_existing_url_and_another_title() {
            Category category1 = new Category(UniqueIdGenerator.generateUniqueId(), "Category_1", "Category 1");
            concurrentMap.put(category1.getId(), category1);
            Category category2 = new Category(category1.getId(), "Category_1", "Category_1");
            category2.setUrl(category1.getUrl());
            assertDoesNotThrow(() -> categoryRepository.addNewCategory(category2));
        }

        @Test
        @DisplayName("Should throw CategoryAlreadyExistsException for a category with an existing URL and same titile")
        void throw_exception_for_existing_url_and_same_title() {
            Category category1 = new Category(UniqueIdGenerator.generateUniqueId(), "Category_1", "Category 1");
            concurrentMap.put(category1.getId(), category1);
            Category category2 = new Category(category1.getId(), "Category_1", "Category 1");
            category2.setUrl(category1.getUrl());
            assertThrows(CategoryAlreadyExistsException.class, () -> {
                categoryRepository.addNewCategory(category2);
            });
        }

    }

    @Nested
    @DisplayName("Testing updateCategoryById method")
    class UpdateCategoryByIdTests {

        @Test
        @DisplayName("Should return updated category")
        void return_updated_category() {
            Category newCategory = Category.builder().url("New_category").build();
            categoryRepository.addNewCategory(newCategory);
            Category updatedCategory = new Category(newCategory.getId(), "Updated_category", "Updated category");
            Category result = categoryRepository.updateCategoryById(newCategory.getId(), updatedCategory);
            assertNotNull(result);
            assertEquals(updatedCategory, result);
        }

        @Test
        @DisplayName("Should throw CategoryAlreadyExistsException for a category with an existing URL and same title")
        void throw_exception_for_existing_url_and_another_title() {
            Category category1 = new Category(UniqueIdGenerator.generateUniqueId(), "Category_1", "Category 1");
            concurrentMap.put(category1.getId(), category1);
            Category category2 = new Category(category1.getId(), "Category_1", "Category_1");
            category2.setUrl(category1.getUrl());
            assertDoesNotThrow(() -> categoryRepository.updateCategoryById(category2.getId(), category2));
        }

        @Test
        @DisplayName("Should throw CategoryAlreadyExistsException for a category with an existing URL and same titile")
        void throw_exception_for_existing_url_and_same_title() {
            Category category1 = new Category(UniqueIdGenerator.generateUniqueId(), "Category_1", "Category 1");
            concurrentMap.put(category1.getId(), category1);
            Category category2 = new Category(category1.getId(), "Category_1", "Category 1");
            category2.setUrl(category1.getUrl());
            assertThrows(CategoryAlreadyExistsException.class, () -> {
                categoryRepository.updateCategoryById(category2.getId(), category2);
            });
        }

        @Test
        @DisplayName("Should throw CategoryNotFoundException for a non-existing category")
        void throw_exception_for_non_existing_id() {
            String nonExistingId = UniqueIdGenerator.generateUniqueId();
            Category updatedCategory = new Category(nonExistingId, "Updated_category", "Updated category");
            assertThrows(CategoryNotFoundException.class, () -> categoryRepository.updateCategoryById(nonExistingId, updatedCategory));
        }
    }

    @Nested
    @DisplayName("Testing deleteCategoryById method")
    class DeleteCategoryByIdTests {

        @Test
        @DisplayName("Should delete an existing category by ID and remove it from the concurrent map")
        void delete_existing_category_by_id() {
            Category category = new Category(UniqueIdGenerator.generateUniqueId(), "Category", "Category");
            concurrentMap.put(category.getId(), category);
            String categoryId = category.getId();
            categoryRepository.deleteCategoryById(categoryId);
            assertFalse(concurrentMap.containsKey(categoryId));
        }

        @Test
        @DisplayName("Should throw CategoryNotFoundException for a non-existing category by ID")
        void throw_category_not_found_exception_when_category_does_not_exist() {
            String nonExistingCategoryId = UniqueIdGenerator.generateUniqueId();
            assertThrows(CategoryNotFoundException.class, () -> {
                categoryRepository.deleteCategoryById(nonExistingCategoryId);
            });
        }


    }

    @Nested
    @DisplayName("Testing existsCategoryWithId method")
    class ExistsCategoryWithIdTests {

        @Test
        @DisplayName("Should return true for an existing category by ID")
        void return_true_for_existing_category_by_id() {
            Category category = new Category(UniqueIdGenerator.generateUniqueId(), "Category", "Category");
            concurrentMap.put(category.getId(),category);
            String categoryId = category.getId();
            assertTrue(categoryRepository.existsCategoryWithId(categoryId));
        }

        @Test
        @DisplayName("Should return false for a non-existing category by ID")
        void return_false_for_non_existing_category_by_id() {
            String nonExistingCategoryId = UniqueIdGenerator.generateUniqueId();
            assertFalse(categoryRepository.existsCategoryWithId(nonExistingCategoryId));
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
            concurrentMap.put(categoryId, category1);
            assertEquals(categoryRepository.findCategoryById(categoryId), category1);
        }

        @Test
        @DisplayName("Should throw CategoryNotFoundException when the category does not exist")
        void throw_category_not_found_exception_when_category_does_not_exist() {
            String categoryId = UniqueIdGenerator.generateUniqueId();
            assertThrows(CategoryNotFoundException.class, () -> categoryRepository.findCategoryById(categoryId));
        }

    }



}