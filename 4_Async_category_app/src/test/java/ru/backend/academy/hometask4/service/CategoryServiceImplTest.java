package ru.backend.academy.hometask4.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.backend.academy.hometask4.dto.category.CategoryDto;
import ru.backend.academy.hometask4.dto.category.NewOrUpdateCategoryDto;
import ru.backend.academy.hometask4.exception.already_exists.CategoryAlreadyExistsException;
import ru.backend.academy.hometask4.exception.not_found.CategoryNotFoundException;
import ru.backend.academy.hometask4.service.impl.CategoryServiceImpl;
import ru.backend.academy.hometask4.util.UniqueIdGenerator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
@ContextConfiguration(initializers = CategoryServiceImplTest.DataSourceInitializer.class)
public class CategoryServiceImplTest {

    @Autowired
    private CategoryServiceImpl categoryService;

    @Container
    private static final PostgreSQLContainer<?> database = new PostgreSQLContainer<>("postgres:12.9-alpine");

    public static class DataSourceInitializer
            implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
                    applicationContext,
                    "spring.datasource.url=" + database.getJdbcUrl(),
                    "spring.datasource.username=" + database.getUsername(),
                    "spring.datasource.password=" + database.getPassword(),
                    "product.repository.impl=database",
                    "category.repository.impl=database",
                    "spring.jpa.hibernate.ddl-auto=update"
            );
        }
    }

    @Nested
    @DisplayName("Testing readAllCategories method")
    class ReadAllCategoriesTests {

        @Test
        @DisplayName("Should return a list with one category when only default category exists")
        void should_return_list_with_one_category_when_only_default_category_exists() {
            String defaultTitle = "default";
            List<CategoryDto> result = categoryService.readAllCategories();
            assertEquals(result.get(0).getUrl(), defaultTitle);
            assertEquals(result.get(0).getTitle(), defaultTitle);
        }


    }

    @Nested
    @DisplayName("Testing createNewCategory method")
    class CreateNewCategoryTests {

        @Test
        @DisplayName("Should return added category when category added successfully")
        void should_return_added_category_when_category_added_successfully() {
            String newCategoryTitle = "new category";
            String newCategoryUrl = "new_category";
            NewOrUpdateCategoryDto categoryDto = new NewOrUpdateCategoryDto(newCategoryTitle);
            CategoryDto result = categoryService.createNewCategory(categoryDto);
            assertEquals(newCategoryTitle, result.getTitle());
            assertEquals(newCategoryUrl, result.getUrl());
        }

        @Test
        @DisplayName("Should throw exception when category already exists")
        void should_throw_exception_when_category_already_exists() {
            NewOrUpdateCategoryDto categoryDto = new NewOrUpdateCategoryDto("existing_category");
            categoryService.createNewCategory(categoryDto);
            assertThrows(CategoryAlreadyExistsException.class, () -> categoryService.createNewCategory(categoryDto));
        }

    }



    @Nested
    @DisplayName("Testing updateCategory method")
    class UpdateCategoryTests {

        @Test
        @DisplayName("Should return updated category when category updated successfully")
        void should_return_updated_category_when_category_updated_correct() {
            NewOrUpdateCategoryDto categoryDto1 = new NewOrUpdateCategoryDto("new category");
            NewOrUpdateCategoryDto categoryDto2 = new NewOrUpdateCategoryDto("updated category");
            CategoryDto addedCategory = categoryService.createNewCategory(categoryDto1);
            CategoryDto result = categoryService.updateCategory(addedCategory.getId(), categoryDto2);
            assertEquals(addedCategory.getId(), result.getId());
        }

        @Test
        @DisplayName("Should throw exception when category not found")
        void should_throw_exception_when_category_not_found() {
            NewOrUpdateCategoryDto categoryDto = new NewOrUpdateCategoryDto("fake update category");
            assertThrows(CategoryNotFoundException.class, () -> categoryService.updateCategory(UniqueIdGenerator.generateUniqueId(), categoryDto));
        }

    }


    @Nested
    @DisplayName("Testing deleteCategory method")
    class DeleteCategoryTests {

        @Test
        @DisplayName("Should throw exception when category not found")
        void should_throw_exception_when_category_not_found() {
            assertThrows(CategoryNotFoundException.class, () -> categoryService.deleteCategory(UniqueIdGenerator.generateUniqueId()));
        }

        @Test
        @DisplayName("Should delete category successfully")
        void should_delete_correct_category() {
            NewOrUpdateCategoryDto categoryDto1 = new NewOrUpdateCategoryDto("new category");
            String categoryId = categoryService.createNewCategory(categoryDto1).getId();
            categoryService.deleteCategory(categoryId);
        }
    }



}
