package ru.backend.academy.hometask4.repository.impl;


import lombok.extern.slf4j.Slf4j;
import ru.backend.academy.hometask4.dto.ProductDto;
import ru.backend.academy.hometask4.exception.already_exists.ProductAlreadyExistsException;
import ru.backend.academy.hometask4.exception.not_found.ProductNotFoundException;
import ru.backend.academy.hometask4.model.Product;
import ru.backend.academy.hometask4.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

@Slf4j
public class ProductRepositoryMapImpl implements ProductRepository {

    private final ConcurrentMap<String, Product> productHashMap;

    public ProductRepositoryMapImpl(ConcurrentMap<String, Product> map) {
        this.productHashMap = map;
    }

    @Override
    public List<Product> getAllProducts() {
        return new ArrayList<>(productHashMap.values());
    }

    @Override
    public void deleteProductByItemNumber(String itemNumber) {
        if (productHashMap.containsKey(itemNumber)) {
            productHashMap.remove(itemNumber);
        } else {
            log.error("Error while deleting product with item number: {}", itemNumber);
            throw new ProductNotFoundException(itemNumber);
        }
    }

    @Override
    public Product updateProductByItemNumber(Product product) {
        String itemNumber = product.getItemNumber();
        if (productHashMap.containsKey(itemNumber)) {
            return productHashMap.put(itemNumber, product);
        } else {
            log.error("Error while updating product with item number: {}", itemNumber);
            throw new ProductNotFoundException(itemNumber);
        }
    }

    @Override
    public Product addNewProduct(Product product) {
        String itemNumber = product.getItemNumber();
        if (productHashMap.containsKey(itemNumber)) {
            log.info("Attempt to add a product with an existing item number: {}", itemNumber);
            throw new ProductAlreadyExistsException();
        } else {
            productHashMap.put(itemNumber, product);
            return productHashMap.get(itemNumber);
        }
    }

    @Override
    public List<Product> findAllByCategoryUrl(String url) {
        return productHashMap.values().stream().filter(x -> url.equals(x.getCategoryId())).toList();
    }


}
