package ru.tinkoff.belskya.hw7.controller.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.tinkoff.belskya.hw7.dto.exception.ExceptionDto;
import ru.tinkoff.belskya.hw7.exception.EducationPlatformBasicException;
import ru.tinkoff.belskya.hw7.validation.responses.ValidationErrorDto;
import ru.tinkoff.belskya.hw7.validation.responses.ValidationErrorsDto;

import java.util.ArrayList;
import java.util.List;


@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(EducationPlatformBasicException.class)
    public ResponseEntity<ExceptionDto> handleMarkerServiceException(EducationPlatformBasicException ex) {
        return ResponseEntity.status(ex.getStatus().value())
                .body(ExceptionDto.builder()
                        .message(ex.getMessage())
                        .status(ex.getStatus().value())
                        .build());
    }


    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ValidationErrorsDto> handleControllerValidationError(MethodArgumentNotValidException ex) {
        List<ValidationErrorDto> errors = new ArrayList<>();

        ex.getBindingResult().getAllErrors().forEach(error -> {
            String errorMessage = error.getDefaultMessage();

            String fieldName = null;
            String objectName = error.getObjectName();

            if (error instanceof FieldError fieldError) {
                fieldName =fieldError.getField();
            }
            ValidationErrorDto errorDto = ValidationErrorDto.builder()
                    .message(errorMessage)
                    .fieldName(fieldName)
                    .objectName(objectName)
                    .build();

            errors.add(errorDto);
        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ValidationErrorsDto.builder()
                        .errors(errors)
                        .build());
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class, IllegalArgumentException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<ExceptionDto> handleTypeMismatch(Exception ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ExceptionDto.builder()
                        .message(ex.getMessage())
                        .status(HttpStatus.BAD_REQUEST.value())
                        .build());
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ExceptionDto> handleOther(Throwable throwable) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ExceptionDto.builder()
                        .message(throwable.getMessage())
                        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .build());
    }


}
