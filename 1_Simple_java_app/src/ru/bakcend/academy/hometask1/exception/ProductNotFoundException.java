package ru.bakcend.academy.hometask1.exception;

public class ProductNotFoundException extends Exception {

    private final String itemNumber;

    private static final String PRODUCT_NOT_FOUND_MESSAGE = "Товар с данным артикулом не найден";

    public ProductNotFoundException(String itemNumber) {
        super(PRODUCT_NOT_FOUND_MESSAGE);
        this.itemNumber = itemNumber;
    }

    public String getItemNumber() {
        return itemNumber;
    }

}
