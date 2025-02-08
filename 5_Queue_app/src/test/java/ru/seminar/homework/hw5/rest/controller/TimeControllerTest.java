package ru.seminar.homework.hw5.rest.controller;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.seminar.homework.hw5.dto.TaskStatusTimeMap;
import ru.seminar.homework.hw5.exception.TaskNotFoundException;
import ru.seminar.homework.hw5.rest.service.TaskService;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, properties = {"grpc.server.address=0.0.0.0",
        "grpc.server.port=9094"})
@AutoConfigureMockMvc
public class TimeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @Test
    public void getProcessingTimeForTaskByStatus_when_called_with_valid_task_number_and_status_should_return_correct_processing_time() throws Exception {
        String taskNumber = "123";
        String status = "NEW";
        Double processingTime = 15.0;
        when(taskService.getProcessingTimeByTaskAndStatus(taskNumber, status)).thenReturn(processingTime);
        mockMvc.perform(get("/times/{number}/{status}", taskNumber, status)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(processingTime.toString()));
    }

    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @Test
    public void getProcessingTimeForTaskByStatus_with_invalid_task_number_should_return_not_found() throws Exception {
        String taskNumber = "123478";
        String status = "NEW";
        when(taskService.getProcessingTimeByTaskAndStatus(taskNumber, status)).thenThrow(new TaskNotFoundException(taskNumber));
        mockMvc.perform(get("/times/{number}/{status}", taskNumber, status)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }


    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @Test
    public void getAverageProcessingTime_should_return_average_processing_time() throws Exception {
        Double averageProcessingTime = 10.0;
        when(taskService.calculateAverageProcessingTimeForAllTasks()).thenReturn(averageProcessingTime);

        mockMvc.perform(get("/times"))
                .andExpect(status().isOk())
                .andExpect(content().string(averageProcessingTime.toString()));
    }


    @Test
    public void getAverageProcessingTime_should_return_unauthorized_when_user_is_anonymous() throws Exception {
        Double averageProcessingTime = 10.0;
        when(taskService.calculateAverageProcessingTimeForAllTasks()).thenReturn(averageProcessingTime);

        mockMvc.perform(get("/times")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @Test
    public void getProcessingTimeMapForTask_should_return_correct_result_when_data_is_correct() throws Exception {
        String taskNumber = "123";
        TaskStatusTimeMap taskStatusTimeMap = new TaskStatusTimeMap();
        Map<String, Double> times = new HashMap<>();
        times.put("NEW", 10.0);
        times.put("WAITING", 20.0);
        taskStatusTimeMap.setTimes(times);
        when(taskService.getStatusMapForTask(taskNumber)).thenReturn(taskStatusTimeMap);
        mockMvc.perform(get("/times/{number}", taskNumber)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"times\":{\"NEW\":10.0,\"WAITING\":20.0}}"));
    }

    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @Test
    public void getProcessingTimeMapForTask_should_return_not_found_when_called_with_invalid_task_number() throws Exception {
        String taskNumber = "1234";
        when(taskService.getStatusMapForTask(taskNumber)).thenThrow(new TaskNotFoundException(taskNumber));
        mockMvc.perform(get("/times/{number}", taskNumber)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getProcessingTimeMapForTask_should_return_unauthorized_when_user_is_anonymous() throws Exception {
        String taskNumber = "1234";
        mockMvc.perform(get("/times/{number}", taskNumber)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }


}