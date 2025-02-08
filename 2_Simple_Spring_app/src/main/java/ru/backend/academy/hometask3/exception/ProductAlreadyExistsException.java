package ru.backend.academy.hometask3.exception;


import org.springframework.http.HttpStatus;

public class ProductAlreadyExistsException extends ProductDatabaseException {

    private static final String ALREADY_EXISTS_MESSAGE = "A product with this item number already exists";

    public ProductAlreadyExistsException() {
        super(ALREADY_EXISTS_MESSAGE, HttpStatus.CONFLICT.value());
    }


}
