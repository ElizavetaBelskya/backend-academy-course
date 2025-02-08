package ru.backend.academy.hometask3.exception;

import org.springframework.http.HttpStatus;

public class CsvDatabaseException extends ProductDatabaseException {

    private static final String CSV_DATABASE_EXCEPTION_MESSAGE = "An issue occurred while working with the database";

    public CsvDatabaseException() {
        super(CSV_DATABASE_EXCEPTION_MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

}
