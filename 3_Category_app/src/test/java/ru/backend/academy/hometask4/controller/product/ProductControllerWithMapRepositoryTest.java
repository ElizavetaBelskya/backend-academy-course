package ru.backend.academy.hometask4.controller.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
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
import ru.backend.academy.hometask4.controller.category.CategoryControllerWithMapRepositoryTest;
import ru.backend.academy.hometask4.dto.ProductDto;
import ru.backend.academy.hometask4.dto.UpdateProductDto;
import ru.backend.academy.hometask4.model.Category;
import ru.backend.academy.hometask4.model.Product;
import ru.backend.academy.hometask4.util.UniqueIdGenerator;

import java.lang.String;
import java.util.concurrent.ConcurrentMap;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(initializers = ProductControllerWithMapRepositoryTest.DataSourceInitializer.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ProductControllerWithMapRepositoryTest implements ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ConcurrentMap<String, Product> productConcurrentMap;

    @Autowired
    private ConcurrentMap<String, Category> categoryConcurrentMap;

    private ObjectMapper objectMapper;

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

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        String id = UniqueIdGenerator.generateUniqueId();
        categoryConcurrentMap.put(id, new Category(id, "category1", "category1"));
    }

    @AfterEach
    void clear() {
        productConcurrentMap.clear();
    }

    @Test
    public void get_should_return_correct_list() throws Exception {
        Product product1 = new Product("ITEM1", "Product 1", 10.0f, 5, "category1");
        Product product2 = new Product("ITEM2", "Product 2", 20.0f, 10, "category2");
        productConcurrentMap.put(product1.getItemNumber(), product1);
        productConcurrentMap.put(product2.getItemNumber(), product2);
        mockMvc.perform(MockMvcRequestBuilders.get("/product")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()")
                        .value(productConcurrentMap.size()));
    }

    @Test
    public void get_all_products_by_category_should_return_product_list() throws Exception {
        String id = UniqueIdGenerator.generateUniqueId();
        categoryConcurrentMap.put(id, new Category(id, "category1", "category1"));
        Product product1 = new Product("ITEM1", "Product 1", 10.0f, 5, "category1");
        Product product2 = new Product("ITEM2", "Product 2", 20.0f, 10, "category2");
        productConcurrentMap.put(product1.getItemNumber(), product1);
        productConcurrentMap.put(product2.getItemNumber(), product2);
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
        Product product1 = new Product("ITEM1", "Product 1", 10.0f, 5, "category1");
        productConcurrentMap.put(product1.getItemNumber(), product1);
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
        Product product1 = new Product("ITEM1", "Product 1", 10.0f, 5, "category1");
        productConcurrentMap.put(product1.getItemNumber(), product1);
        mockMvc.perform(MockMvcRequestBuilders.delete("/product/{itemNumber}", product1.getItemNumber())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());
        Assertions.assertFalse(productConcurrentMap.containsKey(product1.getItemNumber()));
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
        Product product1 = new Product("ITEM1", "Product", 10.0f, 5, "category1");
        productConcurrentMap.put(product1.getItemNumber(), product1);
        UpdateProductDto productDto = new UpdateProductDto( "Product for update", 10.0f, 6, "category1");
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
        UpdateProductDto productDto = new UpdateProductDto( "Product for update", 10.0f, 6, "category1");
        mockMvc.perform(MockMvcRequestBuilders.put("/product/{itemNumber}", "NON_EXISTENT_ITEM")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDto)))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void put_should_not_update_and_return_not_found_category_when_id_is_non_existent() throws Exception {
        Product product1 = new Product("ITEM1", "Product", 10.0f, 5, "category1");
        productConcurrentMap.put(product1.getItemNumber(), product1);
        UpdateProductDto productDto = new UpdateProductDto( "Product for update", 10.0f, 6, "no_category");
        mockMvc.perform(MockMvcRequestBuilders.put("/product/{itemNumber}", product1.getItemNumber())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDto)))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }


}