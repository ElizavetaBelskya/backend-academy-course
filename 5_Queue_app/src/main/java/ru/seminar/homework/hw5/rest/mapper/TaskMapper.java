package ru.seminar.homework.hw5.rest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.seminar.homework.hw5.dto.TaskDto;
import ru.seminar.homework.hw5.model.Status;
import ru.seminar.homework.hw5.model.Task;

import java.util.HashMap;
import java.util.Map;

@Mapper
public interface TaskMapper {

    TaskMapper INSTANCE = Mappers.getMapper(TaskMapper.class);

    @Mapping(expression = "java(entity.getId())", target = "number")
    @Mapping(expression = "java(String.valueOf(entity.getStatus()))", target = "status")
    @Mapping(expression = "java(TaskMapper.timesToStatusTime(entity.getStatusTime()))", target = "times")
    TaskDto taskToTaskDto(Task entity);

    static Map<String, Double> timesToStatusTime(Map<Status, Long> statusTimeMap) {
        Map<String, Double> timeMap = new HashMap<>();
        for (Map.Entry<Status, Long> entry: statusTimeMap.entrySet()) {
            timeMap.put(String.valueOf(entry.getKey()), entry.getValue().doubleValue() / 1000);
        }
        return timeMap;
    }

}
