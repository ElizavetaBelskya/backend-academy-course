package ru.tinkoff.belskya.hw7.service;

import jakarta.persistence.OptimisticLockException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import ru.tinkoff.belskya.hw7.dto.student.NewStudentDto;
import ru.tinkoff.belskya.hw7.dto.student.StudentDto;
import ru.tinkoff.belskya.hw7.dto.student.UpdateStudentDto;
import ru.tinkoff.belskya.hw7.exception.AlreadyExistsException;
import ru.tinkoff.belskya.hw7.exception.EducationPlatformNotFoundException;
import ru.tinkoff.belskya.hw7.exception.EducationPlatformOptimisticLockingException;
import ru.tinkoff.belskya.hw7.mapper.StudentMapper;
import ru.tinkoff.belskya.hw7.model.Course;
import ru.tinkoff.belskya.hw7.model.Student;
import ru.tinkoff.belskya.hw7.repository.CourseRepository;
import ru.tinkoff.belskya.hw7.repository.StudentRepository;
import ru.tinkoff.belskya.hw7.service.impl.StudentServiceImpl;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class StudentServiceTest {
    @Mock
    private StudentRepository studentRepository;
    @Mock
    private CourseRepository courseRepository;
    @Mock
    private StudentMapper studentMapper;
    @InjectMocks
    private StudentServiceImpl studentService;
    private NewStudentDto newStudentDto;
    private Student student;
    private StudentDto studentDto;
    private List<Long> courseIds;
    private List<Course> courses;

    @BeforeEach
    public void setUp() {
        newStudentDto = new NewStudentDto("John Doe");
        student = Student.builder().id(1L).name("John Doe").courses(new HashSet<>()).version(12).build();
        studentDto = new StudentDto(1L, "John Doe", new ArrayList<>(), 12);
        courseIds = List.of(1L, 2L);
        courses = List.of(new Course(1L, "Math", "Math course", new HashSet<>(), 1),
                new Course(2L, "Physics", "Physics course", new HashSet<>(), 1));
    }

    @Test
    public void createStudent_should_add_new_student_and_return() {
        when(studentMapper.studentDtoToStudent(newStudentDto)).thenReturn(student);
        when(studentRepository.save(student)).thenReturn(student);
        when(studentMapper.studentToStudentDto(student)).thenReturn(studentDto);

        StudentDto result = studentService.createStudent(newStudentDto);

        verify(studentRepository, times(1)).save(student);
        verify(studentMapper, times(1)).studentToStudentDto(student);
        assertThat(result).isEqualTo(studentDto);
    }

    @Test
    public void createStudent_should_return_already_exists_when_this_student_exists() {
        when(studentMapper.studentDtoToStudent(newStudentDto)).thenReturn(student);
        when(studentRepository.save(student)).thenThrow(new AlreadyExistsException("Student with name %s already exists", student.getName()));
        assertThatThrownBy(() -> studentService.createStudent(newStudentDto))
                .isInstanceOf(AlreadyExistsException.class);
        verify(studentRepository, times(1)).save(student);
        verify(studentMapper, never()).studentToStudentDto(student);
    }

    @Test
    public void getAllStudents_when_students_exist_should_return_students() {
        List<Student> students = List.of(student);
        when(studentRepository.findAll()).thenReturn(students);
        when(studentMapper.studentToStudentDto(student)).thenReturn(studentDto);
        List<StudentDto> result = studentService.getAllStudents();
        verify(studentRepository, times(1)).findAll();
        verify(studentMapper, times(1)).studentToStudentDto(student);
        assertThat(result).containsExactly(studentDto);
    }

    @Test
    public void getAllStudents_when_no_students_should_return_empty_list() {
        when(studentRepository.findAll()).thenReturn(new ArrayList<>());
        List<StudentDto> result = studentService.getAllStudents();
        verify(studentRepository, times(1)).findAll();
        assertThat(result).isEmpty();
    }

    @Test
    public void getStudentById_when_student_exists_should_return_student_dto() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(studentMapper.studentToStudentDto(student)).thenReturn(studentDto);

        StudentDto result = studentService.getStudentById(1L);

        verify(studentRepository, times(1)).findById(1L);
        verify(studentMapper, times(1)).studentToStudentDto(student);
        assertThat(result).isEqualTo(studentDto);
    }

    @Test
    public void getStudentById_when_student_does_not_exist_should_throw_exception() {
        when(studentRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> studentService.getStudentById(1L))
                .isInstanceOf(EducationPlatformNotFoundException.class);
        verify(studentRepository, times(1)).findById(1L);
        verify(studentMapper, never()).studentToStudentDto(any());
    }

    @Test
    public void addStudentToCourses_when_student_and_courses_exist_should_return_student_dto() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(courseRepository.findAllById(courseIds)).thenReturn(courses);
        when(studentMapper.studentToStudentDto(student)).thenReturn(studentDto);
        when(studentRepository.save(student)).thenReturn(student);

        StudentDto result = studentService.addStudentToCourses(1L, courseIds);

        verify(studentRepository, times(1)).save(student);
        verify(studentMapper, times(1)).studentToStudentDto(student);
        assertThat(result).isEqualTo(studentDto);
    }

    @Test
    public void addStudentToCourses_when_student_does_not_exist_then_throw_exception() {
        when(studentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studentService.addStudentToCourses(1L, courseIds))
                .isInstanceOf(EducationPlatformNotFoundException.class);

        verify(studentRepository, times(1)).findById(1L);
        verify(studentMapper, never()).studentToStudentDto(any());
    }

    @Test
    public void addStudentToCourses_when_course_does_not_exist_then_throw_exception() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(courseRepository.findAllById(courseIds)).thenReturn(new ArrayList<>());
        assertThatThrownBy(() -> studentService.addStudentToCourses(1L, courseIds))
                .isInstanceOf(EducationPlatformNotFoundException.class);
        verify(courseRepository, times(1)).findAllById(courseIds);
        verify(studentMapper, never()).studentToStudentDto(any());
    }


    @Test
    void updateStudent_when_correct_version_then_update_success() {
        Long studentId = 1L;
        UpdateStudentDto updateStudentDto = new UpdateStudentDto();
        updateStudentDto.setName("New Name");
        updateStudentDto.setVersion(1);

        Student updateStudent = new Student();
        updateStudent.setName("New Name");
        updateStudent.setVersion(1);

        StudentDto newStudentDto = new StudentDto();
        newStudentDto.setName("New Name");
        newStudentDto.setVersion(2);

        Student existingStudent = new Student();
        existingStudent.setId(studentId);
        existingStudent.setName("Old Name");
        existingStudent.setVersion(1);
        existingStudent.setCourses(new HashSet<>());

        Student savedStudent = new Student();
        savedStudent.setId(studentId);
        savedStudent.setName("New Name");
        savedStudent.setVersion(2);

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(existingStudent));
        when(studentRepository.save(updateStudent)).thenReturn(savedStudent);
        when(studentMapper.studentToStudentDto(savedStudent)).thenReturn(newStudentDto);
        when(studentMapper.studentDtoToStudent(updateStudentDto)).thenReturn(updateStudent);

        StudentDto result = studentService.updateStudent(studentId, updateStudentDto);

        assertNotNull(result);
        assertEquals(updateStudentDto.getName(), result.getName());
        assertEquals(savedStudent.getVersion(), result.getVersion());
    }

    @Test
    void updateStudent_when_student_not_found_then_throw_EducationPlatformNotFoundException() {
        Long studentId = 1L;
        UpdateStudentDto updateStudentDto = new UpdateStudentDto();
        updateStudentDto.setName("New Name");
        updateStudentDto.setVersion(1);

        when(studentRepository.findById(studentId)).thenReturn(Optional.empty());

        assertThrows(EducationPlatformNotFoundException.class, () -> {
            studentService.updateStudent(studentId, updateStudentDto);
        });
    }

    @Test
    void updateStudent_when_student_version_is_old_then_throw_EducationPlatformOptimisticLockingException() {
        Long studentId = 1L;
        UpdateStudentDto updateStudentDto = new UpdateStudentDto();
        updateStudentDto.setName("New Name");
        updateStudentDto.setVersion(1);

        Student updateStudent = new Student();
        updateStudent.setName("New Name");
        updateStudent.setVersion(1);

        Student existingStudent = new Student();
        existingStudent.setId(studentId);
        existingStudent.setName("Old Name");
        existingStudent.setVersion(1);
        existingStudent.setCourses(new HashSet<>());

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(existingStudent));
        when(studentMapper.studentDtoToStudent(updateStudentDto)).thenReturn(updateStudent);
        when(studentRepository.save(updateStudent)).thenThrow(ObjectOptimisticLockingFailureException.class);

        assertThrows(EducationPlatformOptimisticLockingException.class, () -> {
            studentService.updateStudent(studentId, updateStudentDto);
        });
    }

    @Test
    public void deleteCourseFromStudentCourses_when_courses_exist_then_courses_are_removed_successfully() {
        student = new Student();
        student.setId(1L);
        student.setName("John Doe");
        student.setCourses(new HashSet<>());
        student.setVersion(1);

        Course course1 = new Course();
        course1.setId(1L);
        course1.setTitle("Course 1");

        Course course2 = new Course();
        course2.setId(2L);
        course2.setTitle("Course 2");

        studentDto = new StudentDto();
        studentDto.setId(student.getId());
        studentDto.setName(student.getName());
        studentDto.setCourseIds(new ArrayList<>());
        studentDto.setVersion(student.getVersion());

        Student studentWithCourses = Student.builder().courses(new HashSet<>()).build();
        studentWithCourses.getCourses().addAll(Set.of(course1, course2));
        when(studentRepository.findById(1L)).thenReturn(Optional.of(studentWithCourses));
        when(courseRepository.findAllById(Arrays.asList(1L, 2L))).thenReturn(Arrays.asList(course1, course2));
        when(studentRepository.save(studentWithCourses)).thenReturn(student);
        when(studentMapper.studentToStudentDto(student)).thenReturn(studentDto);

        StudentDto result = studentService.deleteCourseFromStudentCourses(1L, Arrays.asList(1L, 2L));
        assertEquals(1L, result.getId());
        assertEquals(0, result.getCourseIds().size());
        assertEquals(0, student.getCourses().size());
    }

    @Test
    public void deleteCourseFromStudentCourses_when_courses_do_not_exist_then_throw_exception() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(courseRepository.findAllById(Arrays.asList(3L, 4L))).thenReturn(List.of());

        assertThrows(EducationPlatformNotFoundException.class, () -> {
            studentService.deleteCourseFromStudentCourses(1L, Arrays.asList(3L, 4L));
        });
    }

}