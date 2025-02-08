package ru.seminar.homework.hw5.rest.service;

import ru.seminar.homework.hw5.dto.TaskDto;
import ru.seminar.homework.hw5.dto.TaskStatus;
import ru.seminar.homework.hw5.dto.TaskStatusTimeMap;

public interface TaskService {
    TaskDto enqueueTask();

    TaskDto getNextWaitingTask();

    TaskDto updateTaskStatus(String number, String newStatus);

    void deleteTask(String number);

    TaskStatus getTasksListsGroupedByStatus();

    TaskStatusTimeMap getStatusMapForTask(String number);

    Double getProcessingTimeByTaskAndStatus(String number, String status);

    Double calculateAverageProcessingTimeForAllTasks();

    TaskStatusTimeMap calculateAverageProcessingTimeByStatus();
}
