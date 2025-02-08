package ru.backend.academy.hometask3.exception;

import lombok.Getter;

@Getter
public class ProductDatabaseException extends RuntimeException {

    private final int status;
    public ProductDatabaseException(String message, int status) {
        super(message);
        this.status = status;
    }

}
