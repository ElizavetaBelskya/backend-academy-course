package ru.seminar.homework.hw5.rest.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.seminar.homework.hw5.dto.TaskDto;
import ru.seminar.homework.hw5.dto.TaskStatus;
import ru.seminar.homework.hw5.dto.TaskStatusTimeMap;
import ru.seminar.homework.hw5.exception.TaskNotFoundException;
import ru.seminar.homework.hw5.exception.TaskQueueEmptyException;
import ru.seminar.homework.hw5.model.Status;
import ru.seminar.homework.hw5.model.Task;
import ru.seminar.homework.hw5.repository.TaskRepository;
import ru.seminar.homework.hw5.rest.mapper.TaskMapper;
import ru.seminar.homework.hw5.util.UniqueIdGenerator;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TaskServiceImpl taskService;

    private Task task;
    private TaskDto taskDto;

    @BeforeEach
    public void setUp() {
        task = new Task();
        task.setId("1");
        task.setStatus(Status.NEW);
        taskDto = new TaskDto();
        taskDto.setNumber("1");
        taskDto.setStatus("NEW");
    }

    @Test
    public void enqueue_task_should_return_new_task_dto() {
        when(taskRepository.addNewTask()).thenReturn(task);
        when(taskMapper.taskToTaskDto(task)).thenReturn(taskDto);
        TaskDto result = taskService.enqueueTask();
        assertThat(result).matches(x -> x.getStatus().equals(Status.NEW.toString()) && x.getNumber().equals(task.getId()));
    }

    @Test
    public void get_next_waiting_task_when_task_exists_then_return_task_dto() {
        when(taskRepository.getNextWaitingTask()).thenReturn(task);
        when(taskMapper.taskToTaskDto(task)).thenReturn(taskDto);
        TaskDto result = taskService.getNextWaitingTask();
        assertThat(result).matches(x -> x.getStatus().equals(Status.NEW.toString()) && x.getNumber().equals(task.getId()));
    }

    @Test
    public void get_next_waiting_task_when_no_task_should_throw_exception() {
        when(taskRepository.getNextWaitingTask()).thenThrow(TaskQueueEmptyException.class);
        assertThatThrownBy(() -> taskService.getNextWaitingTask()).isInstanceOf(TaskQueueEmptyException.class);
    }

    @Test
    public void update_task_status_should_update_task_correctly() {
        String newStatus = "WAITING";
        task.setStatus(Status.valueOf(newStatus));
        taskDto.setStatus(newStatus);
        when(taskRepository.updateTaskStatusById(task.getId(), Status.valueOf(newStatus))).thenReturn(task);
        when(taskMapper.taskToTaskDto(task)).thenReturn(taskDto);
        TaskDto result = taskService.updateTaskStatus(task.getId(), newStatus);
        assertThat(result).matches(x -> x.getStatus().equals(newStatus)
                && x.getNumber().equals(task.getId()));
    }

    @Test
    public void update_task_status_when_task_does_not_exist_should_throw_exception() {
        String nonExistentTaskNumber = "2";
        String newStatus = "WAITING";
        when(taskRepository.updateTaskStatusById(nonExistentTaskNumber, Status.valueOf(newStatus))).thenThrow(TaskNotFoundException.class);
        assertThatThrownBy(() -> taskService.updateTaskStatus(nonExistentTaskNumber, newStatus)).isInstanceOf(TaskNotFoundException.class);
    }

    @Test
    public void update_task_status_when_status_does_not_exist_should_throw_exception() {
        String invalidStatus = "INVALID";
        assertThatThrownBy(() -> taskService.updateTaskStatus(task.getId(), invalidStatus)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void delete_task_should_throw_exception_when_task_not_found() {
        String notExistingId = UniqueIdGenerator.generateUniqueId();
        doThrow(TaskNotFoundException.class).when(taskRepository).deleteTaskById(notExistingId);
        assertThatThrownBy(() -> taskService.deleteTask(notExistingId)).isInstanceOf(TaskNotFoundException.class);
    }

    @Test
    public void get_tasks_lists_grouped_by_status_should_return_correct_list() {
        Task task1 = Task.builder()
                .id("1")
                .status(Status.NEW)
                .build();
        Task task2 = Task.builder()
                .id("2")
                .status(Status.NEW)
                .build();
        Map<Status, List<Task>> tasksByStatus = Map.of(Status.NEW, List.of(task1, task2));
        TaskDto taskDto1 = new TaskDto();
        taskDto1.setNumber("1");
        taskDto1.setStatus("NEW");
        TaskDto taskDto2 = new TaskDto();
        taskDto2.setNumber("2");
        taskDto2.setStatus("NEW");
        when(taskMapper.taskToTaskDto(task1)).thenReturn(taskDto1);
        when(taskMapper.taskToTaskDto(task2)).thenReturn(taskDto2);
        Map<String, List<TaskDto>> tasksDtoByStatus = Map.of("NEW", List.of(taskDto1, taskDto2));
        when(taskRepository.getTasksSortedByStatusExceptClosed()).thenReturn(tasksByStatus);
        TaskStatus result = taskService.getTasksListsGroupedByStatus();
        assertThat(result.getTasksByStatus()).isEqualTo(tasksDtoByStatus);
    }

    @Test
    public void get_status_map_for_task_should_return_correct_map() {
        Map<String, Double> times = Map.of("NEW", 5.0, "WAITING", 5.3);
        when(taskRepository.getStatusMapByTaskId(task.getId())).thenReturn(times);
        TaskStatusTimeMap result = taskService.getStatusMapForTask(task.getId());
        assertThat(result.getTimes()).isEqualTo(times);
    }

    @Test
    public void get_processing_time_by_task_and_status() {
        Double time = 50.0;
        when(taskRepository.getStatusTimeByTaskIdAndStatus(task.getId(), task.getStatus())).thenReturn(time);
        Double result = taskService.getProcessingTimeByTaskAndStatus(task.getId(), task.getStatus().toString());
        assertThat(result).isEqualTo(time);
    }

    @Test
    public void testCalculateAverageProcessingTimeForAllTasks() {
        Double time = 1.0;
        when(taskRepository.getAverageProcessingTimeForClosedTasks()).thenReturn(time);
        Double result = taskService.calculateAverageProcessingTimeForAllTasks();
        assertThat(result).isEqualTo(time);
    }

    @Test
    public void testCalculateAverageProcessingTimeByStatus() {
        Map<String, Double> times = Map.of("NEW", 4.0, "WAITING", 40.5,  "CLOSE", 30.12);
        when(taskRepository.getAverageStatusTimeForClosedTasks()).thenReturn(times);
        TaskStatusTimeMap result = taskService.calculateAverageProcessingTimeByStatus();
        assertThat(result.getTimes()).isEqualTo(times);
    }



}