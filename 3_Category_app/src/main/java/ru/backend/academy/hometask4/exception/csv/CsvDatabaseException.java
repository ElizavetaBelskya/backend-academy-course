package ru.backend.academy.hometask4.exception.csv;

import org.springframework.http.HttpStatus;
import ru.backend.academy.hometask4.exception.BaseDatabaseException;

public class CsvDatabaseException extends BaseDatabaseException {

    private static final String CSV_DATABASE_EXCEPTION_MESSAGE = "An issue occurred while working with the database";

    public CsvDatabaseException() {
        super(CSV_DATABASE_EXCEPTION_MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

}
