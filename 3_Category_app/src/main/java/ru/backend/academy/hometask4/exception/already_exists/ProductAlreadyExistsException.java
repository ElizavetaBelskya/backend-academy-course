package ru.backend.academy.hometask4.exception.already_exists;


import org.springframework.http.HttpStatus;
import ru.backend.academy.hometask4.exception.BaseDatabaseException;

public class ProductAlreadyExistsException extends BaseDatabaseException {

    private static final String PRODUCT_ALREADY_EXISTS_MESSAGE = "A product with this item number already exists";

    public ProductAlreadyExistsException() {
        super(PRODUCT_ALREADY_EXISTS_MESSAGE, HttpStatus.CONFLICT.value());
    }


}
