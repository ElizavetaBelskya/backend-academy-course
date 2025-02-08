package ru.backend.academy.hometask4.exception;

import lombok.Getter;

@Getter
public abstract class BaseDatabaseException extends RuntimeException {

    private final int status;
    protected BaseDatabaseException(String message, int status) {
        super(message);
        this.status = status;
    }

}
