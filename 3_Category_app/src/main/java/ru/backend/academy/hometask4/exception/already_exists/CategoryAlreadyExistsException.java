package ru.backend.academy.hometask4.exception.already_exists;

import org.springframework.http.HttpStatus;
import ru.backend.academy.hometask4.exception.BaseDatabaseException;

public class CategoryAlreadyExistsException extends BaseDatabaseException {

    private static final String CATEGORY_ALREADY_EXISTS_MESSAGE = "A category with this url already exists";

    public CategoryAlreadyExistsException() {
        super(CATEGORY_ALREADY_EXISTS_MESSAGE, HttpStatus.CONFLICT.value());
    }

}
