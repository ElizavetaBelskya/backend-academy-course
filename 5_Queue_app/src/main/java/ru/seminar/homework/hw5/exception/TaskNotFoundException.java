package ru.seminar.homework.hw5.exception;

import org.springframework.http.HttpStatus;

public class TaskNotFoundException extends TaskQueueException {

    private static final String NOT_FOUND_MESSAGE = "Task with ID %s not found";

    public TaskNotFoundException(String id) {
        super(HttpStatus.NOT_FOUND, String.format(NOT_FOUND_MESSAGE, id));
    }
}
