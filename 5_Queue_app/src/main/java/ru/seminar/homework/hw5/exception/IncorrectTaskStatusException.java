package ru.seminar.homework.hw5.exception;

import org.springframework.http.HttpStatus;

public class IncorrectTaskStatusException extends TaskQueueException {

    public IncorrectTaskStatusException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }

}
