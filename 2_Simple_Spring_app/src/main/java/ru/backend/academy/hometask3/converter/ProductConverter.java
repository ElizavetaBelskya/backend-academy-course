package ru.backend.academy.hometask3.converter;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.backend.academy.hometask3.dto.UpdateProductDto;
import ru.backend.academy.hometask3.dto.ProductDto;
import ru.backend.academy.hometask3.model.Product;

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
