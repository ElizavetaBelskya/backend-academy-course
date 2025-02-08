package ru.tinkoff.belskya.hw7.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.tinkoff.belskya.hw7.dto.course.CourseDto;
import ru.tinkoff.belskya.hw7.dto.course.NewCourseDto;
import ru.tinkoff.belskya.hw7.dto.course.UpdateCourseDto;
import ru.tinkoff.belskya.hw7.model.Course;


@Mapper(componentModel = "spring")
public interface CourseMapper {
    CourseMapper INSTANCE = Mappers.getMapper(CourseMapper.class);

    @Mapping(expression = "java(entity.getId())", target = "id")
    @Mapping(expression = "java(entity.getDescription())", target = "description")
    @Mapping(expression = "java(entity.getTitle())", target = "title")
    @Mapping(expression = "java(entity.getStudents().stream().map(x -> x.getId()).toList())", target = "studentIds")
    @Mapping(expression = "java(entity.getVersion())", target = "version")
    CourseDto courseToCourseDto(Course entity);

    @Mapping(expression = "java(dto.getTitle())", target = "title")
    @Mapping(expression = "java(dto.getDescription())", target = "description")
    Course courseDtoToCourse(NewCourseDto dto);

    @Mapping(expression = "java(dto.getTitle())", target = "title")
    @Mapping(expression = "java(dto.getDescription())", target = "description")
    @Mapping(expression = "java(dto.getVersion())", target = "version")
    Course updateCourseDtoToCourse(UpdateCourseDto dto);

}
