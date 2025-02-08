package ru.seminar.homework.hw5.soap.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.seminar.homework.hw5.model.Status;
import ru.seminar.homework.hw5.repository.TaskRepository;
import ru.seminar.homework.hw5.soap.mapper.TaskMapper;
import ru.seminar.homework.hw5.soap.soap_dto.TaskDto;

@Service
@RequiredArgsConstructor
public class TaskSoapService {

    private final TaskRepository taskRepository;
    private static final TaskMapper mapper = TaskMapper.INSTANCE;

    public TaskDto enqueueTask() {
        return mapper.taskToTaskDto(taskRepository.addNewTask());
    }

    public TaskDto getNextWaitingTask() {
        return mapper.taskToTaskDto(taskRepository.getNextWaitingTask());
    }

    public TaskDto updateTaskStatus(String number, String newStatus) {
        return mapper.taskToTaskDto(taskRepository.updateTaskStatusById(number, Status.valueOf(newStatus)));
    }

    public void deleteTask(String number) {
        taskRepository.deleteTaskById(number);
    }

}
