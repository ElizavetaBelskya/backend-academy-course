package ru.backend.academy.hometask4.exception.not_found;

import java.util.UUID;

public class CategoryNotFoundException extends BaseNotFoundException {

    private static final String CATEGORY_NOT_FOUND_MESSAGE = "Category with id %s not found";
    public CategoryNotFoundException(UUID categoryId) {
        super(String.format(CATEGORY_NOT_FOUND_MESSAGE, categoryId));
    }

    public CategoryNotFoundException(String categoryUrl) {
        super(String.format(CATEGORY_NOT_FOUND_MESSAGE, categoryUrl));
    }


}
