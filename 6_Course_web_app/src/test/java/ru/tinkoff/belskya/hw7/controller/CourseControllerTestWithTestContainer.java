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
import ru.tinkoff.belskya.hw7.dto.course.NewCourseDto;
import ru.tinkoff.belskya.hw7.dto.course.UpdateCourseDto;
import ru.tinkoff.belskya.hw7.model.Course;
import ru.tinkoff.belskya.hw7.repository.CourseRepository;
import ru.tinkoff.belskya.hw7.repository.StudentRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ContextConfiguration(initializers = CourseControllerTestWithTestContainer.DataSourceInitializer.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class CourseControllerTestWithTestContainer {

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
    public void createCourse_should_return_created_and_new_course() throws Exception {
        NewCourseDto newCourseDto = new NewCourseDto("New course", "Description");
        mockMvc.perform(post("/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCourseDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value(newCourseDto.getTitle()))
                .andExpect(jsonPath("$.description").value(newCourseDto.getDescription()));
    }

    @Test
    public void createCourse_should_return_conflict_when_name_exists() throws Exception {
        Course course = Course.builder().title("New course").build();
        courseRepository.save(course);
        NewCourseDto newCourseDto = new NewCourseDto("New course", "Description");
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
        List<Course> courseList = Arrays.asList(
                Course.builder().title("Course 1").build(),
                Course.builder().title("Course 2").build()
        );
        courseRepository.saveAll(courseList);
        courseRepository.saveAll(courseList);
        mockMvc.perform(get("/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Course 1"))
                .andExpect(jsonPath("$[1].title").value("Course 2"));
    }

    @Test
    public void getAllCourses_should_return_ok_when_list_is_empty() throws Exception {
        mockMvc.perform(get("/courses"))
                .andExpect(status().isOk());
    }


    @Test
    public void getCourseById_should_return_course_when_id_exists() throws Exception {
        Course course = Course.builder().title("New course").build();
        Long courseId = courseRepository.save(course).getId();

        mockMvc.perform(get("/courses/{id}", courseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(course.getTitle()));
    }


    @Test
    public void getCourseById_should_return_not_found_when_id_does_not_exist() throws Exception {
        Long courseId = 1L;
        mockMvc.perform(get("/courses/{id}", courseId))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateCourse_when_data_is_correct_then_return_CourseDto() throws Exception {
        Course course = new Course();
        course.setTitle("Old Course");
        course.setStudents(new HashSet<>());
        Long courseId = courseRepository.save(course).getId();
        UpdateCourseDto updateCourseDto = new UpdateCourseDto();
        updateCourseDto.setTitle("Updated Course");
        updateCourseDto.setVersion(course.getVersion());
        mockMvc.perform(put("/courses/{id}", courseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateCourseDto)))
                .andExpect(status().isAccepted())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(courseId))
                .andExpect(jsonPath("$.version").value(course.getVersion() + 1))
                .andExpect(jsonPath("$.title").value(updateCourseDto.getTitle()));
    }

    @Test
    void updateCourse_when_id_is_not_found_then_return_not_found() throws Exception {
        Long courseId = 1L;
        UpdateCourseDto updateCourseDto = new UpdateCourseDto();
        updateCourseDto.setTitle("Updated Course");

        mockMvc.perform(put("/courses/{id}", courseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateCourseDto)))
                .andExpect(status().isNotFound());
    }


    @Test
    void updateCourse_with_parallel_requests_then_return_accepted_and_conflict() throws Exception {
        Course course = Course.builder().title("New course").build();
        Course savedCourse = courseRepository.save(course);
        Long id = savedCourse.getId();
        UpdateCourseDto updateCourseDto = new UpdateCourseDto();
        updateCourseDto.setTitle("Updated Course");
        updateCourseDto.setVersion(savedCourse.getVersion());

        int numberOfRequests = 2;
        List<CompletableFuture<MvcResult>> futures = new ArrayList<>();

        for (int i = 0; i < numberOfRequests; i++) {
            futures.add(i, CompletableFuture.supplyAsync(() -> {
                try {
                    return mockMvc.perform(put("/courses/{id}", id)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(updateCourseDto)))
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


}
