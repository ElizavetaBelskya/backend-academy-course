package ru.tinkoff.belskya.hw7.service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import ru.tinkoff.belskya.hw7.dto.course.CourseDto;
import ru.tinkoff.belskya.hw7.dto.course.NewCourseDto;
import ru.tinkoff.belskya.hw7.dto.course.UpdateCourseDto;
import ru.tinkoff.belskya.hw7.exception.AlreadyExistsException;
import ru.tinkoff.belskya.hw7.exception.EducationPlatformNotFoundException;
import ru.tinkoff.belskya.hw7.exception.EducationPlatformOptimisticLockingException;
import ru.tinkoff.belskya.hw7.mapper.CourseMapper;
import ru.tinkoff.belskya.hw7.model.Course;
import ru.tinkoff.belskya.hw7.repository.CourseRepository;
import ru.tinkoff.belskya.hw7.service.impl.CourseServiceImpl;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class CourseServiceTest {
    @Mock
    private CourseRepository courseRepository;
    @InjectMocks
    private CourseServiceImpl courseService;

    @Mock
    private CourseMapper courseMapper;
    private NewCourseDto newCourseDto;
    private Course course;
    private Course courseBeforePersist;
    private CourseDto courseDto;

    @BeforeEach
    public void setUp() {
        newCourseDto = new NewCourseDto();
        newCourseDto.setTitle("Test Course");
        courseBeforePersist = Course.builder().title(newCourseDto.getTitle()).build();
        course = Course.builder().id(1L).title(newCourseDto.getTitle()).students(new HashSet<>()).build();
        courseDto = CourseDto.builder().id(1L).title(newCourseDto.getTitle()).studentIds(new ArrayList<>()).build();
    }

    @Test
    public void createCourse_when_new_course_then_course_created() {
        when(courseRepository.findByTitle(course.getTitle())).thenReturn(Optional.empty());
        when(courseMapper.courseDtoToCourse(newCourseDto)).thenReturn(courseBeforePersist);
        when(courseRepository.save(courseBeforePersist)).thenReturn(course);
        when(courseMapper.courseToCourseDto(course)).thenReturn(courseDto);
        CourseDto result = courseService.createCourse(newCourseDto);
        assertEquals(courseDto, result);
    }

    @Test
    public void createCourse_when_course_exists_then_already_exists_exception() {
        when(courseRepository.findByTitle(course.getTitle())).thenReturn(Optional.of(course));
        when(courseMapper.courseDtoToCourse(newCourseDto)).thenReturn(courseBeforePersist);
        assertThrows(AlreadyExistsException.class, () -> courseService.createCourse(newCourseDto));
    }

    @Test
    public void getAllCourses_should_return_correct_result() {
        List<Course> courses = Arrays.asList(course);
        when(courseRepository.findAll()).thenReturn(courses);
        when(courseMapper.courseToCourseDto(course)).thenReturn(courseDto);
        List<CourseDto> result = courseService.getAllCourses();
        assertEquals(1, result.size());
        assertEquals(courseDto, result.get(0));
    }

    @Test
    public void getCourseById_when_course_exists_should_return_correct_result() {
        when(courseRepository.findById(course.getId())).thenReturn(Optional.of(course));
        when(courseMapper.courseToCourseDto(course)).thenReturn(courseDto);
        CourseDto result = courseService.getCourseById(course.getId());
        assertEquals(courseDto, result);
    }

    @Test
    public void getCourseById_when_course_does_not_exist() {
        when(courseRepository.findById(course.getId())).thenReturn(Optional.empty());
        assertThrows(EducationPlatformNotFoundException.class, () -> courseService.getCourseById(course.getId()));
    }


    @Test
    public void updateCourse_when_data_is_correct_then_return_CourseDto() {
        Course course = new Course();
        course.setId(1L);
        course.setTitle("Course 1");
        course.setVersion(1);

        UpdateCourseDto updateCourseDto = new UpdateCourseDto();
        updateCourseDto.setTitle("Updated Course");
        updateCourseDto.setVersion(1);

        Course courseForUpdate = new Course();
        courseForUpdate.setTitle("Updated Course");
        courseForUpdate.setId(1L);
        courseForUpdate.setVersion(1);

        courseDto = new CourseDto();
        courseDto.setId(1L);
        courseDto.setVersion(2);
        courseDto.setTitle("Updated Course");

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(courseMapper.updateCourseDtoToCourse(updateCourseDto)).thenReturn(courseForUpdate);
        when(courseRepository.save(courseForUpdate)).thenReturn(courseForUpdate);
        when(courseMapper.courseToCourseDto(courseForUpdate)).thenReturn(courseDto);

        CourseDto result = courseService.updateCourse(1L, updateCourseDto);

        assertEquals(courseDto, result);
    }

    @Test
    public void updateCourse_when_id_is_not_found_then_throw_EducationPlatformNotFoundException() {
        Course course = new Course();
        course.setId(1L);
        courseDto = new CourseDto();
        courseDto.setId(1L);
        UpdateCourseDto updateCourseDto = new UpdateCourseDto();
        updateCourseDto.setTitle("Updated Course");
        when(courseRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EducationPlatformNotFoundException.class, () -> courseService.updateCourse(1L, updateCourseDto));

        verify(courseRepository, times(1)).findById(1L);
    }

    @Test
    public void updateCourse_when_id_is_not_found_then_throw_EducationPlatformOptimisticLockingException() {
        Course course = new Course();
        course.setId(1L);
        course.setTitle("Course 1");
        course.setVersion(1);

        UpdateCourseDto updateCourseDto = new UpdateCourseDto();
        updateCourseDto.setTitle("Updated Course");
        updateCourseDto.setVersion(1);

        Course courseForUpdate = new Course();
        courseForUpdate.setTitle("Updated Course");
        courseForUpdate.setId(1L);
        courseForUpdate.setVersion(1);

        courseDto = new CourseDto();
        courseDto.setId(1L);
        courseDto.setVersion(2);
        courseDto.setTitle("Updated Course");

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(courseMapper.updateCourseDtoToCourse(updateCourseDto)).thenReturn(courseForUpdate);
        when(courseRepository.save(courseForUpdate)).thenThrow(ObjectOptimisticLockingFailureException.class);

        assertThrows(EducationPlatformOptimisticLockingException.class,
                () -> courseService.updateCourse(1L, updateCourseDto));
    }

}