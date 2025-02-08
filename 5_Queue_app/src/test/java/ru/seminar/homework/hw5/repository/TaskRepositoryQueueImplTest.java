package ru.seminar.homework.hw5.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import ru.seminar.homework.hw5.exception.IncorrectTaskStatusException;
import ru.seminar.homework.hw5.exception.TaskNotFoundException;
import ru.seminar.homework.hw5.exception.TaskQueueEmptyException;
import ru.seminar.homework.hw5.model.Status;
import ru.seminar.homework.hw5.model.Task;
import ru.seminar.homework.hw5.repository.impl.TaskRepositoryQueueImpl;
import ru.seminar.homework.hw5.util.UniqueIdGenerator;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static ru.seminar.homework.hw5.repository.impl.TaskRepositoryQueueImpl.MILLIS_TO_SECONDS_COEFFICIENT;


@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class TaskRepositoryQueueImplTest {

    private TaskRepositoryQueueImpl taskRepository;

    private Queue<Task> queue;

    private Map<String, Task> tasksMap;

    @BeforeEach
    public void setUp() {
        queue = new ConcurrentLinkedQueue<>();
        tasksMap = new ConcurrentHashMap<>();
        taskRepository = new TaskRepositoryQueueImpl(queue, tasksMap);
    }

    @Test
    public void add_new_task_then_task_map_should_contain_added_task_with_new_status() {
        Task task = taskRepository.addNewTask();
        assertNotNull(task);
        assertEquals(Status.NEW, task.getStatus());
        assertTrue(tasksMap.containsKey(task.getId()));
    }

    @Test
    public void add_multiple_tasks_then_task_map_should_contain_all_added_task_with_new_status() {
        int numTasks = 5;
        for (int i = 0; i < numTasks; i++) {
            Task task = taskRepository.addNewTask();
            assertNotNull(task);
            assertEquals(Status.NEW, task.getStatus());
            assertTrue(tasksMap.containsKey(task.getId()));
        }
        assertEquals(numTasks, tasksMap.size());
    }

    @Test
    public void get_next_waiting_task_should_throw_exception_when_list_is_empty() {
        assertThrows(TaskQueueEmptyException.class, () -> {
            taskRepository.getNextWaitingTask();
        });
    }

    @Test
    public void get_next_waiting_task_should_return_waiting_task() {
        Task task = new Task();
        task.setId("1");
        task.setStatus(Status.WAITING);
        queue.add(task);
        Task nextTask = taskRepository.getNextWaitingTask();
        assertNotNull(nextTask);
        assertEquals(task, nextTask);
    }

    @Test
    public void get_next_waiting_task_from_empty_queue_should_throw_exception() {
        Task task = new Task();
        task.setId("1");
        task.setStatus(Status.NEW);
        tasksMap.put(task.getId(), task);
        assertThrows(TaskQueueEmptyException.class, () -> {
            taskRepository.getNextWaitingTask();
        });
    }

    @Test
    public void update_task_status_should_update_status_from_new_to_waiting() {
        Task task = new Task();
        task.setId("1");
        task.setStatus(Status.NEW);
        tasksMap.put(task.getId(), task);
        Task updatedTask = taskRepository.updateTaskStatusById(task.getId(), Status.WAITING);
        assertEquals(Status.WAITING, updatedTask.getStatus());
        assertTrue(queue.contains(updatedTask));
    }

    @Test
    public void update_task_status_should_update_status_from_waiting_to_close() {
        Task task = new Task();
        task.setId(UniqueIdGenerator.generateUniqueId());
        task.setStatus(Status.WAITING);
        tasksMap.put(task.getId(), task);
        queue.add(task);
        assertThrows(IncorrectTaskStatusException.class, () -> {
            taskRepository.updateTaskStatusById(task.getId(), Status.CLOSE);
        });
    }

    @Test
    public void update_task_status_should_update_status_from_processed_to_waiting() {
        Task task = new Task();
        task.setId(UniqueIdGenerator.generateUniqueId());
        task.setStatus(Status.PROCESSED);
        tasksMap.put(task.getId(), task);

        Task updatedTask = taskRepository.updateTaskStatusById(task.getId(), Status.WAITING);
        assertEquals(Status.WAITING, updatedTask.getStatus());
        assertTrue(queue.contains(updatedTask));
    }

    @Test
    public void update_task_status_should_update_status_from_processed_to_close() {
        Task task = new Task();
        task.setId(UniqueIdGenerator.generateUniqueId());
        task.setStatus(Status.PROCESSED);
        tasksMap.put(task.getId(), task);
        Task updatedTask = taskRepository.updateTaskStatusById(task.getId(), Status.CLOSE);
        assertEquals(Status.CLOSE, updatedTask.getStatus());
        assertFalse(queue.contains(updatedTask));
    }

    @Test
    public void update_task_status_should_update_status_from_new_to_close() {
        Task task = new Task();
        task.setId(UniqueIdGenerator.generateUniqueId());
        task.setStatus(Status.NEW);
        tasksMap.put(task.getId(), task);
        Task updatedTask = taskRepository.updateTaskStatusById(task.getId(), Status.CLOSE);
        assertEquals(Status.CLOSE, updatedTask.getStatus());
        assertFalse(queue.contains(updatedTask));
    }

    @Test
    public void update_task_status_should_throw_exception_when_update_status_from_new_to_processed() {
        Task task = new Task();
        task.setId(UniqueIdGenerator.generateUniqueId());
        task.setStatus(Status.NEW);
        tasksMap.put(task.getId(), task);
        assertThrows(IncorrectTaskStatusException.class, () -> {
            taskRepository.updateTaskStatusById(task.getId(), Status.PROCESSED);
        });
    }

    @Test
    public void update_task_status_should_throw_exception_when_update_status_from_processed_to_new() {
        Task task = new Task();
        task.setId(UniqueIdGenerator.generateUniqueId());
        task.setStatus(Status.PROCESSED);
        tasksMap.put(task.getId(), task);
        assertThrows(IncorrectTaskStatusException.class, () -> {
            taskRepository.updateTaskStatusById(task.getId(), Status.NEW);
        });
    }

    @Test
    public void update_task_status_should_update_status_from_processed_to_cancel() {
        Task task = new Task();
        task.setId(UniqueIdGenerator.generateUniqueId());
        task.setStatus(Status.PROCESSED);
        tasksMap.put(task.getId(), task);
        taskRepository.updateTaskStatusById(task.getId(), Status.CANCEL);
        assertFalse(queue.contains(task));
        assertEquals(Status.CANCEL, task.getStatus());
    }

    @Test
    public void delete_task_by_id_should_remove_waiting_task_from_list_and_queue() {
        Task task = new Task();
        task.setId(UniqueIdGenerator.generateUniqueId());
        task.setStatus(Status.WAITING);
        tasksMap.put(task.getId(), task);
        queue.add(task);
        taskRepository.deleteTaskById(task.getId());

        assertFalse(queue.contains(task));
        assertFalse(tasksMap.containsKey(task.getId()));
    }

    @Test
    public void delete_task_by_id_should_throw_exception_when_task_not_found() {
        assertThrows(TaskNotFoundException.class, () -> {
            taskRepository.deleteTaskById(UniqueIdGenerator.generateUniqueId());
        });
    }

    @Test
    public void get_tasks_sorted_by_status_except_closed_should_return_tasks_with_statuses_processed_waiting_new() {
        Task task0 = new Task();
        task0.setId(UniqueIdGenerator.generateUniqueId());
        task0.setStatus(Status.NEW);
        tasksMap.put(task0.getId(), task0);
        Task task1 = new Task();
        task1.setId(UniqueIdGenerator.generateUniqueId());
        task1.setStatus(Status.WAITING);
        tasksMap.put(task1.getId(), task1);
        queue.add(task1);
        Task task2 = new Task();
        task2.setId(UniqueIdGenerator.generateUniqueId());
        task2.setStatus(Status.PROCESSED);
        tasksMap.put(task2.getId(), task2);
        queue.add(task2);
        Task task3 = new Task();
        task3.setId(UniqueIdGenerator.generateUniqueId());
        task3.setStatus(Status.CANCEL);
        tasksMap.put(task3.getId(), task3);
        Task task4 = new Task();
        task4.setId(UniqueIdGenerator.generateUniqueId());
        task4.setStatus(Status.CLOSE);
        tasksMap.put(task4.getId(), task4);
        Map<Status, List<Task>> taskStatusMap = taskRepository.getTasksSortedByStatusExceptClosed();
        assertFalse(taskStatusMap.containsKey(Status.CLOSE));
        assertFalse(taskStatusMap.containsKey(Status.CANCEL));
        assertTrue(taskStatusMap.containsKey(Status.WAITING));
        assertTrue(taskStatusMap.containsKey(Status.PROCESSED));
        assertTrue(taskStatusMap.containsKey(Status.NEW));
        assertEquals(1, taskStatusMap.get(Status.WAITING).size());
        assertEquals(1, taskStatusMap.get(Status.PROCESSED).size());
        assertEquals(1, taskStatusMap.get(Status.NEW).size());
    }

    @Test
    public void get_status_map_by_task_id_should_return_correct_status_times() {
        Task task1 = new Task();
        task1.setId(UniqueIdGenerator.generateUniqueId());
        tasksMap.put(task1.getId(), task1);
        Map<Status, Long> statusTimes = task1.getStatusTime();
        statusTimes.put(Status.NEW, TimeUnit.MINUTES.toMillis(5));
        statusTimes.put(Status.WAITING, TimeUnit.MINUTES.toMillis(3));
        statusTimes.put(Status.PROCESSED, TimeUnit.MINUTES.toMillis(1));
        task1.setStatus(Status.WAITING);
        Map<String, Double> statusMap = taskRepository.getStatusMapByTaskId(task1.getId());
        assertEquals(Status.values().length, statusMap.size());
        assertTrue(statusMap.containsKey(Status.NEW.toString()));
        assertTrue(statusMap.containsKey(Status.WAITING.toString()));
        assertTrue(statusMap.containsKey(Status.PROCESSED.toString()));
        double newStatusTime = 300.0;
        double waitingStatusTime = 180.0;
        double processedStatusTime = 60.0;
        assertEquals(newStatusTime, statusMap.get(Status.NEW.toString()), 1.0);
        assertEquals( waitingStatusTime + (System.currentTimeMillis() - task1.getStatusStartTimesMap().get(Status.WAITING))/ MILLIS_TO_SECONDS_COEFFICIENT,
                statusMap.get(Status.WAITING.toString()), 1.0);
        assertEquals(processedStatusTime, statusMap.get(Status.PROCESSED.toString()), 1.0);
    }

    @Test
    public void get_status_time_by_task_id_and_status_should_return_correct_times() {
        Task task1 = new Task();
        task1.setId(UniqueIdGenerator.generateUniqueId());
        tasksMap.put(task1.getId(), task1);
        task1.setStatus(Status.CANCEL);
        Map<Status, Long> statusTimesMap = task1.getStatusTime();
        long waitingStatusStartTime = System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(3);
        long processedStatusStartTime = System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(1);

        statusTimesMap.put(Status.WAITING, waitingStatusStartTime);
        statusTimesMap.put(Status.PROCESSED, processedStatusStartTime);
        task1.getStatusStartTimesMap().put(task1.getStatus(), System.currentTimeMillis());
        double waitingStatusTime = (waitingStatusStartTime / MILLIS_TO_SECONDS_COEFFICIENT);
        double processedStatusTime = (processedStatusStartTime / MILLIS_TO_SECONDS_COEFFICIENT);
        double actualWaitingTime = taskRepository.getStatusTimeByTaskIdAndStatus(task1.getId(), Status.WAITING);
        double actualProcessedTime = taskRepository.getStatusTimeByTaskIdAndStatus(task1.getId(), Status.PROCESSED);
        double actualCancelTime = taskRepository.getStatusTimeByTaskIdAndStatus(task1.getId(), Status.CANCEL);
        assertEquals(waitingStatusTime, actualWaitingTime, 0.001);
        assertEquals(processedStatusTime, actualProcessedTime, 0.001);
        assertEquals(
                (System.currentTimeMillis() - task1.getStatusStartTimesMap().get(Status.CANCEL))
                        /MILLIS_TO_SECONDS_COEFFICIENT,
                actualCancelTime, 0.1);
    }

    @Test
    public void get_average_processing_time_for_closed_tasks_should_return_correct_average_time() {
        Task task1 = new Task();
        task1.setId(UniqueIdGenerator.generateUniqueId());
        task1.setStatus(Status.CLOSE);
        tasksMap.put(task1.getId(), task1);
        Task task2 = new Task();
        task2.setId(UniqueIdGenerator.generateUniqueId());
        task2.setStatus(Status.CLOSE);
        tasksMap.put(task2.getId(), task2);
        Task task3 = new Task();
        task3.setId(UniqueIdGenerator.generateUniqueId());
        task3.setStatus(Status.CANCEL);
        tasksMap.put(task3.getId(), task3);
        Task task4 = new Task();
        task4.setId(UniqueIdGenerator.generateUniqueId());
        task4.setStatus(Status.PROCESSED);
        tasksMap.put(task4.getId(), task4);

        Map<Status, Long> statusTimeMap1 = task1.getStatusTime();
        Map<Status, Long> statusTimeMap2 = task2.getStatusTime();
        Map<Status, Long> statusTimeMap3 = task3.getStatusTime();
        double totalProcessingTime = statusTimeMap1.get(Status.PROCESSED)
                + statusTimeMap2.get(Status.PROCESSED)
                + statusTimeMap3.get(Status.CANCEL);

        double expectedAverage = totalProcessingTime / 3.0;

        double actualAverage = taskRepository.getAverageProcessingTimeForClosedTasks();
        assertEquals(expectedAverage, actualAverage, 0.001);
    }


    @Test
    public void get_all_tasks_should_return_correct_tasks_list() {
        Task task1 = new Task();
        task1.setId(UniqueIdGenerator.generateUniqueId());
        task1.setStatus(Status.PROCESSED);
        tasksMap.put(task1.getId(), task1);
        Task task2 = new Task();
        task2.setId(UniqueIdGenerator.generateUniqueId());
        task2.setStatus(Status.WAITING);
        tasksMap.put(task2.getId(), task2);
        queue.add(task2);
        Task task3 = new Task();
        task3.setId(UniqueIdGenerator.generateUniqueId());
        task3.setStatus(Status.CANCEL);
        tasksMap.put(task3.getId(), task3);
        Task task4 = new Task();
        task4.setId(UniqueIdGenerator.generateUniqueId());
        task4.setStatus(Status.PROCESSED);
        tasksMap.put(task4.getId(), task4);
        Map<String, Task> allTasks = taskRepository.getAllTasks();
        assertTrue(allTasks.containsKey(task1.getId()));
        assertTrue(allTasks.containsKey(task2.getId()));
        assertTrue(allTasks.containsKey(task3.getId()));
        assertTrue(allTasks.containsKey(task4.getId()));
    }


}
