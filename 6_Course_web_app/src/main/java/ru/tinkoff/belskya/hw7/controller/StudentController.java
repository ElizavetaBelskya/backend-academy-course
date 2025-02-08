package ru.tinkoff.belskya.hw7.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.tinkoff.belskya.hw7.controller.api.StudentApi;
import ru.tinkoff.belskya.hw7.dto.student.NewStudentDto;
import ru.tinkoff.belskya.hw7.dto.student.StudentDto;
import ru.tinkoff.belskya.hw7.dto.student.UpdateStudentDto;
import ru.tinkoff.belskya.hw7.service.impl.StudentServiceImpl;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class StudentController implements StudentApi {

    private final StudentServiceImpl studentService;

    public ResponseEntity<StudentDto> createStudent(NewStudentDto student) {
        return ResponseEntity.status(HttpStatus.CREATED).body(studentService.createStudent(student));
    }

    public ResponseEntity<List<StudentDto>> getAllStudents() {
        return ResponseEntity.ok(studentService.getAllStudents());
    }

    public ResponseEntity<StudentDto> getStudentById(Long id) {
        return ResponseEntity.ok(studentService.getStudentById(id));
    }

    public ResponseEntity<StudentDto> deleteCourseFromStudentCourses(
            Long studentId,
            List<Long> courseIds
    ) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(studentService.deleteCourseFromStudentCourses(studentId, courseIds));
    }

    public ResponseEntity<StudentDto> addStudentToCourses(
           Long studentId,
           List<Long> courseIds
    ) {
        return ResponseEntity.accepted().body(studentService.addStudentToCourses(studentId, courseIds));
    }

    public ResponseEntity<StudentDto> updateStudent(Long studentId, UpdateStudentDto updateStudentDto) {
        return ResponseEntity.accepted().body(studentService.updateStudent(studentId, updateStudentDto));
    }

}
