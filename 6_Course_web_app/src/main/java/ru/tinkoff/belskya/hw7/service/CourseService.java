package ru.tinkoff.belskya.hw7.service;

import ru.tinkoff.belskya.hw7.dto.course.CourseDto;
import ru.tinkoff.belskya.hw7.dto.course.NewCourseDto;
import ru.tinkoff.belskya.hw7.dto.course.UpdateCourseDto;

import java.util.List;

public interface CourseService {

    CourseDto createCourse(NewCourseDto courseDto);

    List<CourseDto> getAllCourses();

    CourseDto getCourseById(Long id);

    CourseDto updateCourse(Long id, UpdateCourseDto updateCourseDto);

}