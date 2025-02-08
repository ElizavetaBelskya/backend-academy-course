package ru.backend.academy.hometask4.repository.impl;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import ru.backend.academy.hometask4.exception.not_found.CategoryNotFoundException;
import ru.backend.academy.hometask4.model.Category;
import ru.backend.academy.hometask4.repository.CategoryRepository;
import ru.backend.academy.hometask4.util.CsvUtil;
import ru.backend.academy.hometask4.util.UniqueIdGenerator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class CategoryRepositoryCsvFileImpl implements CategoryRepository {

    private final String defaultCategoryTitle = "default";
    private final String defaultId = UniqueIdGenerator.generateUniqueId();

    @Value("${csv.file.path.category}")
    private String pathToCsv;
    private CsvUtil csvUtil;

    @PostConstruct
    private void initialize() {
        if (pathToCsv == null || pathToCsv.isEmpty()) {
            throw new IllegalArgumentException("Error of creating bean without path of csv");
        }
        File dataFile = new File(pathToCsv);
        csvUtil = new CsvUtil(dataFile);
        addNewCategory(new Category(defaultId, defaultCategoryTitle, defaultCategoryTitle));
    }

    @Override
    public List<Category> getAllCategories() {
        List<String[]> lines = csvUtil.readAllLines();
        return lines.stream()
                .map(this::fromCSVRow)
                .toList();
    }

    @Override
    public Category addNewCategory(Category category) {
        String id = createNewUniqueId();
        checkUniqueUrlAndTitle(category);
        category.setId(id);
        List<String[]> newCategoryList = new ArrayList<>();
        newCategoryList.add(toCSVRow(category));
        csvUtil.writeAllLines(newCategoryList, true);
        return category;
    }

    @Override
    public Category updateCategoryById(String id, Category category) {
        List<String[]> lines = csvUtil.deleteLine(id);
        lines.add(toCSVRow(category));
        checkUniqueUrlAndTitle(category);
        csvUtil.writeAllLines(lines, false);
        return category;

    }

    @Override
    public void deleteCategoryById(String categoryId) {
        List<String[]> lines = csvUtil.deleteLine(categoryId);
        csvUtil.writeAllLines(lines, false);
    }

    @Override
    public boolean existsCategoryWithId(String categoryId) {
        return csvUtil.lineExists(categoryId);
    }

    @Override
    public String getDefaultCategoryId() {
        return defaultId;
    }

    @Override
    public Category findCategoryById(String categoryId) {
        String[] categoryLine = csvUtil.returnLineWithUniqueColumnValue(0, categoryId);
        if (categoryLine.length == 0) {
            throw new CategoryNotFoundException(categoryId);
        } else {
            return fromCSVRow(categoryLine);
        }
    }

    private String[] toCSVRow(Category category) {
        return new String[] {
                category.getId(),
                category.getTitle(),
                category.getUrl()
        };
    }

    private Category fromCSVRow(String[] arr) {
        if (arr.length != 3) {
            log.error("Invalid array length for converting to Category, line {}", (Object) arr);
            throw new IllegalArgumentException("Invalid array length for converting to Category.");
        }
        try {
            String id = arr[0];
            String title = arr[1];
            String url = arr[2];
            return new Category(id, url, title);
        } catch (IllegalArgumentException e) {
            log.error("Invalid format for uuid: ", e);
            throw new IllegalArgumentException("Invalid format for uuid.");
        }
    }

    @Override
    public Optional<Category> findCategoryByUrl(String url) {
        String[] arr = csvUtil.returnLineWithUniqueColumnValue(2, url);
        if (arr.length > 0) {
            return Optional.of(fromCSVRow(arr));
        } else {
            return Optional.empty();
        }
    }


}
