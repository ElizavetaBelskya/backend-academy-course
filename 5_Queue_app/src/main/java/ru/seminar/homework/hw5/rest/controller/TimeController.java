package ru.seminar.homework.hw5.rest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.seminar.homework.hw5.api.TimesApi;
import ru.seminar.homework.hw5.dto.TaskStatusTimeMap;
import ru.seminar.homework.hw5.rest.service.TaskService;

@RestController
@RequiredArgsConstructor
public class TimeController implements TimesApi {

    private final TaskService taskService;

    @Override
    public ResponseEntity<Double> getAverageProcessingTime() {
        return ResponseEntity.ok(taskService.calculateAverageProcessingTimeForAllTasks());
    }


    @Override
    public ResponseEntity<TaskStatusTimeMap> getProcessingTimeMapForTask(@PathVariable String number) {
        return ResponseEntity.ok(taskService.getStatusMapForTask(number));
    }


    @Override
    public ResponseEntity<Double> getProcessingTimeForTaskByStatus(@PathVariable String number, @PathVariable String status) {
        return ResponseEntity.ok(taskService.getProcessingTimeByTaskAndStatus(number, status));
    }


    @Override
    public ResponseEntity<TaskStatusTimeMap> getAverageProcessingTimeByStatus() {
        return ResponseEntity.ok(taskService.calculateAverageProcessingTimeByStatus());
    }


}
