package ru.seminar.homework.hw5.grpc;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.seminar.homework.hw5.grpc.TaskServiceGrpc.TaskServiceImplBase;
import ru.seminar.homework.hw5.grpc.Tasks.*;
import ru.seminar.homework.hw5.model.Status;
import ru.seminar.homework.hw5.model.Task;
import ru.seminar.homework.hw5.repository.TaskRepository;

import javax.annotation.security.PermitAll;


@GrpcService
@RequiredArgsConstructor
public class TaskGrpcServiceImpl extends TaskServiceImplBase {


    //упустила слой сервисов, и не писала еще один mapper - в данном случае это смысла не имеет
    private final TaskRepository taskRepository;


    @Override
    public void getNextWaitingTask(getNextWaitingTaskRequest request, StreamObserver<getNextWaitingTaskResponse> responseObserver) {
        Task task = taskRepository.getNextWaitingTask();
        getNextWaitingTaskResponse response = getNextWaitingTaskResponse.newBuilder()
                .setTaskDto(TaskDto
                        .newBuilder()
                        .setNumber(task.getId())
                        .setStatus(String.valueOf(task.getStatus()))
                        .build())
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }


    @Override
    public void updateTaskStatus(updateTaskStatusRequest request, StreamObserver<updateTaskStatusResponse> responseObserver) {
        Task task = taskRepository.updateTaskStatusById(request.getNumber(), Status.valueOf(request.getNewStatus()));
        updateTaskStatusResponse response = updateTaskStatusResponse.newBuilder()
                .setTaskDto(TaskDto
                        .newBuilder()
                        .setNumber(task.getId())
                        .setStatus(String.valueOf(task.getStatus()))
                        .build())
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }


    @Override
    public void deleteTask(deleteTaskRequest request, StreamObserver<deleteTaskResponse> responseObserver) {
        taskRepository.deleteTaskById(request.getNumber());
        deleteTaskResponse response = deleteTaskResponse.newBuilder().build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @PermitAll
    @Override
    public void createTask(createTaskRequest request, StreamObserver<createTaskResponse> responseObserver) {
        Task task = taskRepository.addNewTask();
        createTaskResponse response = createTaskResponse.newBuilder()
                .setTaskDto(TaskDto
                        .newBuilder()
                        .setNumber(task.getId())
                        .setStatus(String.valueOf(task.getStatus()))
                        .build())
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }


}
