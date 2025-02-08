package ru.tinkoff.belskya.hw7.exception;

import org.springframework.http.HttpStatus;

public class EducationPlatformOptimisticLockingException extends EducationPlatformBasicException {
    public EducationPlatformOptimisticLockingException(String message, Long id) {
        super(String.format(message, id), HttpStatus.CONFLICT);
    }
}
