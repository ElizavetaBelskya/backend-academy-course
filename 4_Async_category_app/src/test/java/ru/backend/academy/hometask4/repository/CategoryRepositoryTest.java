package ru.backend.academy.hometask4.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.backend.academy.hometask4.model.Category;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(initializers = CategoryRepositoryTest.DataSourceInitializer.class)
public class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

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
                    "spring.jpa.hibernate.ddl-auto=update"
            );
        }
    }

    @Test
    public void test_find_by_url_when_category_exists_then_return_category() {
        Category category = new Category("1", "testUrl", "testTitle");
        categoryRepository.save(category);
        Optional<Category> foundCategory = categoryRepository.findByUrl("testUrl");
        assertTrue(foundCategory.isPresent());
        assertEquals(category.getId(), foundCategory.get().getId());
    }

    @Test
    public void testFindByUrlWhenCategoryDoesNotExistThenReturnEmptyOptional() {
        Optional<Category> foundCategory = categoryRepository.findByUrl("nonExistentUrl");
        assertTrue(foundCategory.isEmpty());
    }


}
