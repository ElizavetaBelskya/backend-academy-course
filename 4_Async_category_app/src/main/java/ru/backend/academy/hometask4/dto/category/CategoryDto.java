package ru.backend.academy.hometask4.dto.category;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDto {

    @NotBlank(message = "{category.id.notBlank}")
    private String id;

    private String url;

    @NotBlank(message = "{category.title.notBlank}")
    private String title;

}
