package ru.tinkoff.belskya.hw7.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tinkoff.belskya.hw7.model.Course;

import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {

    Optional<Course> findByTitle(String title);
}
