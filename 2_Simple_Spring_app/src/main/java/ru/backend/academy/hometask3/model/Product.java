package ru.backend.academy.hometask3.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    private String itemNumber;

    private String title;

    private float price;

    private int quantity;

}
