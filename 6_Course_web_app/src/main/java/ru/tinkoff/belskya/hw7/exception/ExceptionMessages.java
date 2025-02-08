package ru.tinkoff.belskya.hw7.exception;

public interface ExceptionMessages {
    String STUDENT_NOT_FOUND = "Student with ID %s not found";
    String COURSE_NOT_FOUND = "Course with ID %s not found";
    String COURSE_NOT_FOUND_WITHOUT_ID = "Course not found";
    String COURSE_ALREADY_EXISTS = "Course with title %s already exists";
    String STUDENT_LOCK_CONFLICT = "Version conflict while trying update student with ID %s";

    String COURSE_LOCK_CONFLICT = "Version conflict while trying update course with ID %s";

}
