package ru.seminar.homework.hw5.rest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.seminar.homework.hw5.api.TaskApi;
import ru.seminar.homework.hw5.api.TasksApi;
import ru.seminar.homework.hw5.dto.TaskDto;
import ru.seminar.homework.hw5.dto.TaskStatus;
import ru.seminar.homework.hw5.rest.service.TaskService;


@RestController
@RequiredArgsConstructor
public class TaskController implements TaskApi, TasksApi {

    private final TaskService taskService;

    @Override
    public ResponseEntity<TaskDto> getNextWaitingTask() {
        return ResponseEntity.ok(taskService.getNextWaitingTask());
    }

    @Override
    public ResponseEntity<TaskDto> updateTaskStatus(String number, String newStatus) {
        return ResponseEntity.accepted().body(taskService.updateTaskStatus(number, newStatus));
    }

    @Override
    public ResponseEntity<TaskDto> createTask() {
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.enqueueTask());
    }

    @Override
    public ResponseEntity<Void> deleteTask(String number) {
        taskService.deleteTask(number);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<TaskStatus> getTasksByStatus() {
        return ResponseEntity.ok(taskService.getTasksListsGroupedByStatus());
    }
}
