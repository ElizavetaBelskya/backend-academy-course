package ru.backend.academy.hometask4.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.backend.academy.hometask4.dto.CategoryDto;
import ru.backend.academy.hometask4.dto.NewOrUpdateCategoryDto;
import ru.backend.academy.hometask4.dto.ProductDto;
import ru.backend.academy.hometask4.service.CategoryService;
import ru.backend.academy.hometask4.service.ProductService;

import java.util.List;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryDto>> getAllCategories() {
        return ResponseEntity.ok(categoryService.readAllCategories());
    }

    @PostMapping
    public ResponseEntity<CategoryDto> addCategory(@RequestBody @Valid NewOrUpdateCategoryDto categoryDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.createNewCategory(categoryDto));
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable String categoryId, @RequestBody @Valid NewOrUpdateCategoryDto categoryDto) {
        return ResponseEntity.accepted().body(categoryService.updateCategory(categoryId, categoryDto));
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable String categoryId) {
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


}
