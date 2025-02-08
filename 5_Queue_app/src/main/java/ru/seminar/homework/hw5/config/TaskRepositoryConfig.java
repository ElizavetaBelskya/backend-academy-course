package ru.seminar.homework.hw5.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.seminar.homework.hw5.model.Task;
import ru.seminar.homework.hw5.rest.mapper.TaskMapper;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Configuration
public class TaskRepositoryConfig {


    @Bean
    public Queue<Task> queue() {
        return new ConcurrentLinkedQueue<>();
    }

    @Bean
    public Map<String, Task> tasksMap() {
        return new ConcurrentHashMap<>();
    }

    @Bean
    public TaskMapper mapper() {
        return TaskMapper.INSTANCE;
    }

}
