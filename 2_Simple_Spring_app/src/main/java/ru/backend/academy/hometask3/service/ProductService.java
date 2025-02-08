package ru.backend.academy.hometask3.service;


import ru.backend.academy.hometask3.dto.ProductDto;
import ru.backend.academy.hometask3.dto.UpdateProductDto;

import java.util.List;

public interface ProductService {

    ProductDto createNewProduct(ProductDto productDto);

    List<ProductDto> readAllProducts();

    ProductDto updateProduct(String itemNumber, UpdateProductDto productDto);

    void deleteProduct(String itemNumber);

}
