package ru.backend.academy.hometask3.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ResourceBundleMessageSource;
import ru.backend.academy.hometask3.repository.ProductRepository;
import ru.backend.academy.hometask3.repository.impl.ProductRepositoryCSVFileImpl;
import ru.backend.academy.hometask3.repository.impl.ProductRepositoryHashMapImpl;


@Configuration
@PropertySource("classpath:application.properties")
public class ApplicationConfig {

    @Value("${product.repository.impl}")
    private String repositoryImplOption;

    @Bean
    @Qualifier("productRepository")
    public ProductRepository productRepository() {
        if ("file".equals(repositoryImplOption)) {
            return new ProductRepositoryCSVFileImpl();
        } else if ("ram".equals(repositoryImplOption)) {
            return new ProductRepositoryHashMapImpl();
        } else {
            throw new IllegalArgumentException("Invalid product.repository.impl value: use correct options - ram or file");
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


}
