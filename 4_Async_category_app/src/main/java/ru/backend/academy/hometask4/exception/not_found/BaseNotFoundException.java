package ru.backend.academy.hometask4.exception.not_found;

import org.springframework.http.HttpStatus;
import ru.backend.academy.hometask4.exception.BaseDatabaseException;

public abstract class BaseNotFoundException extends BaseDatabaseException {

    protected BaseNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND.value());
    }

}
