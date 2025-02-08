package ru.tinkoff.belskya.hw7.dto.course;

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
@Schema(name = "CourseDto", description = "DTO for representing course information")
public class CourseDto {

    @Schema(description = "ID of the course", example = "1")
    private Long id;

    @Schema(description = "Title of the course", example = "Introduction to Programming")
    private String title;

    @Schema(description = "Description of the course", example = "This course covers the basics of programming.")
    private String description;

    @ArraySchema(schema = @Schema(description = "List of student IDs enrolled in the course", example = "[1, 2, 3]"))
    private List<Long> studentIds;

    @Schema(description = "Version of the course data", example = "1")
    private Integer version;

}