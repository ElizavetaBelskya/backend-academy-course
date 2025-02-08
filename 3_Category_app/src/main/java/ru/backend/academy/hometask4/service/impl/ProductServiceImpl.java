package ru.backend.academy.hometask4.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;
import ru.backend.academy.hometask4.config.NonDatabaseCondition;
import ru.backend.academy.hometask4.dto.UpdateProductDto;
import ru.backend.academy.hometask4.dto.ProductDto;
import ru.backend.academy.hometask4.converter.ProductConverter;
import ru.backend.academy.hometask4.exception.not_found.CategoryNotFoundException;
import ru.backend.academy.hometask4.model.Product;
import ru.backend.academy.hometask4.repository.CategoryRepository;
import ru.backend.academy.hometask4.repository.ProductRepository;
import ru.backend.academy.hometask4.service.ProductService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Conditional(NonDatabaseCondition.class)
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    private final ProductConverter productConverter;

    private final CategoryRepository categoryRepository;

    @Override
    public ProductDto createNewProduct(ProductDto productDto) {
        Product newProduct = productConverter.convertToEntity(productDto);
        if (productDto.getCategoryId() == null || productDto.getCategoryId().isBlank()) {
            newProduct.setCategoryId(categoryRepository.getDefaultCategoryId());
        } else if (categoryRepository.findCategoryByUrl(newProduct.getCategoryId()).isEmpty()) {
            throw new CategoryNotFoundException(newProduct.getCategoryId());
        }
        return productConverter.convertToDto(productRepository.addNewProduct(newProduct));
    }

    @Override
    public List<ProductDto> readAllProducts() {
        return productRepository.getAllProducts().stream().map(productConverter::convertToDto).toList();
    }

    @Override
    public ProductDto updateProduct(String itemNumber, UpdateProductDto productDto) {
        Product productToUpdate = productConverter.convertToEntity(productDto);
        productToUpdate.setItemNumber(itemNumber);
        if (productDto.getCategoryId() == null || productDto.getCategoryId().isBlank()) {
            productToUpdate.setCategoryId(categoryRepository.getDefaultCategoryId());
        } else if (categoryRepository.findCategoryByUrl(productToUpdate.getCategoryId()).isEmpty()) {
            throw new CategoryNotFoundException(productToUpdate.getCategoryId());
        }
        return productConverter.convertToDto(productRepository.updateProductByItemNumber(productToUpdate));
    }

    @Override
    public void deleteProduct(String itemNumber) {
        productRepository.deleteProductByItemNumber(itemNumber);
    }

    @Override
    public List<ProductDto> getProductsByCategoryId(String categoryId) {
        return productRepository.findAllByCategoryUrl(categoryRepository.findCategoryById(categoryId).getUrl()).stream().map(productConverter::convertToDto).toList();
    }


}
