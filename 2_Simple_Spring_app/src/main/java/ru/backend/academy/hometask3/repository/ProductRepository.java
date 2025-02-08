package ru.backend.academy.hometask3.repository;

import ru.backend.academy.hometask3.model.Product;

import java.util.List;

public interface ProductRepository {
    List<Product> getAllProducts();

    void deleteProductByItemNumber(String id);

    Product updateProductByItemNumber(Product product);

    Product addNewProduct(Product product);

}
