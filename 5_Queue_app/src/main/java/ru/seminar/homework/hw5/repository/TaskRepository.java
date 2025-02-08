package ru.seminar.homework.hw5.repository;

import ru.seminar.homework.hw5.model.Status;
import ru.seminar.homework.hw5.model.Task;

import java.util.List;
import java.util.Map;

public interface TaskRepository {

    Task addNewTask();

    Task getNextWaitingTask();

    Task updateTaskStatusById(String id, Status status);

    void deleteTaskById(String id);

    Map<Status, List<Task>> getTasksSortedByStatusExceptClosed();

    Map<String, Double> getStatusMapByTaskId(String id);

    Double getStatusTimeByTaskIdAndStatus(String id, Status status);

    Double getAverageProcessingTimeForClosedTasks();

    Map<String, Double> getAverageStatusTimeForClosedTasks();

    Map<String, Task> getAllTasks();
}
