package ru.seminar.homework.hw5.exception;

import org.springframework.http.HttpStatus;

public class TaskQueueEmptyException extends TaskQueueException {

    private static final String TASKS_NOT_FOUND_MESSAGE = "Tasks are not found";

    public TaskQueueEmptyException() {
        super(HttpStatus.NOT_FOUND, TASKS_NOT_FOUND_MESSAGE);
    }

}
