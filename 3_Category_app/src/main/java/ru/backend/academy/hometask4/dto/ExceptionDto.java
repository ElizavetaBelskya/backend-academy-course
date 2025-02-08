package ru.backend.academy.hometask4.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExceptionDto {
    private String message;
    private int status;

}
