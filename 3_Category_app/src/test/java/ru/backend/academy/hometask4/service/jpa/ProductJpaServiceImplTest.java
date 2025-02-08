package ru.backend.academy.hometask4.service.jpa;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
import ru.backend.academy.hometask4.dto.ProductDto;
import ru.backend.academy.hometask4.dto.UpdateProductDto;
import ru.backend.academy.hometask4.exception.not_found.CategoryNotFoundException;
import ru.backend.academy.hometask4.exception.not_found.ProductNotFoundException;
import ru.backend.academy.hometask4.db_model.Category;
import ru.backend.academy.hometask4.repository.jpa.CategoryJpaRepository;
import ru.backend.academy.hometask4.repository.jpa.ProductJpaRepository;
import ru.backend.academy.hometask4.service.impl.jpa.ProductJpaServiceImpl;
import ru.backend.academy.hometask4.util.UniqueIdGenerator;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Testcontainers
@ContextConfiguration(initializers = ProductJpaServiceImplTest.DataSourceInitializer.class)
public class ProductJpaServiceImplTest {

    @Autowired
    private ProductJpaServiceImpl productService;

    @Autowired
    private CategoryJpaRepository categoryRepository;

    @Autowired
    private ProductJpaRepository productRepository;

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

    private ProductDto productDto;


    @BeforeEach
    void setUp() {
        String categoryId = UniqueIdGenerator.generateUniqueId();
        productDto = new ProductDto("ITEM1", "Product 1", 10.0f, 5, categoryId);
        categoryRepository.save(new Category(categoryId, "category_1", "category 1"));
        categoryRepository.save(categoryRepository.defaultCategory);
    }

    @AfterEach
    void clean() {
        productRepository.deleteAll();
        categoryRepository.deleteAll();
    }


    @Test
    public void add_new_product_should_add_product_and_return_correct_product_dto() {
        ProductDto result = productService.createNewProduct(productDto);
        assertEquals(productDto, result);
    }

    @Test
    public void add_new_product_should_add_product_with_default_category() {
        productDto.setCategoryId(null);
        ProductDto result = productService.createNewProduct(productDto);
        assertEquals(productDto.getItemNumber(), result.getItemNumber());
        assertEquals(categoryRepository.defaultCategory.getId(), result.getCategoryId());
    }

    @Test
    public void add_new_product_should_throw_not_found_exception_when_category_is_incorrect() {
        productDto.setCategoryId("NON_EXISTING");
        assertThrows(CategoryNotFoundException.class, () -> productService.createNewProduct(productDto));
    }

    @Test
    public void get_all_products_should_return_correct_products_list() {
        productService.createNewProduct(productDto);
        List<ProductDto> productDtoList = Collections.singletonList(productDto);
        List<ProductDto> result = productService.readAllProducts();
        assertEquals(productDtoList, result);
    }

    @Test
    public void get_all_products_should_return_empty_list() {
        List<ProductDto> result = productService.readAllProducts();
        assertEquals(Collections.emptyList(), result);
    }

    @Test
    public void delete_product_should_delete_product_correctly() {
        productService.createNewProduct(productDto);
        String id = productDto.getItemNumber();
        productService.deleteProduct(id);
    }

    @Test
    public void delete_product_should_throw_product_not_found_exception() {
        String id = "NON_EXISTING";
        assertThrows(RuntimeException.class, () -> productService.deleteProduct(id));
    }

    @Test
    public void update_product_should_update_product_and_return_correct_product_dto() {
        ProductDto newProductDto = productService.createNewProduct(productDto);
        UpdateProductDto updateProductDto = new UpdateProductDto( "Product 1", 10.0f, 5, null);
        ProductDto result = productService.updateProduct(newProductDto.getItemNumber(), updateProductDto);
        assertEquals(updateProductDto.getTitle(), result.getTitle());
        assertEquals(updateProductDto.getPrice(), result.getPrice());
        assertEquals(newProductDto.getItemNumber(), result.getItemNumber());
    }

    @Test
    public void update_product_should_update_product_with_default_category() {
        ProductDto newProductDto = productService.createNewProduct(productDto);
        UpdateProductDto updateProductDto = new UpdateProductDto( "Product 1", 10.0f, 5, null);
        ProductDto result = productService.updateProduct(newProductDto.getItemNumber(), updateProductDto);
        assertEquals(updateProductDto.getTitle(), result.getTitle());
        assertEquals(newProductDto.getItemNumber(), result.getItemNumber());
    }

    @Test
    public void update_product_should_throw_not_found_exception_when_product_id_incorrect() {
        String id = "NON_EXISTING";
        UpdateProductDto updateProductDto = new UpdateProductDto( "Product 1", 10.0f, 5, "category_1");
        assertThrows(ProductNotFoundException.class, () -> productService.updateProduct(id, updateProductDto));
    }

    @Test
    public void get_all_products_by_category_id_should_return_correct_list() {
        String categoryId = UniqueIdGenerator.generateUniqueId();
        categoryRepository.save(new Category(categoryId, "new_category", "new category"));
        ProductDto productDto = new ProductDto("ITEM2", "title", 1.0f, 1, categoryId);
        productService.createNewProduct(productDto);
        List<ProductDto> result = productService.getProductsByCategoryId(categoryId);
        assertEquals(List.of(productDto), result);
    }

    @Test
    public void get_all_products_by_category_id_should_return_empty_list_when_no_products_with_category() {
        String categoryId = UniqueIdGenerator.generateUniqueId();
        Category category = new Category(categoryId, "title", "title");
        categoryRepository.save(category);
        List<ProductDto> result = productService.getProductsByCategoryId(categoryId);
        assertEquals(Collections.emptyList(), result);
    }

    @Test
    public void get_all_products_by_category_id_should_throw_exception_when_category_is_non_existent() {
        String categoryId = UniqueIdGenerator.generateUniqueId();
        assertThrows(CategoryNotFoundException.class, () -> productService.getProductsByCategoryId(categoryId));
    }



}



