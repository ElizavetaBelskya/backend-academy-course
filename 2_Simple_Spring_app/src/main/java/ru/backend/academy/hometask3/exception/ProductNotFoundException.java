package ru.backend.academy.hometask3.exception;

import org.springframework.http.HttpStatus;

public class ProductNotFoundException extends ProductDatabaseException {

    private static final String PRODUCT_NOT_FOUND_MESSAGE = "Product with item number %s not found";

    public ProductNotFoundException(String itemNumber) {
        super(String.format(PRODUCT_NOT_FOUND_MESSAGE, itemNumber), HttpStatus.NOT_FOUND.value());
    }
}
