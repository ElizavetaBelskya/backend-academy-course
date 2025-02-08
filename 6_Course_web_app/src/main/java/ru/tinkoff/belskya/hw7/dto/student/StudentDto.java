package ru.tinkoff.belskya.hw7.dto.student;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "StudentDto", description = "DTO for representing student information")
public class StudentDto {

    @Schema(description = "ID of the student", example = "1")
    private Long id;

    @Schema(description = "Name of the student", example = "John Doe")
    private String name;

    @ArraySchema(schema = @Schema(description = "List of course IDs associated with the student", example = "[1, 2, 3]"))
    private List<Long> courseIds;

    @Schema(description = "Version of the student data", example = "1")
    private Integer version;

}