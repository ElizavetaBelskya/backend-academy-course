package ru.bakcend.academy.hometask1.model;

import ru.bakcend.academy.hometask1.dto.ProductDto;

import java.util.Objects;

public class Product {

    private final String itemNumber;

    private String title;

    private float price;

    private int quantity;

    public Product(String itemNumber, String title, float price, int quantity) {
        this.itemNumber = itemNumber;
        this.title = title;
        this.price = price;
        this.quantity = quantity;
    }

    public String getItemNumber() {
        return itemNumber;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return String.format("| %-10s | %-20s | %-10.2f | %-8d |%n", itemNumber, title, price, quantity);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Product product = (Product) o;
        return Objects.equals(itemNumber, product.itemNumber) &&
                Float.compare(product.price, price) == 0 &&
                quantity == product.quantity &&
                Objects.equals(title, product.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemNumber, title, price, quantity);
    }

    public ProductDto toProductDto() {
        return new ProductDto(this.itemNumber, this.title, this.price, this.quantity);
    }


}
