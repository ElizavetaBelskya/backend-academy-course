package ru.seminar.homework.hw5.repository.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.seminar.homework.hw5.exception.IncorrectTaskStatusException;
import ru.seminar.homework.hw5.exception.TaskNotFoundException;
import ru.seminar.homework.hw5.exception.TaskQueueEmptyException;
import ru.seminar.homework.hw5.model.Status;
import ru.seminar.homework.hw5.model.Task;
import ru.seminar.homework.hw5.repository.TaskRepository;
import ru.seminar.homework.hw5.util.UniqueIdGenerator;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class TaskRepositoryQueueImpl implements TaskRepository {

    public static final double MILLIS_TO_SECONDS_COEFFICIENT = 1000.0;

    private final Queue<Task> queue;

    private final Map<String, Task> tasksMap;

    @Override
    public Task addNewTask() {
        Task task = new Task();
        task.setId(UniqueIdGenerator.generateUniqueId());
        task.setStatus(Status.NEW);
        tasksMap.put(task.getId(), task);
        return task;
    }

    @Override
    public Task getNextWaitingTask() {
        Task task = queue.peek();
        if (task == null) {
            throw new TaskQueueEmptyException();
        } else {
            return task;
        }
    }

    @Override
    public Task updateTaskStatusById(String id, Status newStatus) {
        Task task = getTask(id);
        Status currentStatus = task.getStatus();

        if (newStatus.equals(currentStatus)) {
            return task;
        }

        if (Status.CANCEL.equals(newStatus)) {
            task.setStatus(Status.CANCEL);
            queue.remove(task);
        } else if (Status.WAITING.equals(currentStatus)) {
            if (!Status.CLOSE.equals(newStatus)) {
                queue.remove(task);
                task.setStatus(newStatus);
            } else {
                throw new IncorrectTaskStatusException("Cannot move from WAITING to CLOSE.");
            }
        } else if (Status.PROCESSED.equals(currentStatus)) {
            if (Status.WAITING.equals(newStatus)) {
                task.setStatus(newStatus);
                queue.add(task);
            } else if (Status.CLOSE.equals(newStatus)) {
                task.setStatus(newStatus);
                queue.remove(task);
            } else {
                throw new IncorrectTaskStatusException("Invalid status transition from PROCESSED.");
            }
        } else if (Status.NEW.equals(currentStatus)) {
            if (Status.WAITING.equals(newStatus)) {
                task.setStatus(newStatus);
                queue.add(task);
            } else if (Status.CLOSE.equals(newStatus)) {
                task.setStatus(newStatus);
            } else {
                throw new IncorrectTaskStatusException("Invalid status transition from NEW.");
            }
        } else {
            throw new IncorrectTaskStatusException("Invalid current task status.");
        }
        return task;
    }

    @Override
    public void deleteTaskById(String id) {
        Task task = getTask(id);
        if (Status.WAITING.equals(task.getStatus())) {
            queue.remove(task);
        }
        tasksMap.remove(id);
    }

    @Override
    public Map<Status, List<Task>> getTasksSortedByStatusExceptClosed() {
        return tasksMap.values().stream()
                .filter(x -> !Status.CLOSE.equals(x.getStatus()) && !Status.CANCEL.equals(x.getStatus()))
                .collect(Collectors.groupingBy(Task::getStatus));
    }

    @Override
    public Map<String, Double> getStatusMapByTaskId(String id) {
        Task task = getTask(id);
        Map<String, Double> statusMap = new HashMap<>();
        Map<Status, Long> startTimesMap = task.getStatusStartTimesMap();
        Long currentStatusTime = startTimesMap.get(task.getStatus());
        for (Map.Entry<Status, Long> statusEntry : task.getStatusTime().entrySet()) {
            double seconds;
            if (statusEntry.getKey() == task.getStatus()) {
                seconds = ((System.currentTimeMillis() - currentStatusTime + statusEntry.getValue()) / MILLIS_TO_SECONDS_COEFFICIENT);
            } else {
                seconds = ((double) statusEntry.getValue() / MILLIS_TO_SECONDS_COEFFICIENT);
            }
            statusMap.put(String.valueOf(statusEntry.getKey()), seconds);
        }
        return statusMap;
    }

    @Override
    public Double getStatusTimeByTaskIdAndStatus(String id, Status status) {
        Task task = getTask(id);
        Map<Status, Long> timesMap = task.getStatusTime();
        Double time = 0.0;
        if (status.equals(task.getStatus())) {
            time = ((double) (System.currentTimeMillis() - task.getStatusStartTimesMap().get(status))) / MILLIS_TO_SECONDS_COEFFICIENT;
        } else {
            time = ((double) timesMap.get(status)) / MILLIS_TO_SECONDS_COEFFICIENT;
        }
        return time;
    }

    @Override
    public Double getAverageProcessingTimeForClosedTasks() {
        OptionalDouble average =  tasksMap.values().stream()
                .filter(x -> Status.CANCEL.equals(x.getStatus()) || Status.CLOSE.equals(x.getStatus()))
                .mapToLong(x -> x.getStatusTime().values().stream().mapToLong(y -> y).sum()).average();
        return average.isPresent() ? average.getAsDouble() : 0.0;
    }

    @Override
    public Map<String, Double> getAverageStatusTimeForClosedTasks() {
        List<Task> closedTasksList = tasksMap.values().stream()
                .filter(x -> Status.CANCEL.equals(x.getStatus()) || Status.CLOSE.equals(x.getStatus())).toList();
        Map<Status, Long> sumTimeStatusMap = new HashMap<>();
        Map<Status, Integer> countTimeStatusMap = new HashMap<>();
        for (Status status: Status.values()) {
            sumTimeStatusMap.put(status, 0L);
            countTimeStatusMap.put(status, 0);
        }
        for (Task task: closedTasksList) {
            Map<Status, Long> statusTimeMap = task.getStatusTime();
            for (Status status: sumTimeStatusMap.keySet()) {
                Long previousSum = sumTimeStatusMap.get(status);
                sumTimeStatusMap.put(status, previousSum + statusTimeMap.get(status));
                countTimeStatusMap.put(status, countTimeStatusMap.get(status) + 1);
            }
        }
        Map<String, Double> averageTimeStatusMap = new HashMap<>();
        for (Status status: Status.values()) {
            averageTimeStatusMap.put(String.valueOf(status),
                    ((double) sumTimeStatusMap.get(status) / countTimeStatusMap.get(status))/MILLIS_TO_SECONDS_COEFFICIENT);
        }
        return averageTimeStatusMap;
    }

    @Override
    public Map<String, Task> getAllTasks() {
        return tasksMap;
    }

    private Task getTask(String id) {
        Task task = tasksMap.get(id);
        if (task == null) {
            throw new TaskNotFoundException(id);
        }
        return task;
    }

}
