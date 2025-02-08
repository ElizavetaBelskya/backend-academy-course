package ru.bakcend.academy.hometask1.repository.impl;

import ru.bakcend.academy.hometask1.exception.ProductAlreadyExistsException;
import ru.bakcend.academy.hometask1.exception.ProductNotFoundException;
import ru.bakcend.academy.hometask1.model.Product;
import ru.bakcend.academy.hometask1.repository.ProductRepository;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class ProductRepositoryHashMapImpl implements ProductRepository {

    private final HashMap<String, Product> productHashMap;

    public ProductRepositoryHashMapImpl(HashMap<String, Product> productHashMap) {
        this.productHashMap = productHashMap;
    }

    @Override
    public List<Product> getAllProducts() {
        return productHashMap.values().stream().collect(Collectors.toList());
    }

    @Override
    public boolean deleteProductByItemNumber(String itemNumber) throws ProductNotFoundException {
        if (productHashMap.containsKey(itemNumber)) {
            productHashMap.remove(itemNumber);
            return true;
        } else {
            throw new ProductNotFoundException(itemNumber);
        }
    }

    @Override
    public Product updateProductByItemNumber(Product product) throws ProductNotFoundException {
        String itemNumber = product.getItemNumber();
        if (productHashMap.containsKey(itemNumber)) {
            return productHashMap.put(itemNumber, product);
        } else {
            throw new ProductNotFoundException("Товар с артикулом " + itemNumber + " не найден.");
        }
    }

    @Override
    public Product addNewProduct(Product product) throws ProductAlreadyExistsException {
        if (product == null) {
            throw new IllegalArgumentException("product не может быть null.");
        }
        String itemNumber = product.getItemNumber();
        if (productHashMap.containsKey(itemNumber)) {
            throw new ProductAlreadyExistsException(productHashMap.get(itemNumber));
        } else {
            return productHashMap.put(product.getItemNumber(), product);
        }
    }
}
