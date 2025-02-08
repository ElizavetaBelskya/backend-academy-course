package ru.tinkoff.belskya.hw7.controller.api;

import io.swagger.v3.oas.annotations.Operation;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.tinkoff.belskya.hw7.dto.course.CourseDto;
import ru.tinkoff.belskya.hw7.dto.course.NewCourseDto;
import ru.tinkoff.belskya.hw7.dto.course.UpdateCourseDto;

import java.util.List;


@RequestMapping("/courses")
public interface CourseApi {

    @Operation(summary = "Create a new course")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Course created successfully",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = CourseDto.class))
                    }),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping
    ResponseEntity<CourseDto> createCourse(@RequestBody @Valid NewCourseDto courseDto);


    @Operation(summary = "Get all courses")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list",
                    content = {
                            @Content(mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = CourseDto.class)))
                    }),
            @ApiResponse(responseCode = "404", description = "No courses found")
    })
    @GetMapping
    ResponseEntity<List<CourseDto>> getAllCourses();

    @Operation(summary = "Get a course by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved course",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = CourseDto.class))
                    }),
            @ApiResponse(responseCode = "404", description = "Course not found")
    })
    @GetMapping("/{id}")
    ResponseEntity<CourseDto> getCourseById(@PathVariable Long id);

    @Operation(summary = "Update course information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Course updated successfully",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = CourseDto.class))
                    }),
            @ApiResponse(responseCode = "404", description = "Course not found"),
            @ApiResponse(responseCode = "409", description = "Version conflict")
    })
    @PutMapping("/{id}")
    ResponseEntity<CourseDto> updateCourse(@PathVariable Long id, @RequestBody UpdateCourseDto updateCourseDto);

}
