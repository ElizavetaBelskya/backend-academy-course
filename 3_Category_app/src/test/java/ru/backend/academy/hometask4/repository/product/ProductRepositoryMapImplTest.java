package ru.backend.academy.hometask4.repository.product;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.backend.academy.hometask4.exception.already_exists.ProductAlreadyExistsException;
import ru.backend.academy.hometask4.exception.not_found.ProductNotFoundException;
import ru.backend.academy.hometask4.model.Product;
import ru.backend.academy.hometask4.repository.impl.ProductRepositoryMapImpl;


import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ProductRepositoryMapImplTest {

    private ConcurrentMap<String, Product> concurrentMap;
    private ProductRepositoryMapImpl productRepository;

    @BeforeEach
    public void setUp() {
        concurrentMap = new ConcurrentHashMap<>();
        productRepository = new ProductRepositoryMapImpl(concurrentMap);
    }

    @Test
    public void get_all_products_should_return_correct_products_list() {
        Product product1 = new Product("1", "Product 1", 10.0f, 5, "url1");
        Product product2 = new Product("2", "Product 2", 20.0f, 10, "url2");
        concurrentMap.put(product1.getItemNumber(), product1);
        concurrentMap.put(product2.getItemNumber(), product2);
        var products = productRepository.getAllProducts();
        assertEquals(2, products.size());
        assertTrue(products.contains(product1));
        assertTrue(products.contains(product2));
    }

    @Test
    public void delete_product_should_delete_product_correctly() {
        Product product = new Product("1", "Product 1", 10.0f, 5, "url1");
        concurrentMap.put(product.getItemNumber(), product);
        productRepository.deleteProductByItemNumber(product.getItemNumber());
        var products = productRepository.getAllProducts();
        assertTrue(products.isEmpty());
    }

    @Test
    public void delete_product_should_throw_product_not_found_exception() {
        assertThrows(ProductNotFoundException.class, () -> productRepository.deleteProductByItemNumber("1"));
    }

    @Test
    public void get_all_products_should_get_added_product() {
        Product product = new Product("1", "Product 1", 10.0f, 5, "url1");
        concurrentMap.put(product.getItemNumber(), product);
        var products = productRepository.getAllProducts();
        assertEquals(1, products.size());
        assertTrue(products.contains(product));
    }

    @Test
    public void add_product_should_throw_already_exists_exception_when_add_product_with_same_id() {
        Product product1 = new Product("1", "Product 1", 10.0f, 5, "url1");
        Product product2 = new Product("1", "Product 2", 20.0f, 10, "url2");
        concurrentMap.put(product1.getItemNumber(), product1);
        assertThrows(ProductAlreadyExistsException.class, () -> productRepository.addNewProduct(product2));
    }

    @Test
    public void update_product_should_throw_not_found_exception_when_update_non_existent_product() {
        Product product1 = new Product("1", "Product 1", 10.0f, 5, "url1");
        assertThrows(ProductNotFoundException.class, () -> productRepository.updateProductByItemNumber(product1));
    }

//    @Test
//    public void update_product_should_update_product_correctly() {
//        Product product1 = new Product("1", "Product 1", 10.0f, 5, "url1");
//        concurrentMap.put(product1.getItemNumber(), product1);
//        Product product2 = new Product("1", "Product 1", 40.0f, 8, "url1");
//        assertEquals(product2, productRepository.updateProductByItemNumber(product2));
//    }

//    @Test
//    public void find_all_by_category_url_should_return_correct_list() {
//        String categoryUrl = "category";
//        Product product1 = new Product("1", "Product 1", 10.0f, 5, categoryUrl);
//        Product product2 = new Product("1", "Product 2", 20.0f, 10, "url2");
//        concurrentMap.put(product1.getItemNumber(), product1);
//        concurrentMap.put(product2.getItemNumber(), product2);
//        assertEquals(List.of(product1), productRepository.findAllByCategoryUrl(categoryUrl));
//    }

}
