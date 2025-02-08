package ru.backend.academy.hometask3.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {

    @NotBlank(message = "{product.itemNumber.notBlank}")
    @Pattern(regexp = "[A-Z0-9]+", message = "{product.itemNumber.pattern}")
    private String itemNumber;

    @NotBlank(message = "{product.title.notBlank}")
    private String title;

    @NotNull(message = "{product.price.notNull}")
    @DecimalMin(value = "1.0", message = "{product.price.min}")
    private float price;

    @NotNull(message = "{product.quantity.notNull}")
    @Min(value = 1, message = "{product.quantity.min}")
    private int quantity;

}
