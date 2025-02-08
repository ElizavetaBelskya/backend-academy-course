package ru.tinkoff.belskya.hw7.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import ru.tinkoff.belskya.hw7.dto.student.NewStudentDto;
import ru.tinkoff.belskya.hw7.dto.student.UpdateStudentDto;
import ru.tinkoff.belskya.hw7.model.Course;
import ru.tinkoff.belskya.hw7.model.Student;
import ru.tinkoff.belskya.hw7.repository.CourseRepository;
import ru.tinkoff.belskya.hw7.repository.StudentRepository;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ContextConfiguration(initializers = StudentControllerTestWithTestContainer.DataSourceInitializer.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class StudentControllerTestWithTestContainer {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Container
    private static final PostgreSQLContainer<?> database = new PostgreSQLContainer<>("postgres:12.9-alpine");

    public static class DataSourceInitializer
            implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
                    applicationContext,
                    "spring.datasource.url=" + database.getJdbcUrl(),
                    "spring.datasource.username=" + database.getUsername(),
                    "spring.datasource.password=" + database.getPassword(),
                    "spring.jpa.hibernate.ddl-auto=update"
            );
        }
    }

    @BeforeEach
    public void clearData() {
        studentRepository.deleteAll();
        courseRepository.deleteAll();
    }

    @Test
    public void createStudent_should_create_valid_student_then_return_created_student() throws Exception {
        NewStudentDto newStudentDto = new NewStudentDto("John Doe");
        mockMvc.perform(post("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newStudentDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("John Doe"));
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
        Student student1 = Student.builder().name("John Doe").courses(new HashSet<>()).build();
        Student student2 = Student.builder().name("Jane Doe").courses(new HashSet<>()).build();
        List<Student> students = Arrays.asList(student1, student2);
        studentRepository.saveAll(students);
        mockMvc.perform(get("/students")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("John Doe"));
    }

    @Test
    public void getAllStudents_should_return_students_when_list_is_empty() throws Exception {
        mockMvc.perform(get("/students")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Collections.emptyList())));
    }

    @Test
    public void getStudentById_should_return_correct_student() throws Exception {
        Student student = Student.builder().name("John Doe").courses(new HashSet<>()).build();
        Student savedStudent = studentRepository.save(student);
        mockMvc.perform(get("/students/{id}", savedStudent.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe"));
    }

    @Test
    public void getStudentById_should_return_not_found_when_id_does_not_exist() throws Exception {
        Long id = 11L;
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
        List<Course> courses = Arrays.asList(
                Course.builder().title("Course 1").build(), Course.builder().title("Course 2").build());
        List<Course> savedCourses = courseRepository.saveAll(courses);
        Student student = Student.builder()
                .name("NAME").courses(new HashSet<>())
                .build();
        Student savedStudent = studentRepository.save(student);
        mockMvc.perform(patch("/students/{studentId}", savedStudent.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Arrays.asList(savedCourses.get(0).getId(), savedCourses.get(1).getId()))))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.courseIds[0]").value(savedCourses.get(0).getId()))
                .andExpect(jsonPath("$.version").value(student.getVersion()));
    }

    @Test
    public void addStudentToCourses_should_return_not_found_when_course_id_does_not_exist() throws Exception {
        Long studentId = 1L;
        List<Long> courseIds = List.of(2L);
        mockMvc.perform(patch("/students/{studentId}", studentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseIds)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void addStudentToCourses_should_return_not_found_when_student_id_does_not_exist() throws Exception {
        Long studentId = 1L;
        List<Long> courseIds = List.of(2L);
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
        Student student = Student.builder().name("John Doe").courses(new HashSet<>()).build();
        Student savedStudent = studentRepository.save(student);
        Long studentId = savedStudent.getId();
        UpdateStudentDto updateStudentDto = new UpdateStudentDto();
        updateStudentDto.setName("New Name");
        updateStudentDto.setVersion(savedStudent.getVersion());

        mockMvc.perform(put("/students/{studentId}", studentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateStudentDto)))
                .andExpect(status().isAccepted())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value(updateStudentDto.getName()))
                .andExpect(jsonPath("$.version").value(student.getVersion() + 1));
    }

    @Test
    void updateStudent_when_student_not_found_then_return_not_found() throws Exception {
        Long studentId = 1L;
        UpdateStudentDto updateStudentDto = new UpdateStudentDto();
        updateStudentDto.setName("New Name");
        updateStudentDto.setVersion(1);

        mockMvc.perform(put("/students/{studentId}", studentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateStudentDto)))
                .andExpect(status().isNotFound());
    }


    @Test
    void updateStudent_with_parallel_requests_then_return_accepted_and_conflict() throws Exception {
        Student student = Student.builder().name("John Doe").courses(new HashSet<>()).build();
        Student savedStudent = studentRepository.save(student);

        Long studentId = savedStudent.getId();
        UpdateStudentDto updateStudentDto = new UpdateStudentDto();
        updateStudentDto.setName("New Name");
        updateStudentDto.setVersion(savedStudent.getVersion());

        int numberOfRequests = 2;
        List<CompletableFuture<MvcResult>> futures = new ArrayList<>();

        for (int i = 0; i < numberOfRequests; i++) {
            futures.add(i, CompletableFuture.supplyAsync(() -> {
                try {
                    return mockMvc.perform(put("/students/{studentId}", studentId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(updateStudentDto)))
                            .andReturn();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }));
        }

        List<MvcResult> results = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList()))
                .get();


        int countConflict = 0;
        int countAccepted = 0;
        int countOther = 0;

        for (int i = 0; i < numberOfRequests; i++) {
            MvcResult result = results.get(i);
            int statusCode = result.getResponse().getStatus();

            if (statusCode == HttpStatus.CONFLICT.value()) {
                countConflict++;
            } else if (statusCode == HttpStatus.ACCEPTED.value()){
                countAccepted++;
            } else {
                countOther++;
            }
        }
        assertEquals(1, countConflict);
        assertEquals(1, countAccepted);
        assertEquals(0, countOther);
    }

    @Test
    public void deleteCourseFromStudentCourses_when_courses_exist_then_courses_are_removed_successfully() throws Exception {
        Course course1 = new Course();
        course1.setTitle("Course 1");
        Course course2 = new Course();
        course2.setTitle("Course 2");
        courseRepository.save(course1);
        courseRepository.save(course2);

        Student student = new Student();
        student.setName("John Doe");
        student.setCourses(new HashSet<>());
        studentRepository.save(student);

        student.getCourses().addAll(List.of(course1, course2));
        studentRepository.save(student);

        List<Long> courseIds = List.of(course1.getId());

        mockMvc.perform(delete("/students/{studentId}", student.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseIds)))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.name").value(student.getName()))
                .andExpect(jsonPath("$.id").value(student.getId()))
                .andExpect(jsonPath("$.courseIds[0]").value(course2.getId()));
    }

    @Test
    public void deleteCourseFromStudentCourses_when_courses_do_not_exist_then_throw_exception() throws Exception {
        Long studentId = 1L;
        List<Long> courseIds = List.of(1L, 2L);
        mockMvc.perform(delete("/students/{studentId}", studentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseIds)))
                .andExpect(status().isNotFound());
    }

}
