package ru.tinkoff.belskya.hw7.service;

import ru.tinkoff.belskya.hw7.dto.student.NewStudentDto;
import ru.tinkoff.belskya.hw7.dto.student.StudentDto;
import ru.tinkoff.belskya.hw7.dto.student.UpdateStudentDto;

import java.util.List;

public interface StudentService {

    StudentDto createStudent(NewStudentDto studentDto);

    List<StudentDto> getAllStudents();
    StudentDto getStudentById(Long id);

    StudentDto addStudentToCourses(Long studentId, List<Long> courseIds);

    StudentDto updateStudent(Long studentId, UpdateStudentDto updateStudentDto);

    StudentDto deleteCourseFromStudentCourses(Long studentId, List<Long> courseIds);
}
