package ru.backend.academy.hometask4.repository;

import ru.backend.academy.hometask4.model.Product;

import java.util.List;

public interface ProductRepository {

    List<Product> getAllProducts();

    void deleteProductByItemNumber(String id);

    Product updateProductByItemNumber(Product product);

    Product addNewProduct(Product product);

    List<Product> findAllByCategoryUrl(String url);
}
