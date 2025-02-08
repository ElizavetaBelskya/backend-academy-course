package ru.backend.academy.hometask4.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.backend.academy.hometask4.converter.GenericConverter;
import ru.backend.academy.hometask4.model.Category;
import ru.backend.academy.hometask4.model.Product;
import ru.backend.academy.hometask4.dto.product.ProductDto;
import ru.backend.academy.hometask4.dto.product.UpdateProductDto;
import ru.backend.academy.hometask4.exception.already_exists.ProductAlreadyExistsException;
import ru.backend.academy.hometask4.exception.not_found.CategoryNotFoundException;
import ru.backend.academy.hometask4.exception.not_found.ProductNotFoundException;
import ru.backend.academy.hometask4.repository.CategoryRepository;
import ru.backend.academy.hometask4.repository.ProductRepository;
import ru.backend.academy.hometask4.service.ProductService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    private final CategoryRepository categoryRepository;

    private final GenericConverter<Product, ProductDto> converter = new GenericConverter<>(Product.class, ProductDto.class);

    private final GenericConverter<Product, UpdateProductDto> updateConverter = new GenericConverter<>(Product.class, UpdateProductDto.class);

    @Override
    public ProductDto createNewProduct(ProductDto productDto) {
        Product newProduct = converter.convertToEntity(productDto);
        if (productRepository.findById(newProduct.getItemNumber()).isEmpty()) {
            if (productDto.getCategoryId() == null || productDto.getCategoryId().isBlank()) {
                newProduct.setCategory(categoryRepository.defaultCategory);
            } else if (categoryRepository.findById(productDto.getCategoryId()).isEmpty()) {
                throw new CategoryNotFoundException(productDto.getCategoryId());
            }
            return converter.convertToDto(productRepository.save(newProduct));
        } else {
            throw new ProductAlreadyExistsException();
        }
    }

    @Override
    public List<ProductDto> readAllProducts() {
        return productRepository.findAll().stream().map(converter::convertToDto).toList();
    }

    @Override
    public ProductDto updateProduct(String itemNumber, UpdateProductDto productDto) {
        if (productRepository.findById(itemNumber).isEmpty()) {
            throw new ProductNotFoundException(itemNumber);
        }
        Product productToUpdate = updateConverter.convertToEntity(productDto);
        productToUpdate.setItemNumber(itemNumber);
        if (productDto.getCategoryId() == null || productDto.getCategoryId().isBlank()) {
            productToUpdate.setCategory(categoryRepository.defaultCategory);
        } else if (categoryRepository.findById(productDto.getCategoryId()).isEmpty()) {
            throw new CategoryNotFoundException(productDto.getCategoryId());
        }
        return converter.convertToDto(productRepository.save(productToUpdate));
    }

    @Override
    public void deleteProduct(String itemNumber) {
        Product product = productRepository.findById(itemNumber).orElseThrow(() -> new ProductNotFoundException(itemNumber));
        productRepository.delete(product);
    }

    @Override
    public List<ProductDto> getProductsByCategoryId(String categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new CategoryNotFoundException(categoryId));
        return productRepository.findAllByCategory(category).stream().map(converter::convertToDto).toList();
    }
}
