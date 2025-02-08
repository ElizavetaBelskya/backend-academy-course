package ru.backend.academy.hometask4.controller.category;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.backend.academy.hometask4.util.UniqueIdGenerator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(initializers = CategoryControllerWithCsvRepositoryTest.DataSourceInitializer.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class CategoryControllerWithCsvRepositoryTest implements CategoryControllerTest {

    private static Path tempDir;
    private static Path dataFile;

    @Value("${csv.file.path.category}")
    private String testDataPath;

    @Autowired
    private MockMvc mockMvc;

    public static class DataSourceInitializer
            implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            try {
                tempDir = Paths.get("temp_directory");
                if (!Files.exists(tempDir)) {
                    Files.createDirectories(tempDir);
                }
                dataFile = tempDir.resolve("category_test.csv");
                if (!Files.exists(dataFile)) {
                    Files.createFile(dataFile);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
                    applicationContext,
                    "product.repository.impl=ram",
                    "category.repository.impl=file",
                    "csv.file.path.category=" + dataFile.toAbsolutePath().toString().replace("\\", "/")
            );
        }
    }

    private void rewriteCsv(List<String[]> data) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(testDataPath, true))) {
            for (String[] row : data) {
                StringBuilder line = new StringBuilder();
                for (int i = 0; i < row.length; i++) {
                    if (i > 0) {
                        line.append(",");
                    }
                    line.append(escapeCsvValue(row[i]));
                }
                writer.write(line.toString());
                writer.newLine();
            }
        }
    }

    private String escapeCsvValue(String value) {
        if (value.contains(",") || value.contains("\"")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        } else {
            return value;
        }
    }

    @BeforeEach
    void writeDefault() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(testDataPath))) {
            writer.write(UniqueIdGenerator.generateUniqueId() + ",\"default\",\"default\"");
            writer.newLine();
        }
    }

    @AfterEach
    void cleanAll() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(testDataPath))) {
            writer.write("");
        }
    }

    @AfterAll
    static void deleteTestFile() throws IOException {
        Files.delete(dataFile);
        Files.delete(tempDir);
    }

    @Test
    public void get_should_return_list_of_categories_with_correct_length() throws Exception {
        String[] categoryData = new String[]{UniqueIdGenerator.generateUniqueId(), "title2", "title2"};
        rewriteCsv(Collections.singletonList(categoryData));
        mockMvc.perform(MockMvcRequestBuilders.get("/category"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.size()")
                        .value(2))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.[1].title")
                        .value("title2"));
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
        String[] categoryData = new String[]{UniqueIdGenerator.generateUniqueId(),  "кюхонная техника", "kukhonnaya_tekhnika"};
        rewriteCsv(Collections.singletonList(categoryData));
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
        String[] categoryData = new String[]{UniqueIdGenerator.generateUniqueId(), "кухонная техника", "kukhonnaya_tekhnika"};
        rewriteCsv(Collections.singletonList(categoryData));
        String jsonContent = "{\"title\":\"кухонная техника\"}";
        mockMvc.perform(MockMvcRequestBuilders.post("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isConflict());
    }

    @Test
    public void put_should_update_category_with_same_url_but_another_title() throws Exception {
        String[] category = new String[]{UniqueIdGenerator.generateUniqueId(), "кюхонная техника", "kukhonnaya_tekhnika"};
        String[] oldCategory = new String[]{UniqueIdGenerator.generateUniqueId(), "old category", "old_category"};
        rewriteCsv(List.of(category, oldCategory));
        String jsonContent = "{\"title\":\"кyхонная техника\"}";
        mockMvc.perform(MockMvcRequestBuilders.put("/category/{categoryId}", oldCategory[0])
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isAccepted())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.url").exists());
    }

    @Test
    public void put_should_not_update_category_with_same_url_and_same_title() throws Exception {
        String[] category = new String[]{UniqueIdGenerator.generateUniqueId(),"кухонная техника", "kukhonnaya_tekhnika"};
        String[] oldCategory = new String[]{UniqueIdGenerator.generateUniqueId(), "old category", "old_category"};
        rewriteCsv(List.of(category, oldCategory));
        String jsonContent = "{\"title\":\"кухонная техника\"}";
        mockMvc.perform(MockMvcRequestBuilders.put("/category/{categoryId}", oldCategory[0])
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
        String[] oldCategory = new String[]{UniqueIdGenerator.generateUniqueId(), "old category", "old_category"};
        rewriteCsv(Collections.singletonList(oldCategory));
        mockMvc.perform(MockMvcRequestBuilders.delete("/category/{categoryId}", oldCategory[0]))
                .andExpect(status().isNoContent());
    }

    @Test
    public void delete_should_return_not_found_when_id_is_unknown() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/category/{categoryId}", UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

}
