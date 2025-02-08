package ru.backend.academy.hometask3.service.impl;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.backend.academy.hometask3.dto.UpdateProductDto;
import ru.backend.academy.hometask3.dto.ProductDto;
import ru.backend.academy.hometask3.converter.ProductConverter;
import ru.backend.academy.hometask3.model.Product;
import ru.backend.academy.hometask3.repository.ProductRepository;
import ru.backend.academy.hometask3.service.ProductService;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductConverter productConverter;

    @Override
    public ProductDto createNewProduct(ProductDto productDto) {
        return productConverter.convertToDto(productRepository.addNewProduct(productConverter.convertToEntity(productDto)));
    }

    @Override
    public List<ProductDto> readAllProducts() {
        return productRepository.getAllProducts().stream().map(productConverter::convertToDto).toList();
    }

    @Override
    public ProductDto updateProduct(String itemNumber, UpdateProductDto productDto) {
        Product productToUpdate = productConverter.convertToEntity(productDto);
        productToUpdate.setItemNumber(itemNumber);
        return productConverter.convertToDto(productRepository.updateProductByItemNumber(productToUpdate));
    }

    @Override
    public void deleteProduct(String itemNumber) {
        productRepository.deleteProductByItemNumber(itemNumber);
    }

}
