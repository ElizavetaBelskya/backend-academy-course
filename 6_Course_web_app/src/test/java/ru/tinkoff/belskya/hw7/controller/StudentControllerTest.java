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
import ru.tinkoff.belskya.hw7.dto.student.NewStudentDto;
import ru.tinkoff.belskya.hw7.dto.student.StudentDto;
import ru.tinkoff.belskya.hw7.dto.student.UpdateStudentDto;
import ru.tinkoff.belskya.hw7.exception.EducationPlatformNotFoundException;
import ru.tinkoff.belskya.hw7.exception.EducationPlatformOptimisticLockingException;
import ru.tinkoff.belskya.hw7.exception.ExceptionMessages;
import ru.tinkoff.belskya.hw7.model.Course;
import ru.tinkoff.belskya.hw7.model.Student;
import ru.tinkoff.belskya.hw7.service.impl.StudentServiceImpl;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentServiceImpl studentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void createStudent_should_create_valid_student_then_return_created_student() throws Exception {
        NewStudentDto newStudentDto = new NewStudentDto("John Doe");
        StudentDto addedStudentDto = StudentDto.builder().id(1L).name("John Doe").build();
        when(studentService.createStudent(newStudentDto)).thenReturn(addedStudentDto);
        mockMvc.perform(post("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newStudentDto)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(addedStudentDto)));
    }

    @Test
    public void createStudent_when_invalid_json_payload_should_return_bad_request() throws Exception {
        String invalidJsonPayload = "{ \"invalid\": \"payload\" }";
        mockMvc.perform(post("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJsonPayload))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createStudent_when_incorrect_name_payload_should_return_bad_request_and_ValidationErrorDto() throws Exception {
        String jsonWithIncorrectName = "{ \"name\": \"*pad???№!\" }";
        mockMvc.perform(post("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonWithIncorrectName))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].fieldName").value("name"));
    }

    @Test
    public void getAllStudents_should_return_students() throws Exception {
        StudentDto studentDto1 = StudentDto.builder().id(1L).name("John Doe").build();
        StudentDto studentDto2 = StudentDto.builder().id(2L).name("Jane Doe").build();
        List<StudentDto> students = Arrays.asList(studentDto1, studentDto2);
        when(studentService.getAllStudents()).thenReturn(students);
        mockMvc.perform(get("/students")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(students)));
    }

    @Test
    public void getAllStudents_should_return_students_when_list_is_empty() throws Exception {
        when(studentService.getAllStudents()).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/students")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Collections.emptyList())));
    }

    @Test
    public void getStudentById_should_return_correct_student() throws Exception {
        Long id = 1L;
        StudentDto studentDto1 = StudentDto.builder().id(id).name("John Doe").build();
        when(studentService.getStudentById(id)).thenReturn(studentDto1);
        mockMvc.perform(get("/students/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(studentDto1)));
    }

    @Test
    public void getStudentById_should_return_not_found_when_id_does_not_exist() throws Exception {
        Long id = 11L;
        when(studentService.getStudentById(id)).thenThrow(
                new EducationPlatformNotFoundException(ExceptionMessages.STUDENT_NOT_FOUND, id));
        mockMvc.perform(get("/students/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("404"));
    }

    @Test
    public void getStudentById_should_return_bad_request_when_id_is_incorrect() throws Exception {
        String id = "incorrect";
        mockMvc.perform(get("/students/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("400"));
    }

    @Test
    public void addStudentToCourses_should_return_accepted_and_updated_student() throws Exception {
        Long studentId = 1L;
        List<Long> courseIds = Arrays.asList(2L, 3L);
        StudentDto studentDto = StudentDto.builder()
                .id(studentId)
                .name("NAME")
                .courseIds(courseIds)
                .build();
        when(studentService.addStudentToCourses(studentId, courseIds)).thenReturn(studentDto);
        mockMvc.perform(patch("/students/{studentId}", studentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseIds)))
                .andExpect(status().isAccepted())
                .andExpect(content().json(objectMapper.writeValueAsString(studentDto)));
    }

    @Test
    public void addStudentToCourses_should_return_not_found_when_course_id_does_not_exist() throws Exception {
        Long studentId = 1L;
        List<Long> courseIds = List.of(2L);
        when(studentService.addStudentToCourses(studentId, courseIds))
                .thenThrow(new EducationPlatformNotFoundException(ExceptionMessages.COURSE_NOT_FOUND, 2L));
        mockMvc.perform(patch("/students/{studentId}", studentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseIds)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void addStudentToCourses_should_return_not_found_when_student_id_does_not_exist() throws Exception {
        Long studentId = 1L;
        List<Long> courseIds = List.of(2L);
        when(studentService.addStudentToCourses(studentId, courseIds))
                .thenThrow(new EducationPlatformNotFoundException(ExceptionMessages.STUDENT_NOT_FOUND, studentId));
        mockMvc.perform(patch("/students/{studentId}", studentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseIds)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void addStudentToCourses_should_return_bad_request_when_data_types_are_incorrect() throws Exception {
        String studentId = "1L";
        List<String> courseIds = List.of("2L");
        mockMvc.perform(patch("/students/{studentId}", studentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseIds)))
                .andExpect(status().isBadRequest());
    }


    @Test
    void updateStudent_when_correct_version_then_return_accepted() throws Exception {
        Long studentId = 1L;
        UpdateStudentDto updateStudentDto = new UpdateStudentDto();
        updateStudentDto.setName("New Name");
        updateStudentDto.setVersion(1);

        StudentDto updatedStudentDto = new StudentDto();
        updatedStudentDto.setName("New Name");
        updatedStudentDto.setVersion(2);

        when(studentService.updateStudent(studentId, updateStudentDto)).thenReturn(updatedStudentDto);

        mockMvc.perform(put("/students/{studentId}", studentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateStudentDto)))
                .andExpect(status().isAccepted())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value(updatedStudentDto.getName()))
                .andExpect(jsonPath("$.version").value(updatedStudentDto.getVersion()));
    }

    @Test
    void updateStudent_when_student_not_found_then_return_not_found() throws Exception {
        Long studentId = 1L;
        UpdateStudentDto updateStudentDto = new UpdateStudentDto();
        updateStudentDto.setName("New Name");
        updateStudentDto.setVersion(1);

        when(studentService.updateStudent(studentId, updateStudentDto))
                .thenThrow(new EducationPlatformNotFoundException(ExceptionMessages.STUDENT_NOT_FOUND, studentId));

        mockMvc.perform(put("/students/{studentId}", studentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateStudentDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateStudent_when_student_version_is_old_then_return_conflict() throws Exception {
        Long studentId = 1L;
        UpdateStudentDto updateStudentDto = new UpdateStudentDto();
        updateStudentDto.setName("New Name");
        updateStudentDto.setVersion(1);

        when(studentService.updateStudent(studentId, updateStudentDto))
                .thenThrow(new EducationPlatformOptimisticLockingException(ExceptionMessages.STUDENT_LOCK_CONFLICT, studentId));

        mockMvc.perform(put("/students/{studentId}", studentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateStudentDto)))
                .andExpect(status().isConflict());
    }

    @Test
    public void deleteCourseFromStudentCourses_when_courses_exist_then_courses_are_removed_successfully() throws Exception {
        Long studentId = 1L;
        StudentDto studentDto = new StudentDto();
        studentDto.setId(studentId);
        studentDto.setName("Name");
        studentDto.setCourseIds(new ArrayList<>());
        studentDto.setVersion(1);
        List<Long> courseIds = List.of(1L, 2L);
        when(studentService.deleteCourseFromStudentCourses(studentId, courseIds)).thenReturn(studentDto);
        mockMvc.perform(delete("/students/{studentId}", studentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseIds)))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.name").value(studentDto.getName()))
                .andExpect(jsonPath("$.id").value(studentDto.getId()))
                .andExpect(jsonPath("$.courseIds").isEmpty());
    }

    @Test
    public void deleteCourseFromStudentCourses_when_courses_do_not_exist_then_throw_exception() throws Exception {
        Long studentId = 1L;
        List<Long> courseIds = List.of(1L, 2L);
        when(studentService.deleteCourseFromStudentCourses(studentId,courseIds))
                .thenThrow(new EducationPlatformNotFoundException(ExceptionMessages.STUDENT_LOCK_CONFLICT, studentId));
        mockMvc.perform(delete("/students/{studentId}", studentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseIds)))
                .andExpect(status().isNotFound());
    }



}