package ru.backend.academy.hometask4.dto.product;


import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class UpdateProductDto {

    @NotBlank(message = "{product.title.notBlank}")
    private String title;

    @NotNull(message = "{product.price.notNull}")
    @DecimalMin(value = "1.0", message = "{product.price.min}")
    private float price;

    @NotNull(message = "{product.quantity.notNull}")
    @Min(value = 1, message = "{product.quantity.min}")
    private int quantity;

    private String categoryId;

}
