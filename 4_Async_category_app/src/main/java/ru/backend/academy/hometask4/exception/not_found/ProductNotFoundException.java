package ru.backend.academy.hometask4.exception.not_found;

public class ProductNotFoundException extends BaseNotFoundException {

    private static final String PRODUCT_NOT_FOUND_MESSAGE = "Product with item number %s not found";

    public ProductNotFoundException(String itemNumber) {
        super(String.format(PRODUCT_NOT_FOUND_MESSAGE, itemNumber));
    }
}
