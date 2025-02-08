package ru.bakcend.academy.hometask1.util;

import ru.bakcend.academy.hometask1.dto.NewOrUpdateProductDto;
import ru.bakcend.academy.hometask1.dto.ProductDto;
import ru.bakcend.academy.hometask1.exception.ProductAlreadyExistsException;
import ru.bakcend.academy.hometask1.exception.ProductNotFoundException;
import ru.bakcend.academy.hometask1.service.ProductService;

import java.util.List;

import static ru.bakcend.academy.hometask1.util.validator.CommandValidator.*;

public class CommandProcessor {

    private static final String INVALID_COMMAND_MESSAGE = "Некорректная команда.";
    private static final String PRODUCT_NOT_FOUND_MESSAGE = "Товар с этим артикулом не существует.";
    private static final String PRODUCT_ALREADY_EXISTS_MESSAGE = "Товар с этим артикулом уже существует.";
    private static final String DATABASE_EMPTY_MESSAGE = "База данных пуста.";
    private static final String INVALID_UPDATE_COMMAND_MESSAGE = "Некорректный ввод команды update: update $артикул $название $цена $количество";
    private static final String INVALID_UPDATE_ARGUMENTS_MESSAGE = "Некорректные аргументы команды update: update $артикул $название $цена (дробное число) $количество (целое число)";
    private static final String INVALID_CREATE_COMMAND_MESSAGE = "Некорректный ввод команды create: create $артикул $название $цена $количество";
    private static final String INVALID_CREATE_ARGUMENTS_MESSAGE = "Некорректные аргументы create: create $артикул $название $цена (дробное число) $количество (целое число)";
    private static final String INVALID_ARTICLE_MESSAGE = "Некорректный артикул";
    private static final String PROBLEM_ON_DELETE_MESSAGE = "Возникли проблемы при удалении";
    private static final String PRODUCT_DELETED_MESSAGE = "Товар удален";
    private static final String PRODUCT_ADDED_MESSAGE = "Товар добавлен.";

    private static final String PRODUCT_UPDATED_MESSAGE = "Товар обновлен, предыдущее значение: \n";

    private static final String TABLE_SEPARATOR = "+------------+----------------------+------------+----------+\n";
    private static final String TABLE_HEADER = TABLE_SEPARATOR + "| Артикул    | Название             | Цена       | Кол-во   |\n" +TABLE_SEPARATOR;

    private final ProductService productService;

    public CommandProcessor(ProductService productService) {
        this.productService = productService;
    }

    public String processCommand(String command) {
        String[] parts = command.split(" ");
        String cmd = parts[0];

        switch (cmd) {
            case "create":
                return createProduct(parts);
            case "read":
                return readProducts();
            case "update":
                return updateProduct(parts);
            case "delete":
                return deleteProduct(parts);
            case "exit":
                return "exit";
            default:
                return INVALID_COMMAND_MESSAGE;
        }
    }

    private String updateProduct(String[] parts) {
        if (parts.length >= 5) {
            String id = parts[1];
            String name = extractProductName(parts);
            String priceStr = parts[parts.length - 2];
            String quantityStr = parts[parts.length - 1];
            if (isValidProductItemNumber(id) && name != null && isValidPrice(priceStr) && isValidQuantity(quantityStr)) {
                NewOrUpdateProductDto productDtoToUpdate = new NewOrUpdateProductDto(id, name, Float.parseFloat(priceStr), Integer.parseInt(quantityStr));
                try {
                    return PRODUCT_UPDATED_MESSAGE + productService.updateProduct(productDtoToUpdate);
                } catch (ProductNotFoundException e) {
                    return PRODUCT_NOT_FOUND_MESSAGE;
                }
            } else {
                return INVALID_UPDATE_ARGUMENTS_MESSAGE;
            }
        }
        return INVALID_UPDATE_COMMAND_MESSAGE;
    }

    private String deleteProduct(String[] parts) {
        if (parts.length >= 2 && isValidProductItemNumber(parts[1])) {
            try {
                if (productService.deleteProduct(parts[1])) {
                    return PRODUCT_DELETED_MESSAGE;
                } else {
                    return PROBLEM_ON_DELETE_MESSAGE;
                }
            } catch (ProductNotFoundException e) {
                return PRODUCT_NOT_FOUND_MESSAGE;
            }
        } else {
            return INVALID_ARTICLE_MESSAGE;
        }
    }

    private String readProducts() {
        List<ProductDto> products = productService.readAllProducts();
        if (products.isEmpty()) {
            return DATABASE_EMPTY_MESSAGE;
        }
        StringBuilder result = new StringBuilder();
        result.append(TABLE_HEADER);
        for (ProductDto product : products) {
            result.append(product.toString());
        }
        result.append(TABLE_SEPARATOR);
        return result.toString();
    }


    private String createProduct(String[] parts) {
        if (parts.length >= 5) {
            String itemNumber = parts[1];
            String name = extractProductName(parts);
            String priceStr = parts[parts.length - 2];
            String quantityStr = parts[parts.length - 1];

            if (isValidProductItemNumber(itemNumber) && name != null && isValidPrice(priceStr) && isValidQuantity(quantityStr)) {
                NewOrUpdateProductDto newProductDto = new NewOrUpdateProductDto(itemNumber, name, Float.parseFloat(priceStr), Integer.parseInt(quantityStr));
                try {
                    productService.createNewProduct(newProductDto);
                    return PRODUCT_ADDED_MESSAGE;
                } catch (ProductAlreadyExistsException e) {
                    return PRODUCT_ALREADY_EXISTS_MESSAGE;
                }
            } else {
                return INVALID_CREATE_ARGUMENTS_MESSAGE;
            }
        }
        return INVALID_CREATE_COMMAND_MESSAGE;
    }


}
