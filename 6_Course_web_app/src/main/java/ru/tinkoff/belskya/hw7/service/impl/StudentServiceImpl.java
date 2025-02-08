package ru.tinkoff.belskya.hw7.service.impl;


import jakarta.persistence.LockModeType;
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import ru.tinkoff.belskya.hw7.dto.student.NewStudentDto;
import ru.tinkoff.belskya.hw7.dto.student.StudentDto;
import ru.tinkoff.belskya.hw7.dto.student.UpdateStudentDto;
import ru.tinkoff.belskya.hw7.exception.EducationPlatformNotFoundException;
import ru.tinkoff.belskya.hw7.exception.EducationPlatformOptimisticLockingException;
import ru.tinkoff.belskya.hw7.exception.ExceptionMessages;
import ru.tinkoff.belskya.hw7.mapper.StudentMapper;
import ru.tinkoff.belskya.hw7.model.Course;
import ru.tinkoff.belskya.hw7.model.Student;
import ru.tinkoff.belskya.hw7.repository.CourseRepository;
import ru.tinkoff.belskya.hw7.repository.StudentRepository;
import ru.tinkoff.belskya.hw7.service.StudentService;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;


@RequiredArgsConstructor
@Service
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;

    private final CourseRepository courseRepository;

    private final StudentMapper mapper;

    @Override
    public StudentDto createStudent(NewStudentDto studentDto) {
        Student student = mapper.studentDtoToStudent(studentDto);
        student.setCourses(new HashSet<>());
        return mapper.studentToStudentDto(studentRepository.save(student));
    }

    @Override
    public List<StudentDto> getAllStudents() {
        return studentRepository.findAll().stream().map(mapper::studentToStudentDto).toList();
    }

    @Override
    public StudentDto getStudentById(Long id) {
        return mapper.studentToStudentDto(studentRepository.findById(id)
                .orElseThrow(() -> new EducationPlatformNotFoundException(ExceptionMessages.STUDENT_NOT_FOUND, id)));
    }


    @Transactional
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Override
    public StudentDto addStudentToCourses(Long studentId, List<Long> courseIds) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new EducationPlatformNotFoundException(ExceptionMessages.COURSE_NOT_FOUND, studentId));
        List<Course> courses = courseRepository.findAllById(courseIds);
        if (courses.size() != courseIds.size()) {
            throw new EducationPlatformNotFoundException(ExceptionMessages.COURSE_NOT_FOUND_WITHOUT_ID);
        }
        student.getCourses().addAll(courses);
        return mapper.studentToStudentDto(studentRepository.save(student));
    }

    @Transactional
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Override
    public StudentDto deleteCourseFromStudentCourses(Long studentId, List<Long> courseIds) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new EducationPlatformNotFoundException(ExceptionMessages.COURSE_NOT_FOUND, studentId));
        List<Course> courses = courseRepository.findAllById(courseIds);
        if (courses.size() != courseIds.size()) {
            throw new EducationPlatformNotFoundException(ExceptionMessages.COURSE_NOT_FOUND_WITHOUT_ID);
        }
        courses.forEach(student.getCourses()::remove);
        return mapper.studentToStudentDto(studentRepository.save(student));
    }


    @Override
    public StudentDto updateStudent(Long studentId, UpdateStudentDto updateStudentDto) {
        Optional<Student> existingStudent = studentRepository.findById(studentId);
        if (existingStudent.isPresent()) {
            Student student = existingStudent.get();
            Student studentForUpdate = mapper.studentDtoToStudent(updateStudentDto);
            studentForUpdate.setId(studentId);
            studentForUpdate.setCourses(student.getCourses());
            try {
                Student updatedStudent = studentRepository.save(studentForUpdate);
                return mapper.studentToStudentDto(updatedStudent);
            } catch (ObjectOptimisticLockingFailureException ex) {
                throw new EducationPlatformOptimisticLockingException(ExceptionMessages.STUDENT_LOCK_CONFLICT, studentId);
            }
        } else {
            throw new EducationPlatformNotFoundException(ExceptionMessages.STUDENT_NOT_FOUND, studentId);
        }
    }



}
