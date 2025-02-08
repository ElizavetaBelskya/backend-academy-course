package ru.bakcend.academy.hometask1.dto;

import ru.bakcend.academy.hometask1.model.Product;

import java.util.Objects;

public class NewOrUpdateProductDto {
    private String itemNumber;

    private String title;

    private float price;

    private int quantity;

    public NewOrUpdateProductDto(String itemNumber, String title, float price, int quantity) {
        this.itemNumber = itemNumber;
        this.title = title;
        this.price = price;
        this.quantity = quantity;
    }

    public String getItemNumber() {
        return itemNumber;
    }

    public void setItemNumber(String itemNumber) {
        this.itemNumber = itemNumber;
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
        return "NewOrUpdateProductDto{" +
                "itemNumber='" + itemNumber + '\'' +
                ", title='" + title + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NewOrUpdateProductDto that = (NewOrUpdateProductDto) o;
        return Float.compare(that.price, price) == 0 &&
                quantity == that.quantity &&
                Objects.equals(itemNumber, that.itemNumber) &&
                Objects.equals(title, that.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemNumber, title, price, quantity);
    }

    public Product toProduct() {
        return new Product(this.itemNumber, this.title, this.price, this.quantity);
    }

}
