package ru.seminar.homework.hw5.rest.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.seminar.homework.hw5.dto.TaskDto;
import ru.seminar.homework.hw5.dto.TaskStatus;
import ru.seminar.homework.hw5.dto.TaskStatusTimeMap;
import ru.seminar.homework.hw5.model.Status;
import ru.seminar.homework.hw5.model.Task;
import ru.seminar.homework.hw5.repository.TaskRepository;
import ru.seminar.homework.hw5.rest.mapper.TaskMapper;
import ru.seminar.homework.hw5.rest.service.TaskService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    private final TaskMapper mapper;

    @Override
    public TaskDto enqueueTask() {
        return mapper.taskToTaskDto(taskRepository.addNewTask());
    }

    @Override
    public TaskDto getNextWaitingTask() {
        return mapper.taskToTaskDto(taskRepository.getNextWaitingTask());
    }

    @Override
    public TaskDto updateTaskStatus(String number, String newStatus) {
        return mapper.taskToTaskDto(taskRepository.updateTaskStatusById(number, Status.valueOf(newStatus)));
    }

    @Override
    public void deleteTask(String number) {
        taskRepository.deleteTaskById(number);
    }

    @Override
    public TaskStatus getTasksListsGroupedByStatus() {
        Map<Status, List<Task>> tasksByStatus = taskRepository.getTasksSortedByStatusExceptClosed();
        Map<String, List<TaskDto>> tasksDtoByStatus = tasksByStatus.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey().toString(),
                        entry -> entry.getValue()
                                .stream()
                                .map(mapper::taskToTaskDto)
                                .toList()
                ));
        TaskStatus taskStatusMap = new TaskStatus();
        taskStatusMap.setTasksByStatus(tasksDtoByStatus);
        return taskStatusMap;
    }

    @Override
    public TaskStatusTimeMap getStatusMapForTask(String number) {
        TaskStatusTimeMap taskStatusTimeMap = new TaskStatusTimeMap();
        taskStatusTimeMap.setTimes(taskRepository.getStatusMapByTaskId(number));
        return taskStatusTimeMap;
    }

    @Override
    public Double getProcessingTimeByTaskAndStatus(String number, String status) {
        return taskRepository.getStatusTimeByTaskIdAndStatus(number, Status.valueOf(status));
    }

    @Override
    public Double calculateAverageProcessingTimeForAllTasks() {
        return taskRepository.getAverageProcessingTimeForClosedTasks();
    }

    @Override
    public TaskStatusTimeMap calculateAverageProcessingTimeByStatus() {
        TaskStatusTimeMap taskStatusTimeMap = new TaskStatusTimeMap();
        taskStatusTimeMap.setTimes(taskRepository.getAverageStatusTimeForClosedTasks());
        return taskStatusTimeMap;
    }

}
