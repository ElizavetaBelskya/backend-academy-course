package ru.backend.academy.hometask3.controller.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.backend.academy.hometask3.dto.ExceptionDto;
import ru.backend.academy.hometask3.dto.ValidationErrorDto;
import ru.backend.academy.hometask3.exception.ProductDatabaseException;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(ProductDatabaseException.class)
    public ResponseEntity<ExceptionDto> handleProductDatabaseException(ProductDatabaseException ex) {
        return ResponseEntity.status(ex.getStatus())
                .body(ExceptionDto.builder()
                        .message(ex.getMessage())
                        .status(ex.getStatus())
                        .build());
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
            ValidationErrorDto errorDto = ValidationErrorDto.builder()
                    .message(errorMessage)
                    .fieldName(fieldName)
                    .objectName(objectName)
                    .build();

            errors.add(errorDto);
        });

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errors);
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class, IllegalArgumentException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<ExceptionDto> handleTypeMismatch(Exception ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ExceptionDto.builder()
                        .message(ex.getMessage())
                        .status(HttpStatus.BAD_REQUEST.value())
                        .build());
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<ExceptionDto> handleThrowable(Exception exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ExceptionDto.builder()
                        .message(exception.getMessage())
                        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .build());
    }

}
