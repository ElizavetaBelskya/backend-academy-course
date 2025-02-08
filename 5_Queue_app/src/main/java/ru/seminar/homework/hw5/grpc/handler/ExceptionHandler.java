package ru.seminar.homework.hw5.grpc.handler;

import io.grpc.Status;
import io.grpc.StatusException;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.seminar.homework.hw5.dto.ValidationErrorDto;
import ru.seminar.homework.hw5.exception.TaskNotFoundException;
import ru.seminar.homework.hw5.exception.TaskQueueException;

import java.util.ArrayList;
import java.util.List;


@GrpcAdvice
public class ExceptionHandler {

    @GrpcExceptionHandler(TaskQueueException.class)
    public StatusException handleTaskQueueException(TaskQueueException ex) {
        return Status.INTERNAL.withDescription(ex.getMessage()).asException();
    }

    @GrpcExceptionHandler(TaskNotFoundException.class)
    public StatusException handleTaskNotFoundException(TaskNotFoundException ex) {
        return Status.NOT_FOUND.withDescription(ex.getMessage()).asException();
    }
    @GrpcExceptionHandler(MethodArgumentNotValidException.class)
    public StatusException handleValidationException(MethodArgumentNotValidException ex) {
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

        return Status.INVALID_ARGUMENT
                .withDescription("Validation error")
                .withCause(ex)
                .asException();
    }

    @GrpcExceptionHandler(MethodArgumentTypeMismatchException.class)
    public StatusException handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return Status.INVALID_ARGUMENT
                .withDescription(ex.getMessage())
                .withCause(ex)
                .asException();
    }

    @GrpcExceptionHandler(IllegalArgumentException.class)
    public StatusException handleIllegalArgumentException(IllegalArgumentException ex) {
        return Status.INVALID_ARGUMENT
                .withDescription(ex.getMessage())
                .withCause(ex)
                .asException();
    }

    @GrpcExceptionHandler(HttpMessageNotReadableException.class)
    public StatusException handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        return Status.INVALID_ARGUMENT
                .withDescription(ex.getMessage()).asException();
    }


    @GrpcExceptionHandler(StatusRuntimeException.class)
    public StatusException handleStatusException(StatusRuntimeException ex) {
        return Status.UNAUTHENTICATED.withDescription(ex.getMessage()).asException();
    }


}