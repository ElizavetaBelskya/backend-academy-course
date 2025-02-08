package ru.tinkoff.belskya.hw7.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.tinkoff.belskya.hw7.dto.course.CourseDto;
import ru.tinkoff.belskya.hw7.dto.course.NewCourseDto;
import ru.tinkoff.belskya.hw7.dto.course.UpdateCourseDto;
import ru.tinkoff.belskya.hw7.exception.AlreadyExistsException;
import ru.tinkoff.belskya.hw7.exception.EducationPlatformNotFoundException;
import ru.tinkoff.belskya.hw7.exception.EducationPlatformOptimisticLockingException;
import ru.tinkoff.belskya.hw7.exception.ExceptionMessages;
import ru.tinkoff.belskya.hw7.service.impl.CourseServiceImpl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CourseController.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CourseServiceImpl courseService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void createCourse_should_return_created_and_new_course() throws Exception {
        NewCourseDto newCourseDto = new NewCourseDto("New course", "Description");
        CourseDto createdCourse = CourseDto.builder().id(1L).title("New course").description("Description").build();
        when(courseService.createCourse(newCourseDto)).thenReturn(createdCourse);
        mockMvc.perform(post("/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCourseDto)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(createdCourse)));
    }

    @Test
    public void createCourse_should_return_conflict_when_name_exists() throws Exception {
        NewCourseDto newCourseDto = new NewCourseDto("New course", "Description");
        when(courseService.createCourse(newCourseDto)).thenThrow(new AlreadyExistsException(ExceptionMessages.COURSE_ALREADY_EXISTS, newCourseDto.getTitle()));
        mockMvc.perform(post("/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCourseDto)))
                .andExpect(status().isConflict());
    }

    @Test
    public void createCourse_should_return_bad_request_when_course_data_is_incorrect() throws Exception {
        NewCourseDto newCourseDto = new NewCourseDto("23w4sedrtcfgh&^$", "23w4sedrtcfgh&^$");
        mockMvc.perform(post("/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCourseDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].fieldName").value("title"));
    }

    @Test
    public void getAllCourses_should_return_all_courses() throws Exception {
        List<CourseDto> courseList = Arrays.asList(
                CourseDto.builder().id(1L).title("Course 1").build(),
                CourseDto.builder().id(2L).title("Course 2").build()
        );
        when(courseService.getAllCourses()).thenReturn(courseList);
        mockMvc.perform(get("/courses"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(courseList)));
    }

    @Test
    public void getAllCourses_should_return_ok_when_list_is_empty() throws Exception {
        when(courseService.getAllCourses()).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/courses"))
                .andExpect(status().isOk());
    }


    @Test
    public void getCourseById_should_return_course_when_id_exists() throws Exception {
        Long courseId = 1L;
        CourseDto courseDto =  CourseDto.builder().id(1L).title("Course 1").build();
        when(courseService.getCourseById(courseId)).thenReturn(courseDto);
        mockMvc.perform(get("/courses/{id}", courseId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(courseDto)));
    }


    @Test
    public void getCourseById_should_return_not_found_when_id_does_not_exist() throws Exception {
        Long courseId = 1L;
        when(courseService.getCourseById(courseId)).thenThrow(new EducationPlatformNotFoundException(ExceptionMessages.COURSE_NOT_FOUND, courseId));
        mockMvc.perform(get("/courses/{id}", courseId))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateCourse_when_data_is_correct_then_return_CourseDto() throws Exception {
        Long courseId = 1L;
        CourseDto courseDto = new CourseDto();
        courseDto.setId(courseId);
        courseDto.setVersion(2);
        courseDto.setTitle("Updated Course");

        UpdateCourseDto updateCourseDto = new UpdateCourseDto();
        updateCourseDto.setTitle("Updated Course");
        updateCourseDto.setVersion(1);

        when(courseService.updateCourse(courseId, updateCourseDto)).thenReturn(courseDto);

        mockMvc.perform(put("/courses/{id}", courseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateCourseDto)))
                .andExpect(status().isAccepted())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(courseDto.getId()))
                .andExpect(jsonPath("$.version").value(courseDto.getVersion()))
                .andExpect(jsonPath("$.title").value(courseDto.getTitle()));
    }

    @Test
    void updateCourse_when_id_is_not_found_then_return_not_found() throws Exception {
        Long courseId = 1L;
        UpdateCourseDto updateCourseDto = new UpdateCourseDto();
        updateCourseDto.setTitle("Updated Course");

        when(courseService.updateCourse(courseId, updateCourseDto))
                .thenThrow(new EducationPlatformNotFoundException("Course not found"));

        mockMvc.perform(put("/courses/{id}", courseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateCourseDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateCourse_when_optimistic_locking_conflict_then_return_conflict() throws Exception {
        Long courseId = 1L;
        UpdateCourseDto updateCourseDto = new UpdateCourseDto();
        updateCourseDto.setTitle("Updated Course");
        updateCourseDto.setVersion(1);

        when(courseService.updateCourse(courseId, updateCourseDto))
                .thenThrow(new EducationPlatformOptimisticLockingException(ExceptionMessages.COURSE_LOCK_CONFLICT, courseId));

        mockMvc.perform(put("/courses/{id}", courseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateCourseDto)))
                .andExpect(status().isConflict());
    }

}