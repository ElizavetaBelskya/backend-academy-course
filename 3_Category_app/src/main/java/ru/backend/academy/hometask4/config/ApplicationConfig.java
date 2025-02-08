package ru.backend.academy.hometask4.config;

import com.ibm.icu.text.Transliterator;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ResourceBundleMessageSource;
import ru.backend.academy.hometask4.model.Category;
import ru.backend.academy.hometask4.model.Product;
import ru.backend.academy.hometask4.repository.CategoryRepository;
import ru.backend.academy.hometask4.repository.ProductRepository;
import ru.backend.academy.hometask4.repository.impl.CategoryRepositoryCsvFileImpl;
import ru.backend.academy.hometask4.repository.impl.CategoryRepositoryMapImpl;
import ru.backend.academy.hometask4.repository.impl.ProductRepositoryCsvFileImpl;
import ru.backend.academy.hometask4.repository.impl.ProductRepositoryMapImpl;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


@Configuration
@PropertySource("classpath:application.properties")
public class ApplicationConfig {

    private final static String CYRILLIC_TO_LATIN = "Russian-Latin/BGN";

    @Value("${product.repository.impl}")
    private String repositoryImplOption;

    @Value("${category.repository.impl}")
    private String categoryImplOption;

    @Bean
    @Qualifier("productRepository")
    @Conditional(NonDatabaseCondition.class)
    public ProductRepository productRepository() {
        if ("file".equals(repositoryImplOption)) {
            return new ProductRepositoryCsvFileImpl();
        } else if ("ram".equals(repositoryImplOption)) {
            return new ProductRepositoryMapImpl(productConcurrentMap());
        } else {
            throw new IllegalArgumentException("Invalid product.repository.impl value: use correct options - ram, file, database");
        }
    }

    @Bean
    @Qualifier("categoryRepository")
    @Conditional(NonDatabaseCondition.class)
    public CategoryRepository categoryRepository() {
        if ("file".equals(categoryImplOption)) {
            return new CategoryRepositoryCsvFileImpl();
        } else if ("ram".equals(categoryImplOption)) {
            return new CategoryRepositoryMapImpl(categoryConcurrentMap());
        } else {
            throw new IllegalArgumentException("Invalid category.repository.impl value: use correct options - ram, file, database");
        }
    }

    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setUseCodeAsDefaultMessage(false);
        return messageSource;
    }

    @Bean
    public Transliterator transliterator() {
        return Transliterator.getInstance(CYRILLIC_TO_LATIN);
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    @Conditional(NonDatabaseCondition.class)
    public ConcurrentMap<String, Category> categoryConcurrentMap() {
        return new ConcurrentHashMap<>();
    }

    @Bean
    @Conditional(NonDatabaseCondition.class)
    public ConcurrentMap<String, Product> productConcurrentMap() {
        return new ConcurrentHashMap<>();
    }


}
