package ru.bakcend.academy.hometask1.service.impl;

import ru.bakcend.academy.hometask1.model.Product;
import ru.bakcend.academy.hometask1.dto.NewOrUpdateProductDto;
import ru.bakcend.academy.hometask1.dto.ProductDto;
import ru.bakcend.academy.hometask1.exception.ProductAlreadyExistsException;
import ru.bakcend.academy.hometask1.exception.ProductNotFoundException;
import ru.bakcend.academy.hometask1.repository.ProductRepository;
import ru.bakcend.academy.hometask1.service.ProductService;

import java.util.List;
import java.util.stream.Collectors;

public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public ProductDto createNewProduct(NewOrUpdateProductDto productDto) throws ProductAlreadyExistsException {
        return productRepository.addNewProduct(productDto.toProduct()).toProductDto();
    }

    @Override
    public List<ProductDto> readAllProducts() {
        return productRepository.getAllProducts().stream().map(Product::toProductDto).collect(Collectors.toList());
    }

    @Override
    public ProductDto updateProduct(NewOrUpdateProductDto productDto) throws ProductNotFoundException {
        Product productToUpdate = productDto.toProduct();
        return productRepository.updateProductByItemNumber(productToUpdate).toProductDto();
    }

    @Override
    public boolean deleteProduct(String itemNumber) throws ProductNotFoundException {
        return productRepository.deleteProductByItemNumber(itemNumber);
    }

}
