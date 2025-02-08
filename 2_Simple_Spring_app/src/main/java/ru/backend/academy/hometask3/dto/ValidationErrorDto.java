package ru.backend.academy.hometask3.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ValidationErrorDto {

    private String fieldName;

    private String objectName;

    private String message;

}
