package ru.bakcend.academy.hometask1.service;

import ru.bakcend.academy.hometask1.dto.NewOrUpdateProductDto;
import ru.bakcend.academy.hometask1.dto.ProductDto;
import ru.bakcend.academy.hometask1.exception.ProductAlreadyExistsException;
import ru.bakcend.academy.hometask1.exception.ProductNotFoundException;

import java.util.List;

public interface ProductService {

    ProductDto createNewProduct(NewOrUpdateProductDto productDto) throws ProductAlreadyExistsException;

    List<ProductDto> readAllProducts();

    ProductDto updateProduct(NewOrUpdateProductDto productDto) throws ProductNotFoundException;

    boolean deleteProduct(String itemNumber) throws ProductNotFoundException;

}
