package ru.backend.academy.hometask4.repository;

import org.springframework.stereotype.Repository;
import ru.backend.academy.hometask4.exception.already_exists.CategoryAlreadyExistsException;
import ru.backend.academy.hometask4.model.Category;
import ru.backend.academy.hometask4.util.UniqueIdGenerator;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository {
    List<Category> getAllCategories();

    Category addNewCategory(Category category);

    Category updateCategoryById(String categoryId, Category category);

    void deleteCategoryById(String categoryId);
    boolean existsCategoryWithId(String categoryId);

    Optional<Category> findCategoryByUrl(String url);

    default String createNewUniqueId() {
        return UniqueIdGenerator.generateUniqueId();
    }

    default void checkUniqueUrlAndTitle(Category category) {
        String url = category.getUrl();
        Optional<Category> sameCategory = findCategoryByUrl(url);
        if (sameCategory.isPresent()) {
            if (category.getTitle().equals(sameCategory.get().getTitle())) {
                throw new CategoryAlreadyExistsException();
            } else {
                category.setUrl(category.getUrl() + "_" + category.hashCode());
            }
        }
    }

    Category findCategoryById(String categoryId);

    String getDefaultCategoryId();
}
