package ru.backend.academy.hometask3.repository.impl;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import ru.backend.academy.hometask3.exception.ProductAlreadyExistsException;
import ru.backend.academy.hometask3.exception.ProductNotFoundException;
import ru.backend.academy.hometask3.model.Product;
import ru.backend.academy.hometask3.repository.ProductRepository;
import ru.backend.academy.hometask3.util.CsvUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ProductRepositoryCSVFileImpl implements ProductRepository {

    @Value("${csv.file.path}")
    private String pathToCsv;
    private CsvUtil csvUtil;

    @PostConstruct
    private void initialize() {
        if (pathToCsv == null || pathToCsv.isEmpty()) {
            throw new IllegalArgumentException("Error of creating bean without path of csv");
        }
        File dataFile = new File(pathToCsv);
        csvUtil = new CsvUtil(dataFile);
    }

    @Override
    public List<Product> getAllProducts() {
        List<String[]> lines = csvUtil.readAllLines();
        return lines.stream()
                .map(this::fromCSVRow)
                .toList();
    }

    @Override
    public void deleteProductByItemNumber(String itemNumber) {
        List<String[]> lines = csvUtil.deleteLine(itemNumber);
        csvUtil.writeAllLines(lines, false);
    }

    @Override
    public Product updateProductByItemNumber(Product product) throws ProductNotFoundException {
        String itemNumber = product.getItemNumber();
        List<String[]> lines = csvUtil.deleteLine(itemNumber);
        lines.add(toCSVRow(product));
        csvUtil.writeAllLines(lines, false);
        return product;
    }

    @Override
    public Product addNewProduct(Product product) throws ProductAlreadyExistsException {
        if (csvUtil.lineExists(product.getItemNumber())) {
            throw new ProductAlreadyExistsException();
        }
        String[] productRow = toCSVRow(product);
        List<String[]> productList = new ArrayList<>();
        productList.add(productRow);
        csvUtil.writeAllLines(productList, true);
        return product;
    }

    private String[] toCSVRow(Product product) {
        return new String[] {
                product.getItemNumber(),
                product.getTitle(),
                String.valueOf(product.getPrice()),
                String.valueOf(product.getQuantity())
        };
    }

    private Product fromCSVRow(String[] arr) {
        if (arr.length != 4) {
            log.error("Invalid array length for converting to Product, line {}", (Object) arr);
            throw new IllegalArgumentException("Invalid array length for converting to Product.");
        }
        try {
            String itemNumber = arr[0];
            String title = arr[1];
            float price = Float.parseFloat(arr[2]);
            int quantity = Integer.parseInt(arr[3]);
            return new Product(itemNumber, title, price, quantity);
        } catch (NumberFormatException e) {
            log.error("Invalid format for price or quantity: ", e);
            throw new IllegalArgumentException("Invalid format for price or quantity.");
        }
    }


}
