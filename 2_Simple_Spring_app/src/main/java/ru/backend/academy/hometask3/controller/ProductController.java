package ru.backend.academy.hometask3.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.backend.academy.hometask3.dto.UpdateProductDto;
import ru.backend.academy.hometask3.dto.ProductDto;
import ru.backend.academy.hometask3.service.ProductService;

import java.util.List;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        return ResponseEntity.ok(productService.readAllProducts());
    }

    @PostMapping
    public ResponseEntity<ProductDto> addProduct(@RequestBody @Valid ProductDto productDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createNewProduct(productDto));
    }

    @PutMapping("/{itemNumber}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable String itemNumber, @RequestBody @Valid UpdateProductDto productDto) {
        return ResponseEntity.accepted().body(productService.updateProduct(itemNumber, productDto));
    }

    @DeleteMapping("/{itemNumber}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String itemNumber) {
        productService.deleteProduct(itemNumber);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
