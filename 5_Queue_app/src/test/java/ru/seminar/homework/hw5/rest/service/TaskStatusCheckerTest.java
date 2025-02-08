package ru.seminar.homework.hw5.rest.service;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import ru.seminar.homework.hw5.model.Status;
import ru.seminar.homework.hw5.model.Task;
import ru.seminar.homework.hw5.service.TaskStatusChecker;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static ru.seminar.homework.hw5.model.Status.CANCEL;
import static ru.seminar.homework.hw5.model.Status.NEW;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK,  properties = {"grpc.server.address=0.0.0.0",
        "grpc.server.port=9097"})
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class TaskStatusCheckerTest {

    @SpyBean
    private TaskStatusChecker taskStatusChecker;

    @Autowired
    private Map<String, Task> tasksMap;
    private Task task1;

    @BeforeEach
    public void setUp() {
        task1 = new Task();
        task1.setStatus(Status.NEW);
        task1.getStatusStartTimesMap().put(Status.NEW, System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(30));
        Task task2 = new Task("2", Status.WAITING, new HashMap<>(), new HashMap<>());
        tasksMap.put("1", task1);
        tasksMap.put("2", task2);
    }

    @Test
    public void test_check_task_status_when_tasks_with_different_statuses_then_statuses_updated() {
        Awaitility.await().atMost(1, TimeUnit.MINUTES).untilAsserted(() ->
                verify(taskStatusChecker, Mockito.atLeastOnce()).checkTaskStatus()
        );
        assertEquals(CANCEL, task1.getStatus());
        assertEquals(0, tasksMap.values().stream().filter(x -> NEW.equals(x.getStatus())).toList().size());
    }


}