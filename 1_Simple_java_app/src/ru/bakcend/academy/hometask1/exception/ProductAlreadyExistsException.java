package ru.bakcend.academy.hometask1.exception;

import ru.bakcend.academy.hometask1.model.Product;

public class ProductAlreadyExistsException extends Exception {

    private final Product duplicateProduct;

    private static final String ALREADY_EXISTS_MESSAGE = "Товар с этим артикулом уже существует";

    public ProductAlreadyExistsException(Product duplicateProduct) {
        super(ALREADY_EXISTS_MESSAGE);
        this.duplicateProduct = duplicateProduct;
    }

    public Product getDuplicateProduct() {
        return duplicateProduct;
    }

}
