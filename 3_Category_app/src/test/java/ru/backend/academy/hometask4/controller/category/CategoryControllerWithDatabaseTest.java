package ru.backend.academy.hometask4.controller.category;


import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.backend.academy.hometask4.db_model.Category;
import ru.backend.academy.hometask4.repository.jpa.CategoryJpaRepository;
import ru.backend.academy.hometask4.util.UniqueIdGenerator;

import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ContextConfiguration(initializers = CategoryControllerWithDatabaseTest.DataSourceInitializer.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class CategoryControllerWithDatabaseTest implements CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoryJpaRepository categoryJpaRepository;

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

    @AfterEach
    void cleanDatabase() {
        categoryJpaRepository.deleteAll();
        categoryJpaRepository.save(categoryJpaRepository.defaultCategory);
    }


    @Test
    public void get_should_return_list_of_categories_with_correct_length() throws Exception {
        List<Category> categories = List.of(
                new Category(UniqueIdGenerator.generateUniqueId(), "category_1", "category 1"),
                new Category(UniqueIdGenerator.generateUniqueId(), "category_2", "category 2"));
        categoryJpaRepository.saveAll(categories);
        mockMvc.perform(MockMvcRequestBuilders.get("/category"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.size()")
                        .value(categories.size() + 1));
    }

    @Test
    public void get_should_return_list_with_default_category() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/category")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.size()")
                        .value(1))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.[0].title")
                        .value("default"));
    }

    @Test
    public void post_should_return_added_category() throws Exception {
        String jsonContent = "{\"title\":\"New category title\"}";
        mockMvc.perform(MockMvcRequestBuilders.post("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.url")
                        .value("New_category_title"));
    }

    @Test
    public void post_should_return_added_category_with_transliterated_title() throws Exception {
        String jsonContent = "{\"title\":\"кухонная техника\"}";
        mockMvc.perform(MockMvcRequestBuilders.post("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.url")
                        .value("kukhonnaya_tekhnika"));
    }

    @Test
    public void post_should_add_category_with_same_url_but_another_title() throws Exception {
        Category category = new Category(UniqueIdGenerator.generateUniqueId(),"kukhonnaya_tekhnika",  "кюхонная техника");
        categoryJpaRepository.save(category);
        String jsonContent = "{\"title\":\"кухонная техника\"}";
        mockMvc.perform(MockMvcRequestBuilders.post("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.url").exists());
    }

    @Test
    public void post_should_not_add_category_with_same_url_and_same_title() throws Exception {
        Category category = new Category(UniqueIdGenerator.generateUniqueId(), "kukhonnaya_tekhnika", "кухонная техника");
        categoryJpaRepository.save(category);
        String jsonContent = "{\"title\":\"кухонная техника\"}";
        mockMvc.perform(MockMvcRequestBuilders.post("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isConflict());
    }

    @Test
    public void put_should_update_category_with_same_url_but_another_title() throws Exception {
        Category category = new Category(UniqueIdGenerator.generateUniqueId(), "kukhonnaya_tekhnika", "кюхонная техника");
        categoryJpaRepository.save(category);
        String jsonContent = "{\"title\":\"кyхонная техника\"}";
        mockMvc.perform(MockMvcRequestBuilders.put("/category/{categoryId}", category.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isAccepted())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.url").exists());
    }

    @Test
    public void put_should_not_update_category_with_same_url_and_same_title() throws Exception {
        Category category = new Category(UniqueIdGenerator.generateUniqueId(), "kukhonnaya_tekhnika", "кухонная техника");
        categoryJpaRepository.save(category);
        String jsonContent = "{\"title\":\"кухонная техника\"}";
        mockMvc.perform(MockMvcRequestBuilders.put("/category/{categoryId}", category.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isConflict());
    }

    @Test
    public void put_should_not_update_unknown_id_and_return_not_found() throws Exception {
        String jsonContent = "{\"title\":\"кухонная техника\"}";
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/category/{categoryId}", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isNotFound());
    }

    @Test
    public void delete_should_delete_category_and_return_no_content() throws Exception {
        Category category = new Category(UniqueIdGenerator.generateUniqueId(), "kukhonnaya_tekhnika", "кухонная техника");
        categoryJpaRepository.save(category);
        mockMvc.perform(MockMvcRequestBuilders.delete("/category/{categoryId}", category.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    public void delete_should_return_not_found_when_id_is_unknown() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/category/{categoryId}",
                                UniqueIdGenerator.generateUniqueId()))
                .andExpect(status().isNotFound());
    }
}
