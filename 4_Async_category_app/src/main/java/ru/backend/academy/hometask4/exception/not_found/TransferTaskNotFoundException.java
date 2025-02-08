package ru.backend.academy.hometask4.exception.not_found;

public class TransferTaskNotFoundException extends BaseNotFoundException {

    private static final String TASK_NOT_FOUND_MESSAGE = "Task with id %d not found";
    public TransferTaskNotFoundException(Long taskId) {
        super(String.format(TASK_NOT_FOUND_MESSAGE, taskId));
    }

}

