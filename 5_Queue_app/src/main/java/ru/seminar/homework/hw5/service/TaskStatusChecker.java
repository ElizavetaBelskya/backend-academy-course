package ru.seminar.homework.hw5.service;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.seminar.homework.hw5.model.Status;
import ru.seminar.homework.hw5.model.Task;
import ru.seminar.homework.hw5.repository.TaskRepository;

import java.util.Map;

@Service
@NoArgsConstructor
public class TaskStatusChecker {

    private static final int CANCEL_PERIOD = 30*60*1000;

    private TaskRepository taskRepository;

    @Autowired
    public TaskStatusChecker(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Async
    @Scheduled(cron = "0 * * * * ?")
    public void checkTaskStatus() {
        Map<String, Task> tasks = taskRepository.getAllTasks();
        for (Task task : tasks.values()) {
            if (Status.NEW.equals(task.getStatus())) {
                long elapsedTime = System.currentTimeMillis() - task.getStatusTime().get(task.getStatus());
                if (elapsedTime > CANCEL_PERIOD) {
                    task.setStatus(Status.CANCEL);
                }
            }
        }
    }

}
