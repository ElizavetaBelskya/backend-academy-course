package ru.seminar.homework.hw5.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.client.security.CallCredentialsHelper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.seminar.homework.hw5.grpc.Tasks.createTaskRequest;
import ru.seminar.homework.hw5.grpc.Tasks.createTaskResponse;
import ru.seminar.homework.hw5.model.Status;
import ru.seminar.homework.hw5.model.Task;
import ru.seminar.homework.hw5.util.UniqueIdGenerator;

import java.util.Map;
import java.util.Queue;

import static io.grpc.Status.Code.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.seminar.homework.hw5.model.Status.CANCEL;

@SpringBootTest(properties = {"grpc.server.address=0.0.0.0", "grpc.server.port=9091"})
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class TaskGrpcServiceImplTest {

    @Autowired
    private Map<String, Task> tasksMap;

    @Autowired
    private Queue<Task> taskQueue;

    private static final ManagedChannel channel = ManagedChannelBuilder
            .forAddress("0.0.0.0", 9091).usePlaintext().build();
    private TaskServiceGrpc.TaskServiceBlockingStub taskServiceBlockingStub;

    @BeforeEach
    public void setUp() {
        taskServiceBlockingStub = TaskServiceGrpc.newBlockingStub(channel)
                .withCallCredentials(CallCredentialsHelper.basicAuth("user", "password"));
        tasksMap.clear();
        taskQueue.clear();
    }

    @AfterAll
    public static void close() {
        channel.shutdown();
    }

    @Test
    public void post_task_should_add_new_task_with_new_status_and_return_created() {
        long size = tasksMap.size();
        createTaskResponse response = taskServiceBlockingStub.createTask(createTaskRequest.newBuilder().build());
        assertThat(response.getTaskDto()).matches(x -> "NEW".equals(x.getStatus()));
        assertEquals(size + 1, tasksMap.size());
    }

    @Test
    public void patch_task_status_should_return_unauthorized_when_user_is_anonymous() {
        taskServiceBlockingStub = TaskServiceGrpc.newBlockingStub(channel);
        Task task1 = new Task();
        task1.setId(UniqueIdGenerator.generateUniqueId());
        task1.setStatus(Status.PROCESSED);
        tasksMap.put(task1.getId(), task1);
        assertThatThrownBy(() -> taskServiceBlockingStub.updateTaskStatus(
                Tasks.updateTaskStatusRequest.newBuilder()
                        .setNumber(task1.getId())
                        .setNewStatus("CANCEL")
                        .build())).isInstanceOf(StatusRuntimeException.class)
                .matches(e -> UNAUTHENTICATED.equals(((StatusRuntimeException) e).getStatus().getCode()));;
    }


    @Test
    public void patch_task_status_should_change_status_and_return_accepted() {
        Task task1 = new Task();
        task1.setId(UniqueIdGenerator.generateUniqueId());
        task1.setStatus(Status.PROCESSED);
        tasksMap.put(task1.getId(), task1);
        Tasks.updateTaskStatusResponse response = taskServiceBlockingStub.updateTaskStatus(
                Tasks.updateTaskStatusRequest.newBuilder()
                        .setNumber(task1.getId())
                        .setNewStatus("CANCEL")
                        .build());
        assertThat(response.getTaskDto())
                .matches(x -> "CANCEL".equals(x.getStatus())
                && task1.getId().equals(x.getNumber()));
        assertEquals(CANCEL, task1.getStatus());
    }


    @Test
    public void patch_task_status_with_incorrect_status_should_return_bad_request()  {
        Task task1 = new Task();
        task1.setId(UniqueIdGenerator.generateUniqueId());
        task1.setStatus(Status.PROCESSED);
        tasksMap.put(task1.getId(), task1);
        assertThatThrownBy(() -> taskServiceBlockingStub.updateTaskStatus(
                Tasks.updateTaskStatusRequest.newBuilder()
                        .setNumber(task1.getId())
                        .setNewStatus("INVALID")
                        .build())).isInstanceOf(StatusRuntimeException.class)
                .matches(e -> INVALID_ARGUMENT.equals(((StatusRuntimeException) e).getStatus().getCode()));
    }

    @Test
    public void get_task_should_return_task_from_queue_and_return_ok()  {
        Task task1 = new Task();
        task1.setId(UniqueIdGenerator.generateUniqueId());
        task1.setStatus(Status.WAITING);
        tasksMap.put(task1.getId(), task1);
        taskQueue.add(task1);
        Tasks.getNextWaitingTaskResponse response = taskServiceBlockingStub
                .getNextWaitingTask(Tasks.getNextWaitingTaskRequest.newBuilder().build());
        assertThat(response.getTaskDto()).matches(x -> task1.getId().equals(x.getNumber())
                && "WAITING".equals(x.getStatus()));
    }

    @Test
    public void get_task_should_return_unauthorized_when_user_is_anonymous()  {
        taskServiceBlockingStub = TaskServiceGrpc.newBlockingStub(channel);
        assertThatThrownBy(() -> taskServiceBlockingStub
                .getNextWaitingTask(Tasks.getNextWaitingTaskRequest.newBuilder().build()))
                .isInstanceOf(StatusRuntimeException.class)
                .matches(e -> UNAUTHENTICATED.equals(((StatusRuntimeException) e).getStatus().getCode()));
    }


    @Test
    public void delete_task_should_delete_and_return_no_content() {
        Task task1 = new Task();
        task1.setId(UniqueIdGenerator.generateUniqueId());
        task1.setStatus(Status.WAITING);
        tasksMap.put(task1.getId(), task1);
        taskQueue.add(task1);
        Tasks.deleteTaskResponse response = taskServiceBlockingStub
                .deleteTask(Tasks.deleteTaskRequest.newBuilder().setNumber(task1.getId()).build());
        assertEquals(0, tasksMap.size());
        assertEquals(0, taskQueue.size());
    }


    @Test
    public void delete_task_should_return_not_found_when_id_is_not_found() {
        assertThatThrownBy(() -> taskServiceBlockingStub
                .deleteTask(Tasks.deleteTaskRequest.newBuilder()
                        .setNumber(UniqueIdGenerator.generateUniqueId()).build()))
                .isInstanceOf(StatusRuntimeException.class)
                .matches(e -> NOT_FOUND.equals(((StatusRuntimeException) e).getStatus().getCode()));
    }




}