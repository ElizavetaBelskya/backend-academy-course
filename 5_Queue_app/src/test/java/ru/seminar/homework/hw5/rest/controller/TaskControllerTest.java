package ru.seminar.homework.hw5.rest.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.seminar.homework.hw5.model.Status;
import ru.seminar.homework.hw5.model.Task;
import ru.seminar.homework.hw5.util.UniqueIdGenerator;

import java.util.Map;
import java.util.Queue;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.hasKey;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, properties = {"grpc.server.address=0.0.0.0",
        "grpc.server.port=9093"})
@AutoConfigureMockMvc
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class TaskControllerTest {


    @Autowired
    private Map<String, Task> tasksMap;

    @Autowired
    private Queue<Task> taskQueue;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        tasksMap.clear();
        taskQueue.clear();
    }


    @Test
    public void post_task_should_add_new_task_with_new_status_and_return_created() throws Exception {
        long size = tasksMap.size();
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/task"))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.status")
                        .value("NEW"));
        assertThat(tasksMap.size()).isEqualTo(size + 1);
    }

    @Test
    public void get_task_list_should_return_tasks_by_status_except_closed_and_return_ok() throws Exception {
        Task task1 = new Task();
        task1.setId(UniqueIdGenerator.generateUniqueId());
        task1.setStatus(Status.PROCESSED);
        Task task2 = new Task();
        task2.setId(UniqueIdGenerator.generateUniqueId());
        task2.setStatus(Status.WAITING);
        Task task3 = new Task();
        task3.setId(UniqueIdGenerator.generateUniqueId());
        task3.setStatus(Status.CANCEL);
        tasksMap.put(task1.getId(), task1);
        tasksMap.put(task2.getId(), task2);
        tasksMap.put(task3.getId(), task3);
        mockMvc.perform(get("/tasks"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.tasksByStatus.size()")
                        .value(2))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.tasksByStatus")
                        .value(hasKey("WAITING")))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.tasksByStatus")
                        .value(hasKey("PROCESSED")));

    }

    @Test
    public void patch_task_status_should_return_unauthorized_when_user_is_anonymous() throws Exception {
        Task task1 = new Task();
        task1.setId(UniqueIdGenerator.generateUniqueId());
        task1.setStatus(Status.PROCESSED);
        tasksMap.put(task1.getId(), task1);
        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/task/{number}", task1.getId()).param("newStatus", "CLOSE"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @WithMockUser(username = "user")
    @Test
    public void patch_task_status_should_change_status_and_return_accepted() throws Exception {
        Task task1 = new Task();
        task1.setId(UniqueIdGenerator.generateUniqueId());
        task1.setStatus(Status.PROCESSED);
        tasksMap.put(task1.getId(), task1);
        mockMvc.perform(patch("/task/{number}", task1.getId())
                        .param("newStatus", "CLOSE"))
                .andDo(print())
                .andExpect(status().isAccepted())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("CLOSE"));
    }

    @WithMockUser(username = "user")
    @Test
    public void patch_task_status_with_incorrect_status_should_return_bad_request() throws Exception {
        Task task1 = new Task();
        task1.setId(UniqueIdGenerator.generateUniqueId());
        task1.setStatus(Status.PROCESSED);
        tasksMap.put(task1.getId(), task1);
        mockMvc.perform(patch("/task/{number}", task1.getId())
                        .param("newStatus", "INVALID"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(username = "user")
    @Test
    public void get_task_should_return_task_from_queue_and_return_ok() throws Exception {
        Task task1 = new Task();
        task1.setId(UniqueIdGenerator.generateUniqueId());
        task1.setStatus(Status.WAITING);
        tasksMap.put(task1.getId(), task1);
        taskQueue.add(task1);
        mockMvc.perform(get("/task"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.number")
                        .value(task1.getId()));
    }

    @Test
    public void get_task_should_return_unauthorized_when_user_is_anonymous() throws Exception {
        mockMvc.perform(get("/task"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @WithMockUser(username = "user")
    @Test
    public void delete_task_should_delete_and_return_no_content() throws Exception {
        Task task1 = new Task();
        task1.setId(UniqueIdGenerator.generateUniqueId());
        task1.setStatus(Status.WAITING);
        tasksMap.put(task1.getId(), task1);
        taskQueue.add(task1);
        mockMvc.perform(delete("/task/{number}", task1.getId()))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertEquals(0, tasksMap.size());
        assertEquals(0, taskQueue.size());
    }

    @WithMockUser(username = "user")
    @Test
    public void delete_task_should_return_not_found_when_id_is_not_found() throws Exception {
        mockMvc.perform(delete("/task/{number}", UniqueIdGenerator.generateUniqueId()))
                .andDo(print())
                .andExpect(status().isNotFound());
    }


}