package ru.tinkoff.belskya.hw7.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.tinkoff.belskya.hw7.dto.student.NewStudentDto;
import ru.tinkoff.belskya.hw7.dto.student.StudentDto;
import ru.tinkoff.belskya.hw7.dto.student.UpdateStudentDto;

import java.util.List;

@RequestMapping("/students")
public interface StudentApi {

    @Operation(summary = "Create a new student")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Student created successfully",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = StudentDto.class))
                    }),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping
    ResponseEntity<StudentDto> createStudent(@RequestBody @Valid NewStudentDto student);

    @Operation(summary = "Get all students")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list",
                    content = {
                            @Content(mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = StudentDto.class)))
                    }),
            @ApiResponse(responseCode = "404", description = "No students found")
    })
    @GetMapping
    ResponseEntity<List<StudentDto>> getAllStudents();

    @Operation(summary = "Get a student by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved student",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = StudentDto.class))
                    }),
            @ApiResponse(responseCode = "404", description = "Student not found")
    })
    @GetMapping("/{id}")
    ResponseEntity<StudentDto> getStudentById(@PathVariable Long id);

    @Operation(summary = "Add student to courses")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Student added to courses successfully",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = StudentDto.class))
                    }),
            @ApiResponse(responseCode = "404", description = "Student or course not found")
    })
    @PatchMapping("/{studentId}")
    ResponseEntity<StudentDto> addStudentToCourses(@PathVariable Long studentId, @RequestBody List<Long> courseIds);

    @Operation(summary = "Courses are removed from student course list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "209", description = "Student with courses after removing",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = StudentDto.class))
                    }),
            @ApiResponse(responseCode = "404", description = "Student or course not found")
    })
    @DeleteMapping("/{studentId}")
    ResponseEntity<StudentDto> deleteCourseFromStudentCourses(@PathVariable Long studentId, @RequestBody List<Long> courseIds);

    @Operation(summary = "Update student information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Student updated successfully",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = StudentDto.class))
                    }),
            @ApiResponse(responseCode = "404", description = "Student not found"),
            @ApiResponse(responseCode = "409", description = "Version conflict"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PutMapping("/{studentId}")
    ResponseEntity<StudentDto> updateStudent(@PathVariable Long studentId, @RequestBody UpdateStudentDto updateStudentDto);

}
