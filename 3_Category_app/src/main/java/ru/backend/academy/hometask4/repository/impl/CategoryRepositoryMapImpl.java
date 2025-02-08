package ru.backend.academy.hometask4.repository.impl;

import lombok.extern.slf4j.Slf4j;
import ru.backend.academy.hometask4.exception.not_found.CategoryNotFoundException;
import ru.backend.academy.hometask4.model.Category;
import ru.backend.academy.hometask4.repository.CategoryRepository;
import ru.backend.academy.hometask4.util.UniqueIdGenerator;

import java.util.*;
import java.util.concurrent.ConcurrentMap;

@Slf4j
public class CategoryRepositoryMapImpl implements CategoryRepository {

    private final ConcurrentMap<String, Category> concurrentMap;
    private final String defaultUUID = UniqueIdGenerator.generateUniqueId();
    private Category defaultCategory = new Category(defaultUUID, "default", "default");


    public CategoryRepositoryMapImpl(ConcurrentMap<String, Category> concurrentMap) {
        this.concurrentMap = concurrentMap;
        concurrentMap.put(defaultUUID, defaultCategory);
    }

    @Override
    public List<Category> getAllCategories() {
        return new ArrayList<>(concurrentMap.values());
    }

    @Override
    public Category addNewCategory(Category category) {
        String id = createNewUniqueId();
        checkUniqueUrlAndTitle(category);
        category.setId(id);
        concurrentMap.put(id, category);
        return concurrentMap.get(id);
    }

    @Override
    public Category updateCategoryById(String id, Category category) {
        if (concurrentMap.containsKey(id)) {
            checkUniqueUrlAndTitle(category);
            concurrentMap.put(id, category);
            return concurrentMap.get(id);
        } else {
            throw new CategoryNotFoundException(id);
        }
    }

    @Override
    public void deleteCategoryById(String categoryId) {
        if (concurrentMap.containsKey(categoryId)) {
            concurrentMap.remove(categoryId);
        } else {
            log.error("Error while deleting category with id: {}", categoryId);
            throw new CategoryNotFoundException(categoryId);
        }
    }

    @Override
    public boolean existsCategoryWithId(String categoryId) {
        return concurrentMap.containsKey(categoryId);
    }

    @Override
    public String getDefaultCategoryId() {
        return defaultUUID;
    }

    @Override
    public Category findCategoryById(String id) {
        return concurrentMap.values().stream().filter(x -> id.equals(x.getId())).findFirst().orElseThrow(() -> new CategoryNotFoundException(id));
    }

    @Override
    public Optional<Category> findCategoryByUrl(String url) {
        return concurrentMap.values().stream().filter(x -> url.equals(x.getUrl())).findFirst();
    }

}
