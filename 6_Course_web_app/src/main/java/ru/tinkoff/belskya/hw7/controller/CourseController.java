package ru.tinkoff.belskya.hw7.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.tinkoff.belskya.hw7.controller.api.CourseApi;
import ru.tinkoff.belskya.hw7.dto.course.CourseDto;
import ru.tinkoff.belskya.hw7.dto.course.NewCourseDto;
import ru.tinkoff.belskya.hw7.dto.course.UpdateCourseDto;
import ru.tinkoff.belskya.hw7.service.impl.CourseServiceImpl;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CourseController implements CourseApi {

    private final CourseServiceImpl courseService;
    public ResponseEntity<CourseDto> createCourse(@RequestBody @Valid NewCourseDto courseDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(courseService.createCourse(courseDto));
    }

    public ResponseEntity<List<CourseDto>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }


    public ResponseEntity<CourseDto> getCourseById(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getCourseById(id));
    }

    public ResponseEntity<CourseDto> updateCourse(@PathVariable Long id, @RequestBody UpdateCourseDto updateCourseDto) {
        return ResponseEntity.accepted().body(courseService.updateCourse(id, updateCourseDto));
    }


}