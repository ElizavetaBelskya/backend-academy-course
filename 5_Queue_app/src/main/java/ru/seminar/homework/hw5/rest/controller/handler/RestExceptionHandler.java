package ru.seminar.homework.hw5.rest.controller.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.seminar.homework.hw5.dto.ExceptionDto;
import ru.seminar.homework.hw5.dto.ValidationErrorDto;
import ru.seminar.homework.hw5.exception.TaskQueueException;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(TaskQueueException.class)
    public ResponseEntity<ExceptionDto> handleProductDatabaseException(TaskQueueException ex) {
        ExceptionDto exceptionDto = new ExceptionDto();
        exceptionDto.setMessage(ex.getMessage());
        exceptionDto.setStatus(ex.getHttpStatus().value());
        return ResponseEntity.status(ex.getHttpStatus()).body(exceptionDto);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<List<ValidationErrorDto>> handleControllerValidationError(MethodArgumentNotValidException ex) {
        List<ValidationErrorDto> errors = new ArrayList<>();

        ex.getBindingResult().getAllErrors().forEach(error -> {
            String errorMessage = error.getDefaultMessage();

            String fieldName = null;
            String objectName = error.getObjectName();

            if (error instanceof FieldError fieldError) {
                fieldName = fieldError.getField();
            }
            ValidationErrorDto errorDto = new ValidationErrorDto();
            errorDto.setMessage(errorMessage);
            errorDto.setFieldName(fieldName);
            errorDto.setObjectName(objectName);
            errors.add(errorDto);
        });

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errors);
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class, IllegalArgumentException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<ExceptionDto> handleTypeMismatch(Exception ex) {
        ExceptionDto exceptionDto = new ExceptionDto();
        exceptionDto.setMessage(ex.getMessage());
        exceptionDto.setStatus(HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(exceptionDto);
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<ExceptionDto> handleThrowable(Exception exception) {
        ExceptionDto exceptionDto = new ExceptionDto();
        exceptionDto.setMessage(exception.getMessage());
        exceptionDto.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(exceptionDto);
    }

}
