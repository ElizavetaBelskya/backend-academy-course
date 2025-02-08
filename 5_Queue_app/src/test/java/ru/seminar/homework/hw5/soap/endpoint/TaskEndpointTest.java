package ru.seminar.homework.hw5.soap.endpoint;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.webservices.server.WebServiceServerTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.test.server.MockWebServiceClient;
import org.springframework.xml.transform.StringSource;
import ru.seminar.homework.hw5.config.TaskRepositoryConfig;
import ru.seminar.homework.hw5.model.Status;
import ru.seminar.homework.hw5.model.Task;
import ru.seminar.homework.hw5.repository.impl.TaskRepositoryQueueImpl;
import ru.seminar.homework.hw5.soap.config.WebServiceConfig;
import ru.seminar.homework.hw5.soap.service.TaskSoapService;

import java.io.IOException;
import java.util.Map;
import java.util.Queue;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.ws.test.server.RequestCreators.withPayload;
import static org.springframework.ws.test.server.ResponseMatchers.*;


@WebServiceServerTest(TaskEndpoint.class)
@Import({TaskSoapService.class, TaskRepositoryQueueImpl.class, TaskRepositoryConfig.class, WebServiceConfig.class})
public class TaskEndpointTest {

    @Autowired
    private MockWebServiceClient mockClient;

    @Autowired
    private Map<String, Task> tasksMap;

    @Autowired
    private Queue<Task> taskQueue;

    @BeforeEach
    public void clear() {
        taskQueue.clear();
        tasksMap.clear();
    }

    @Test
    public void get_next_waiting_task_when_valid_request_then_return_task() throws IOException {
        Task task1 = new Task();
        task1.setId("123");
        task1.setStatus(Status.WAITING);
        taskQueue.add(task1);
        tasksMap.put(task1.getId(), task1);
        String requestPayload = "<tns:GetNextWaitingTaskRequest xmlns:tns=\"http://www.example.com/soap\"/>";
        StringSource request = new StringSource(requestPayload);

        mockClient.sendRequest(withPayload(request))
                .andExpect(noFault())
                .andExpect(validPayload(new ClassPathResource("xsd/soap.xsd")))
                .andExpect(xpath("/ns2:GetNextWaitingTaskResponse/ns2:taskDto/ns2:number",
                        ImmutableMap.of("ns2", "http://www.example.com/soap"))
                        .evaluatesTo("123"));
    }

    @Test
    public void update_task_status_when_valid_request_then_return_update_task_status_response() throws IOException {
        Task task1 = new Task();
        task1.setId("123");
        task1.setStatus(Status.WAITING);
        taskQueue.add(task1);
        tasksMap.put(task1.getId(), task1);

        StringSource requestPayload = new StringSource(
                "<tns:UpdateTaskStatusRequest xmlns:tns=\"http://www.example.com/soap\">" +
                        "<tns:number>123</tns:number>" +
                        "<tns:newStatus>NEW</tns:newStatus>" +
                        "</tns:UpdateTaskStatusRequest>"
        );

        mockClient.sendRequest(withPayload(requestPayload))
                .andExpect(noFault())
                .andExpect(validPayload(new ClassPathResource("xsd/soap.xsd")))
                .andExpect(xpath("/ns2:UpdateTaskStatusResponse/ns2:taskDto/ns2:number", ImmutableMap.of("ns2", "http://www.example.com/soap"))
                        .evaluatesTo("123"));

        assertEquals(Status.NEW, task1.getStatus());
        assertThat(taskQueue.size()).isEqualTo(0);
    }

    @Test
    public void update_task_status_when_incorrect_status_then_return_fault() throws IOException {
        Task task1 = new Task();
        task1.setId("123");
        task1.setStatus(Status.WAITING);
        taskQueue.add(task1);
        tasksMap.put(task1.getId(), task1);

        StringSource requestPayload = new StringSource(
                "<tns:UpdateTaskStatusRequest xmlns:tns=\"http://www.example.com/soap\">" +
                        "<tns:number>123</tns:number>" +
                        "<tns:newStatus>INVALID</tns:newStatus>" +
                        "</tns:UpdateTaskStatusRequest>"
        );

        mockClient.sendRequest(withPayload(requestPayload))
                .andExpect(serverOrReceiverFault());
    }



    @Test
    public void test_create_task_when_valid_request_then_return_create_task_response() throws IOException {
        StringSource requestPayload = new StringSource("<tns:CreateTaskRequest xmlns:tns=\"http://www.example.com/soap\"/>");
        mockClient.sendRequest(withPayload(requestPayload))
                .andExpect(noFault())
                .andExpect(validPayload(new ClassPathResource("xsd/soap.xsd")))
                .andExpect(xpath("/ns2:CreateTaskResponse/ns2:taskDto/ns2:status",
                        ImmutableMap.of("ns2", "http://www.example.com/soap"))
                        .evaluatesTo("NEW"));
        assertEquals(1, tasksMap.size());
    }

    @Test
    public void test_delete_task_when_valid_request_then_task_deleted() throws IOException {
        Task task1 = new Task();
        task1.setId("123");
        task1.setStatus(Status.WAITING);
        taskQueue.add(task1);
        tasksMap.put(task1.getId(), task1);

        StringSource requestPayload = new StringSource(
                "<tns:DeleteTaskRequest xmlns:tns=\"http://www.example.com/soap\">" +
                        "<tns:number>123</tns:number>" +
                        "</tns:DeleteTaskRequest>"
        );

        mockClient.sendRequest(withPayload(requestPayload))
                .andExpect(noFault())
                .andExpect(validPayload(new ClassPathResource("xsd/soap.xsd")));

        assertEquals(0, tasksMap.size());
        assertEquals(0, taskQueue.size());
    }

    @Test
    public void test_delete_task_when_request_not_found_then_task_deleted() throws IOException {
        StringSource requestPayload = new StringSource(
                "<tns:DeleteTaskRequest xmlns:tns=\"http://www.example.com/soap\">" +
                        "<tns:number>123</tns:number>" +
                        "</tns:DeleteTaskRequest>"
        );

        mockClient.sendRequest(withPayload(requestPayload))
                .andExpect(serverOrReceiverFault());
    }



}