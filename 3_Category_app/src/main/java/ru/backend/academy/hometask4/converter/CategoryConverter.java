package ru.backend.academy.hometask4.converter;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.backend.academy.hometask4.dto.CategoryDto;
import ru.backend.academy.hometask4.dto.NewOrUpdateCategoryDto;
import ru.backend.academy.hometask4.model.Category;
import ru.backend.academy.hometask4.util.TransliterationUtil;

@Component
@RequiredArgsConstructor
public class CategoryConverter {

    private final ModelMapper mapper;
    private final TransliterationUtil transliterationUtil;

    public CategoryDto convertToDto(Category entity) {
        return mapper.map(entity, CategoryDto.class);
    }

    public CategoryDto convertToDto(ru.backend.academy.hometask4.db_model.Category entity) {
        return mapper.map(entity, CategoryDto.class);
    }

    public ru.backend.academy.hometask4.model.Category convertToNewEntity(NewOrUpdateCategoryDto categoryDto) {
        return Category.builder()
                .title(categoryDto.getTitle())
                .url(transliterationUtil.transliterate(categoryDto.getTitle()))
                .build();
    }

    public ru.backend.academy.hometask4.db_model.Category convertToNewDatabaseEntity(NewOrUpdateCategoryDto categoryDto) {
        return ru.backend.academy.hometask4.db_model.Category.builder()
                .title(categoryDto.getTitle())
                .url(transliterationUtil.transliterate(categoryDto.getTitle()))
                .build();
    }

}
