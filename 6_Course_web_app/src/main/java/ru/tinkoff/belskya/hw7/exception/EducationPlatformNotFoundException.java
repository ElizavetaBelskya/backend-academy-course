package ru.tinkoff.belskya.hw7.exception;

import org.springframework.http.HttpStatus;

public class EducationPlatformNotFoundException extends EducationPlatformBasicException {


    public EducationPlatformNotFoundException(String message, Long id) {
        super(String.format(message, id), HttpStatus.NOT_FOUND);
    }

    public EducationPlatformNotFoundException(String message) {
        super(String.format(message), HttpStatus.NOT_FOUND);
    }
}
