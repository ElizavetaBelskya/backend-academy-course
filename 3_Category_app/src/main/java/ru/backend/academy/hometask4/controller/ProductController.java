package ru.backend.academy.hometask4.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.backend.academy.hometask4.dto.UpdateProductDto;
import ru.backend.academy.hometask4.dto.ProductDto;
import ru.backend.academy.hometask4.service.ProductService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/product")
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        return ResponseEntity.ok(productService.readAllProducts());
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductDto>> getAllProductsByCategory(@PathVariable String categoryId) {
        return ResponseEntity.ok(productService.getProductsByCategoryId(categoryId));
    }

    @PostMapping("/product")
    public ResponseEntity<ProductDto> addProduct(@RequestBody @Valid ProductDto productDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createNewProduct(productDto));
    }

    @PutMapping("/product/{itemNumber}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable String itemNumber, @RequestBody @Valid UpdateProductDto productDto) {
        return ResponseEntity.accepted().body(productService.updateProduct(itemNumber, productDto));
    }

    @DeleteMapping("/product/{itemNumber}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String itemNumber) {
        productService.deleteProduct(itemNumber);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
