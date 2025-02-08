package ru.seminar.homework.hw5.soap.endpoint;

import jakarta.xml.bind.JAXBElement;
import lombok.RequiredArgsConstructor;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import ru.seminar.homework.hw5.soap.service.TaskSoapService;
import ru.seminar.homework.hw5.soap.soap_dto.*;

@Endpoint
@RequiredArgsConstructor
public class TaskEndpoint {

    private static final String NAMESPACE_URI = "http://www.example.com/soap";

    private final TaskSoapService taskSoapService;
    private final ObjectFactory objectFactory = new ObjectFactory();

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "GetNextWaitingTaskRequest")
    @ResponsePayload
    public JAXBElement<GetNextWaitingTaskResponse> getNextWaitingTask(@RequestPayload JAXBElement<GetNextWaitingTaskRequest> request) {
        var response = new GetNextWaitingTaskResponse();
        response.setTaskDto(taskSoapService.getNextWaitingTask());
        return objectFactory.createGetNextWaitingTaskResponse(response);
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "UpdateTaskStatusRequest")
    @ResponsePayload
    public JAXBElement<UpdateTaskStatusResponse> updateTaskStatus(@RequestPayload JAXBElement<UpdateTaskStatusRequest> request) {
        var response = new UpdateTaskStatusResponse();
        response.setTaskDto(taskSoapService.updateTaskStatus(request.getValue().getNumber(), request.getValue().getNewStatus()));
        return objectFactory.createUpdateTaskStatusResponse(response);
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "CreateTaskRequest")
    @ResponsePayload
    public JAXBElement<CreateTaskResponse> createTask(@RequestPayload JAXBElement<CreateTaskRequest> request) {
        var response = new CreateTaskResponse();
        response.setTaskDto(taskSoapService.enqueueTask());
        return objectFactory.createCreateTaskResponse(response);
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "DeleteTaskRequest")
    @ResponsePayload
    public JAXBElement<DeleteTaskResponse> deleteTask(@RequestPayload JAXBElement<DeleteTaskRequest> request) {
        taskSoapService.deleteTask(request.getValue().getNumber());
        return objectFactory.createDeleteTaskResponse(new DeleteTaskResponse());
    }


}
