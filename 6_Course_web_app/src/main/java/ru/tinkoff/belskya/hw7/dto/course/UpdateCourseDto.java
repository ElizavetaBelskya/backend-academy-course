package ru.tinkoff.belskya.hw7.dto.course;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OptimisticLocking;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@OptimisticLocking
public class UpdateCourseDto {

    @NotNull(message = "Title must not be null")
    @Pattern(regexp = "^[A-Za-z0-9 ]+$", message = "Title must contain only letters, numbers, or spaces")
    private String title;

    private String description;

    @NotNull
    private Integer version;
}
