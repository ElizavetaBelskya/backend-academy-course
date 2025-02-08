package ru.seminar.homework.hw5.exception;

import org.springframework.http.HttpStatus;

public abstract class TaskQueueException extends RuntimeException {

    private final HttpStatus httpStatus;

    public TaskQueueException(HttpStatus status, String message) {
        super(message);
        this.httpStatus = status;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
