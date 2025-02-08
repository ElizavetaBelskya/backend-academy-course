package ru.tinkoff.belskya.hw7.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.tinkoff.belskya.hw7.model.Student;

public interface StudentRepository extends JpaRepository<Student, Long> {

    @Modifying
    @Query("UPDATE Student s SET s.name = :name WHERE s.id = :id")
    int updateStudentNameById(@Param("id") Long id, @Param("name") String name);


}
