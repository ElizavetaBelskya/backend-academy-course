package ru.bakcend.academy.hometask1.repository;

import ru.bakcend.academy.hometask1.model.Product;
import ru.bakcend.academy.hometask1.exception.ProductAlreadyExistsException;
import ru.bakcend.academy.hometask1.exception.ProductNotFoundException;

import java.util.List;

public interface ProductRepository {
    List<Product> getAllProducts();

    boolean deleteProductByItemNumber(String id) throws ProductNotFoundException;

    Product updateProductByItemNumber(Product product) throws ProductNotFoundException;

    Product addNewProduct(Product product) throws ProductAlreadyExistsException;

}
