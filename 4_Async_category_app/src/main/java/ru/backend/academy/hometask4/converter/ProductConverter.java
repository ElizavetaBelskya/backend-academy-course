package ru.backend.academy.hometask4.converter;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.backend.academy.hometask4.model.Product;
import ru.backend.academy.hometask4.dto.product.UpdateProductDto;
import ru.backend.academy.hometask4.dto.product.ProductDto;

@Component
public class ProductConverter {

    private final ModelMapper mapper;

    private ProductConverter() {
        mapper = new ModelMapper();
    }

    public ProductDto convertToDto(Product entity) {
        return mapper.map(entity, ProductDto.class);
    }

    public Product convertToEntity(UpdateProductDto productDto) {
        return mapper.map(productDto, Product.class);
    }

    public Product convertToEntity(ProductDto productDto) {
        return mapper.map(productDto, Product.class);
    }

}
