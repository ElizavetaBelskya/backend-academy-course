package ru.tinkoff.belskya.hw7.exception;

import org.springframework.http.HttpStatus;

public class AlreadyExistsException extends EducationPlatformBasicException {
    public AlreadyExistsException(String message, String duplicate) {
        super(String.format(message, duplicate), HttpStatus.CONFLICT);
    }
}
