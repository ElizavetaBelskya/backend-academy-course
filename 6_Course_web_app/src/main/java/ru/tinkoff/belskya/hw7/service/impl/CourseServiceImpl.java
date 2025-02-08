package ru.tinkoff.belskya.hw7.service.impl;

import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import ru.tinkoff.belskya.hw7.dto.course.CourseDto;
import ru.tinkoff.belskya.hw7.dto.course.NewCourseDto;
import ru.tinkoff.belskya.hw7.dto.course.UpdateCourseDto;
import ru.tinkoff.belskya.hw7.exception.AlreadyExistsException;
import ru.tinkoff.belskya.hw7.exception.EducationPlatformNotFoundException;
import ru.tinkoff.belskya.hw7.exception.EducationPlatformOptimisticLockingException;
import ru.tinkoff.belskya.hw7.exception.ExceptionMessages;
import ru.tinkoff.belskya.hw7.mapper.CourseMapper;
import ru.tinkoff.belskya.hw7.model.Course;
import ru.tinkoff.belskya.hw7.repository.CourseRepository;
import ru.tinkoff.belskya.hw7.service.CourseService;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final CourseMapper mapper;

    public CourseDto createCourse(NewCourseDto courseDto) {
        Course course = mapper.courseDtoToCourse(courseDto);
        course.setStudents(new HashSet<>());
        if (courseRepository.findByTitle(course.getTitle()).isEmpty()) {
            return mapper.courseToCourseDto(courseRepository.save(course));
        } else {
            throw new AlreadyExistsException(ExceptionMessages.COURSE_ALREADY_EXISTS, course.getTitle());
        }
    }

    public List<CourseDto> getAllCourses() {
        return courseRepository.findAll().stream().map(mapper::courseToCourseDto).toList();
    }

    public CourseDto getCourseById(Long id) {
        return mapper
                .courseToCourseDto(courseRepository.findById(id)
                .orElseThrow(() -> new EducationPlatformNotFoundException(ExceptionMessages.COURSE_NOT_FOUND, id)));
    }

    public CourseDto updateCourse(Long id, UpdateCourseDto updateCourseDto) {
        Optional<Course> course = courseRepository.findById(id);
        if (course.isPresent()) {
            Course courseForUpdate = mapper.updateCourseDtoToCourse(updateCourseDto);
            courseForUpdate.setId(id);
            courseForUpdate.setStudents(course.get().getStudents());
            try {
                Course updatedCourse = courseRepository.save(courseForUpdate);
                return mapper.courseToCourseDto(updatedCourse);
            } catch (ObjectOptimisticLockingFailureException ex) {
                throw new EducationPlatformOptimisticLockingException(ExceptionMessages.COURSE_LOCK_CONFLICT, id);
            }
        } else {
            throw new EducationPlatformNotFoundException(ExceptionMessages.COURSE_NOT_FOUND, id);
        }
    }


}
