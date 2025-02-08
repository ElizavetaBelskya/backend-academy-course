package ru.seminar.homework.hw5.soap.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.seminar.homework.hw5.model.Status;
import ru.seminar.homework.hw5.model.Task;
import ru.seminar.homework.hw5.soap.soap_dto.TaskDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mapper
public interface TaskMapper {

    TaskMapper INSTANCE = Mappers.getMapper(TaskMapper.class);

    @Mapping(expression = "java(entity.getId())", target = "number")
    @Mapping(expression = "java(String.valueOf(entity.getStatus()))", target = "status")
    @Mapping(expression = "java(TaskMapper.timesToStatusTime(entity.getStatusTime()))", target = "times")
    TaskDto taskToTaskDto(Task entity);

    static List<TaskDto.Times> timesToStatusTime(Map<Status, Long> statusTimeMap) {
        List<TaskDto.Times> timeList = new ArrayList<>();
        for (Map.Entry<Status, Long> entry: statusTimeMap.entrySet()) {
            var times = new TaskDto.Times();
            times.setStatus(String.valueOf(entry.getKey()));
            times.setTime(entry.getValue().doubleValue() / 1000);
            timeList.add(times);
        }
        return timeList;
    }

}
