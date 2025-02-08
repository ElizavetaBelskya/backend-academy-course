package ru.backend.academy.hometask4.controller.product;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import ru.backend.academy.hometask4.model.Category;
import ru.backend.academy.hometask4.model.Product;
import ru.backend.academy.hometask4.dto.product.ProductDto;
import ru.backend.academy.hometask4.dto.product.UpdateProductDto;
import ru.backend.academy.hometask4.repository.CategoryRepository;
import ru.backend.academy.hometask4.repository.ProductRepository;
import ru.backend.academy.hometask4.util.UniqueIdGenerator;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ContextConfiguration(initializers = ProductControllerTestImpl.DataSourceInitializer.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class ProductControllerTestImpl implements ProductControllerTest {

    private ObjectMapper objectMapper;
    private Category testCategory;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

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

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        testCategory = new Category(UniqueIdGenerator.generateUniqueId(), "category1", "category1");
        categoryRepository.save(testCategory);
        categoryRepository.save(categoryRepository.defaultCategory);
    }

    @AfterEach
    void clear() {
        productRepository.deleteAll();
        categoryRepository.deleteAll();
    }


    @Test
    public void get_should_return_correct_list() throws Exception {
        Product product1 = new Product("ITEM1", "Product 1", 10.0f, 5, testCategory);
        Product product2 = new Product("ITEM2", "Product 2", 20.0f, 10, testCategory);
        List<Product> productList = List.of(product1, product2);
        productRepository.saveAll(productList);
        mockMvc.perform(MockMvcRequestBuilders.get("/product")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()")
                        .value(productList.size()));
    }

    @Test
    public void get_all_products_by_category_should_return_product_list() throws Exception {
        Product product1 = new Product("ITEM1", "Product 1", 10.0f, 5, categoryRepository.defaultCategory);
        Product product2 = new Product("ITEM2", "Product 2", 20.0f, 10, testCategory);
        List<Product> productList = List.of(product1, product2);
        productRepository.saveAll(productList);
        mockMvc.perform(MockMvcRequestBuilders.get("/category/{categoryId}", testCategory.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()")
                        .value(1));
    }

    @Test
    public void post_should_add_product_correctly_and_return_created() throws Exception {
        ProductDto productDto = new ProductDto("ITEM1", "Product 1", 10.0f, 5, testCategory.getId());
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
        ProductDto productDto = new ProductDto("ITEM1", "Product 1", 10.0f, 5, testCategory.getId());
        Product product1 = new Product("ITEM1", "Product 1", 10.0f, 5, testCategory);
        productRepository.save(product1);
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
        Product product1 = new Product("ITEM1", "Product 1", 10.0f, 5, testCategory);
        productRepository.save(product1);
        mockMvc.perform(MockMvcRequestBuilders.delete("/product/{itemNumber}", product1.getItemNumber())
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
        Product product1 = new Product("ITEM1", "Product", 10.0f, 5, testCategory);
        productRepository.save(product1);
        UpdateProductDto productDto = new UpdateProductDto( "Product for update", 10.0f, 6, null);
        mockMvc.perform(MockMvcRequestBuilders.put("/product/{itemNumber}", product1.getItemNumber())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDto)))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isAccepted())
                .andExpect(MockMvcResultMatchers.jsonPath("$.itemNumber")
                        .value(product1.getItemNumber()));
    }

    @Test
    public void put_should_not_update_and_return_not_found_when_id_is_non_existent() throws Exception {
        UpdateProductDto productDto = new UpdateProductDto( "Product for update", 10.0f, 6, testCategory.getId());
        mockMvc.perform(MockMvcRequestBuilders.put("/product/{itemNumber}", "NON_EXISTENT_ITEM")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDto)))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void put_should_not_update_and_return_not_found_category_when_id_is_non_existent() throws Exception {
        Product product1 = new Product("ITEM1", "Product", 10.0f, 5, testCategory);
        productRepository.save(product1);
        UpdateProductDto productDto = new UpdateProductDto( "Product for update", 10.0f, 6, "no_category");
        mockMvc.perform(MockMvcRequestBuilders.put("/product/{itemNumber}", product1.getItemNumber())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDto)))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }



}
