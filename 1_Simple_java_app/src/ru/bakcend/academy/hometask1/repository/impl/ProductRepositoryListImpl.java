package ru.bakcend.academy.hometask1.repository.impl;

import ru.bakcend.academy.hometask1.model.Product;
import ru.bakcend.academy.hometask1.exception.ProductAlreadyExistsException;
import ru.bakcend.academy.hometask1.exception.ProductNotFoundException;
import ru.bakcend.academy.hometask1.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductRepositoryListImpl implements ProductRepository {

    private final List<Product> productsList;

    public ProductRepositoryListImpl() {
        productsList = new ArrayList<>();
    }

    @Override
    public List<Product> getAllProducts() {
        return productsList;
    }

    @Override
    public boolean deleteProductByItemNumber(String itemNumber) throws ProductNotFoundException {
        Optional<Product> product = findProduct(itemNumber);
        if (product.isPresent()) {
            return productsList.remove(product.get());
        } else {
            throw new ProductNotFoundException(itemNumber);
        }
    }

    @Override
    public Product updateProductByItemNumber(Product productToUpdate) throws ProductNotFoundException {
        String itemNumber = productToUpdate.getItemNumber();
        for (int i = 0; i < productsList.size(); i++) {
            Product oldProduct = productsList.get(i);
            if (oldProduct.getItemNumber().equals(itemNumber)) {
                return productsList.set(i, productToUpdate);
            }
        }
        throw new ProductNotFoundException(itemNumber);
    }

    @Override
    public Product addNewProduct(Product product) throws ProductAlreadyExistsException {
        Optional<Product> foundProduct = findProduct(product.getItemNumber());
        if (foundProduct.isPresent()) {
            throw new ProductAlreadyExistsException(foundProduct.get());
        } else {
            productsList.add(product);
            return product;
        }
    }

    private Optional<Product> findProduct(String itemNumber) {
        for (Product product: productsList) {
            if (product.getItemNumber().equals(itemNumber)) {
                return Optional.of(product);
            }
        }
        return Optional.empty();
    }

}
