package ru.backend.academy.hometask4.service;


import ru.backend.academy.hometask4.dto.product.ProductDto;
import ru.backend.academy.hometask4.dto.product.UpdateProductDto;

import java.util.List;

public interface ProductService {

    ProductDto createNewProduct(ProductDto productDto);

    List<ProductDto> readAllProducts();

    ProductDto updateProduct(String itemNumber, UpdateProductDto productDto);

    void deleteProduct(String itemNumber);

    List<ProductDto> getProductsByCategoryId(String categoryId);
}
