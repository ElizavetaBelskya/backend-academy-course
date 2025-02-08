package ru.backend.academy.hometask4.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.backend.academy.hometask4.converter.ProductConverter;
import ru.backend.academy.hometask4.dto.ProductDto;
import ru.backend.academy.hometask4.dto.UpdateProductDto;
import ru.backend.academy.hometask4.exception.not_found.CategoryNotFoundException;
import ru.backend.academy.hometask4.exception.not_found.ProductNotFoundException;
import ru.backend.academy.hometask4.model.Category;
import ru.backend.academy.hometask4.model.Product;
import ru.backend.academy.hometask4.repository.CategoryRepository;
import ru.backend.academy.hometask4.repository.ProductRepository;
import ru.backend.academy.hometask4.service.impl.ProductServiceImpl;
import ru.backend.academy.hometask4.util.UniqueIdGenerator;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.lang.String;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductConverter productConverter;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private ProductDto productDto;

    private Product product;

    @BeforeEach
    void setUp() {
        productDto = new ProductDto("ITEM1", "Product 1", 10.0f, 5, "category_1");
        product = new Product("ITEM1", "Product 1", 10.0f, 5, "category_1");
    }


    @Test
    public void add_new_product_should_add_product_and_return_correct_product_dto() {
        when(productConverter.convertToEntity(productDto)).thenReturn(product);
        when(categoryRepository.findCategoryByUrl(product.getCategoryId())).thenReturn(Optional.of(Category.builder().build()));
        when(productRepository.addNewProduct(product)).thenReturn(product);
        when(productConverter.convertToDto(product)).thenReturn(productDto);
        ProductDto result = productService.createNewProduct(productDto);
        assertEquals(productDto, result);
        verify(productRepository).addNewProduct(product);
    }

    @Test
    public void add_new_product_should_add_product_with_default_category() {
        productDto.setCategoryId(null);
        product.setCategoryId(null);
        when(categoryRepository.getDefaultCategoryId()).thenReturn("default");
        when(productConverter.convertToEntity(productDto)).thenReturn(product);
        when(productRepository.addNewProduct(product)).thenReturn(product);
        when(productConverter.convertToDto(product)).thenReturn(productDto);
        ProductDto result = productService.createNewProduct(productDto);
        assertEquals(productDto, result);
        verify(productRepository).addNewProduct(product);
    }

    @Test
    public void add_new_product_should_throw_not_found_exception_when_category_is_incorrect() {
        when(productConverter.convertToEntity(productDto)).thenReturn(product);
        when(categoryRepository.findCategoryByUrl(productDto.getCategoryId())).thenReturn(Optional.empty());
        assertThrows(CategoryNotFoundException.class, () -> productService.createNewProduct(productDto));
        verify(productRepository, never()).addNewProduct(product);
    }

    @Test
    public void get_all_products_should_return_correct_products_list() {
        List<Product> products = Collections.singletonList(product);
        List<ProductDto> productDtos = Collections.singletonList(productDto);
        when(productRepository.getAllProducts()).thenReturn(products);
        when(productConverter.convertToDto(product)).thenReturn(productDto);
        List<ProductDto> result = productService.readAllProducts();
        assertEquals(productDtos, result);
        verify(productRepository).getAllProducts();
    }

    @Test
    public void get_all_products_should_return_empty_list() {
        when(productRepository.getAllProducts()).thenReturn(Collections.emptyList());
        List<ProductDto> result = productService.readAllProducts();
        assertEquals(Collections.emptyList(), result);
        verify(productRepository).getAllProducts();
    }

    @Test
    public void delete_product_should_delete_product_correctly() {
        String id = "ITEM1";
        productService.deleteProduct(id);
        verify(productRepository).deleteProductByItemNumber(id);
    }

    @Test
    public void delete_product_should_throw_product_not_found_exception() {
        String id = "ITEM1";
        doThrow(new ProductNotFoundException(id)).when(productRepository).deleteProductByItemNumber(id);
        assertThrows(RuntimeException.class, () -> productService.deleteProduct(id));
        verify(productRepository).deleteProductByItemNumber(id);
    }

    @Test
    public void update_product_should_update_product_and_return_correct_product_dto() {
        UpdateProductDto updateProductDto = new UpdateProductDto( "Product 1", 10.0f, 5, "category");
        when(productConverter.convertToEntity(updateProductDto)).thenReturn(product);
        when(categoryRepository.findCategoryByUrl(product.getCategoryId())).thenReturn(Optional.of(Category.builder().build()));
        when(productRepository.updateProductByItemNumber(product)).thenReturn(product);
        when(productConverter.convertToDto(product)).thenReturn(productDto);
        ProductDto result = productService.updateProduct(product.getItemNumber(), updateProductDto);
        assertEquals(productDto, result);
        verify(productRepository).updateProductByItemNumber(product);
    }

    @Test
    public void update_product_should_update_product_with_default_category() {
        UpdateProductDto updateProductDto = new UpdateProductDto( "Product 1", 10.0f, 5, null);
        product.setCategoryId(null);
        when(categoryRepository.getDefaultCategoryId()).thenReturn("default");
        when(productConverter.convertToEntity(updateProductDto)).thenReturn(product);
        when(productRepository.updateProductByItemNumber(product)).thenReturn(product);
        when(productConverter.convertToDto(product)).thenReturn(productDto);
        ProductDto result = productService.updateProduct(product.getItemNumber(), updateProductDto);
        assertEquals(productDto, result);
        verify(productRepository).updateProductByItemNumber(product);
    }

    @Test
    public void update_product_should_throw_not_found_exception_when_product_id_incorrect() {
        UpdateProductDto updateProductDto = new UpdateProductDto( "Product 1", 10.0f, 5, "category_1");
        when(productConverter.convertToEntity(updateProductDto)).thenReturn(product);
        when(categoryRepository.findCategoryByUrl(product.getCategoryId())).thenReturn(Optional.of(Category.builder().build()));
        when(productRepository.updateProductByItemNumber(product)).thenThrow(ProductNotFoundException.class);
        assertThrows(ProductNotFoundException.class, () -> productService.updateProduct(UniqueIdGenerator.generateUniqueId(), updateProductDto));
        verify(productRepository).updateProductByItemNumber(product);
    }

    @Test
    public void get_all_products_by_category_id_should_return_correct_list() {
        String categoryId = UniqueIdGenerator.generateUniqueId();
        String categoryUrl = "categoryUrl";
        Category category = new Category(UniqueIdGenerator.generateUniqueId(), categoryUrl, "title");
        Product product = new Product("itemNumber", "title", 1.0f, 1, categoryUrl);
        ProductDto productDto = new ProductDto("itemNumber", "title", 1.0f, 1, categoryUrl);
        when(categoryRepository.findCategoryById(categoryId)).thenReturn(category);
        when(productRepository.findAllByCategoryUrl(categoryUrl)).thenReturn(List.of(product));
        when(productConverter.convertToDto(product)).thenReturn(productDto);
        List<ProductDto> result = productService.getProductsByCategoryId(categoryId);
        assertEquals(List.of(productDto), result);
    }

    @Test
    public void get_all_products_by_category_id_should_return_empty_list_when_no_products_with_category() {
        String categoryId = UniqueIdGenerator.generateUniqueId();
        String categoryUrl = "categoryUrl";
        Category category = new Category(UniqueIdGenerator.generateUniqueId(), categoryUrl, "title");
        when(categoryRepository.findCategoryById(categoryId)).thenReturn(category);
        when(productRepository.findAllByCategoryUrl(categoryUrl)).thenReturn(Collections.emptyList());
        List<ProductDto> result = productService.getProductsByCategoryId(categoryId);
        assertEquals(Collections.emptyList(), result);
    }

    @Test
    public void get_all_products_by_category_id_should_throw_exception_when_category_is_non_existent() {
        String categoryId = UniqueIdGenerator.generateUniqueId();
        when(categoryRepository.findCategoryById(categoryId)).thenThrow(CategoryNotFoundException.class);
        assertThrows(CategoryNotFoundException.class, () -> productService.getProductsByCategoryId(categoryId));
    }


}