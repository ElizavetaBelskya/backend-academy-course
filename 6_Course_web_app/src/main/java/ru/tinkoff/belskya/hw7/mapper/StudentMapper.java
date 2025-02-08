package ru.tinkoff.belskya.hw7.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.tinkoff.belskya.hw7.dto.student.NewStudentDto;
import ru.tinkoff.belskya.hw7.dto.student.StudentDto;
import ru.tinkoff.belskya.hw7.dto.student.UpdateStudentDto;
import ru.tinkoff.belskya.hw7.model.Student;


@Mapper(componentModel = "spring")
public interface StudentMapper {

    StudentMapper INSTANCE = Mappers.getMapper(StudentMapper.class);

    @Mapping(expression = "java(entity.getId())", target = "id")
    @Mapping(expression = "java(entity.getName())", target = "name")
    @Mapping(expression = "java(entity.getCourses().stream().map(x -> x.getId()).toList())", target = "courseIds")
    @Mapping(expression = "java(entity.getVersion())", target = "version")
    StudentDto studentToStudentDto(Student entity);


    @Mapping(expression = "java(studentDto.getName())", target = "name")
    Student studentDtoToStudent(NewStudentDto studentDto);

    @Mapping(expression = "java(studentDto.getName())", target = "name")
    @Mapping(expression = "java(studentDto.getVersion())", target = "version")
    Student studentDtoToStudent(UpdateStudentDto studentDto);

}
