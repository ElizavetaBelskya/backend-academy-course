package ru.backend.academy.hometask4.controller.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import ru.backend.academy.hometask4.dto.ProductDto;
import ru.backend.academy.hometask4.dto.UpdateProductDto;
import ru.backend.academy.hometask4.model.Category;
import ru.backend.academy.hometask4.util.UniqueIdGenerator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(initializers = ProductControllerWithCsvRepositoryTest.DataSourceInitializer.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class ProductControllerWithCsvRepositoryTest implements ProductControllerTest {

    private static Path tempDir;
    private static Path dataFile;

    @Value("${csv.file.path.product}")
    private String testDataPath;

    private ObjectMapper objectMapper;

    @Autowired
    private ConcurrentMap<String, Category> categoryConcurrentMap;

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
                dataFile = tempDir.resolve("product_test.csv");
                if (!Files.exists(dataFile)) {
                    Files.createFile(dataFile);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
                    applicationContext,
                    "product.repository.impl=file",
                    "category.repository.impl=ram",
                    "csv.file.path.product=" + dataFile.toAbsolutePath().toString().replace("\\", "/")
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
    void setUp() {
        objectMapper = new ObjectMapper();
        String id = UniqueIdGenerator.generateUniqueId();
        categoryConcurrentMap.put(id, new Category(id, "category1", "category1"));
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
    public void get_should_return_correct_list() throws Exception {
        String[] product1 = new String[]{"ITEM1", "Product 1", "10.0", "5", "category1"};
        String[] product2 = new String[]{"ITEM2", "Product 2", "20.0f", "10", "category2"};
        List<String[]> expectedList = List.of(product1, product2);
        rewriteCsv(expectedList);
        mockMvc.perform(MockMvcRequestBuilders.get("/product")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()")
                        .value(expectedList.size()));
    }

    @Test
    public void get_all_products_by_category_should_return_product_list() throws Exception {
        String id = UniqueIdGenerator.generateUniqueId();
        categoryConcurrentMap.put(id, new Category(id, "category1", "category1"));
        String[] product1 = new String[]{"ITEM1", "Product 1", "10.0f", "5", "category1"};
        String[] product2 = new String[]{"ITEM2", "Product 2", "20.0f", "10", "category2"};
        rewriteCsv(List.of(product1, product2));
        mockMvc.perform(MockMvcRequestBuilders.get("/category/{categoryId}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()")
                        .value(1));
    }

    @Test
    public void post_should_add_product_correctly_and_return_created() throws Exception {
        ProductDto productDto = new ProductDto("ITEM1", "Product 1", 10.0f, 5, "category1");
        mockMvc.perform(MockMvcRequestBuilders.post("/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDto)))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.itemNumber")
                        .value(productDto.getItemNumber()));
    }

    @Test
    public void post_should_return_conflict_when_product_has_existing_id() throws Exception {
        ProductDto productDto = new ProductDto("ITEM1", "Product 1", 10.0f, 5, "category1");
        String[] product1 = new String[]{"ITEM1", "Product 1", "10.0f", "5", "category1"};
        List<String[]> products = new ArrayList<>();
        products.add(product1);
        rewriteCsv(products);
        mockMvc.perform(MockMvcRequestBuilders.post("/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDto)))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isConflict());
    }

    @Test
    public void post_should_return_bad_request_when_product_data_incorrect() throws Exception {
        ProductDto productDto = new ProductDto("", "", 0.0f, 0, "");
        mockMvc.perform(MockMvcRequestBuilders.post("/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDto)))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void delete_should_delete_product_and_return_no_content() throws Exception {
        String[] product1 = new String[]{"ITEM1", "Product 1", "10.0f", "5", "category1"};
        List<String[]> products = new ArrayList<>();
        products.add(product1);
        rewriteCsv(products);
        mockMvc.perform(MockMvcRequestBuilders.delete("/product/{itemNumber}", "ITEM1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    public void delete_should_return_not_found_when_id_is_non_existent() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/product/{itemNumber}", "NON_EXISTENT_ITEM")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void put_should_update_product_correctly_and_return_accepted() throws Exception {
        String[] product1 = new String[]{"ITEM1", "Product 1", "10.0f", "5", "category1"};
        List<String[]> products = new ArrayList<>();
        products.add(product1);
        rewriteCsv(products);
        UpdateProductDto productDto = new UpdateProductDto( "Product for update", 10.0f, 6, "category1");
        mockMvc.perform(MockMvcRequestBuilders.put("/product/{itemNumber}", "ITEM1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDto)))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isAccepted())
                .andExpect(MockMvcResultMatchers.jsonPath("$.itemNumber")
                        .value(product1[0]));
    }

    @Test
    public void put_should_not_update_and_return_not_found_when_id_is_non_existent() throws Exception {
        UpdateProductDto productDto = new UpdateProductDto( "Product for update", 10.0f, 6, "category1");
        mockMvc.perform(MockMvcRequestBuilders.put("/product/{itemNumber}", "NON_EXISTENT_ITEM")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDto)))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void put_should_not_update_and_return_not_found_category_when_id_is_non_existent() throws Exception {
        String[] product1 = new String[]{"ITEM1", "Product 1", "10.0f", "5", "category1"};
        List<String[]> products = new ArrayList<>();
        products.add(product1);
        rewriteCsv(products);
        UpdateProductDto productDto = new UpdateProductDto( "Product for update", 10.0f, 6, "no_category");
        mockMvc.perform(MockMvcRequestBuilders.put("/product/{itemNumber}", product1[0])
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDto)))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }



}
