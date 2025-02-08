package ru.backend.academy.hometask4.controller.category;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
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
import ru.backend.academy.hometask4.model.Category;
import ru.backend.academy.hometask4.util.UniqueIdGenerator;

import java.util.concurrent.ConcurrentMap;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(initializers = CategoryControllerWithMapRepositoryTest.DataSourceInitializer.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class CategoryControllerWithMapRepositoryTest implements CategoryControllerTest {

    private final String defaultUUID = UniqueIdGenerator.generateUniqueId();

    @Autowired
    private ConcurrentMap<String, Category> categoryConcurrentMap;

    @Autowired
    private MockMvc mockMvc;

    public static class DataSourceInitializer
            implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
                    applicationContext,
                    "product.repository.impl=ram",
                    "category.repository.impl=ram"
            );
        }
    }

    @AfterEach
    void clear() {
        categoryConcurrentMap.clear();
        categoryConcurrentMap.put(defaultUUID, new Category(defaultUUID, "default", "default"));
    }

    @Test
    public void get_should_return_list_of_categories_with_correct_length() throws Exception {
        Category category1 = new Category(UniqueIdGenerator.generateUniqueId(), "title2", "title2");
        categoryConcurrentMap.put(category1.getId(), category1);
        mockMvc.perform(MockMvcRequestBuilders.get("/category"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.size()")
                        .value(categoryConcurrentMap.size()));
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
        Category category = new Category(UniqueIdGenerator.generateUniqueId(), "kukhonnaya_tekhnika", "кюхонная техника");
        categoryConcurrentMap.put(category.getId(), category);
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
        categoryConcurrentMap.put(category.getId(), category);
        String jsonContent = "{\"title\":\"кухонная техника\"}";
        mockMvc.perform(MockMvcRequestBuilders.post("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isConflict());
    }

    @Test
    public void put_should_update_category_with_same_url_but_another_title() throws Exception {
        Category category = new Category(UniqueIdGenerator.generateUniqueId(), "kukhonnaya_tekhnika", "кюхонная техника");
        Category oldCategory = new Category(UniqueIdGenerator.generateUniqueId(), "old_category", "old category");
        categoryConcurrentMap.put(category.getId(), category);
        categoryConcurrentMap.put(oldCategory.getId(), oldCategory);
        String jsonContent = "{\"title\":\"кyхонная техника\"}";
        mockMvc.perform(MockMvcRequestBuilders.put("/category/{categoryId}", oldCategory.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isAccepted())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.url").exists());
    }

    @Test
    public void put_should_not_update_category_with_same_url_and_same_title() throws Exception {
        Category category = new Category(UniqueIdGenerator.generateUniqueId(), "kukhonnaya_tekhnika", "кухонная техника");
        Category oldCategory = new Category(UniqueIdGenerator.generateUniqueId(), "old_category", "old category");
        categoryConcurrentMap.put(category.getId(), category);
        categoryConcurrentMap.put(oldCategory.getId(), oldCategory);
        String jsonContent = "{\"title\":\"кухонная техника\"}";
        mockMvc.perform(MockMvcRequestBuilders.put("/category/{categoryId}", oldCategory.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isConflict());
    }

    @Test
    public void put_should_not_update_unknown_id_and_return_not_found() throws Exception {
        String jsonContent = "{\"title\":\"кухонная техника\"}";
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/category/{categoryId}", UniqueIdGenerator.generateUniqueId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isNotFound());
    }

    @Test
    public void delete_should_delete_category_and_return_no_content() throws Exception {
        Category oldCategory = new Category(UniqueIdGenerator.generateUniqueId(), "old_category", "old category");
        categoryConcurrentMap.put(oldCategory.getId(), oldCategory);
        mockMvc.perform(MockMvcRequestBuilders.delete("/category/{categoryId}", oldCategory.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    public void delete_should_return_not_found_when_id_is_unknown() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/category/{categoryId}", UniqueIdGenerator.generateUniqueId()))
                .andExpect(status().isNotFound());
    }


}