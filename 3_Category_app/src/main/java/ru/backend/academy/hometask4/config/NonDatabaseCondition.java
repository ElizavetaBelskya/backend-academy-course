package ru.backend.academy.hometask4.config;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.env.Environment;

public class NonDatabaseCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Environment environment = context.getEnvironment();
        String productProperty = environment.getProperty("product.repository.impl");
        String categoryProperty = environment.getProperty("category.repository.impl");
        return !"database".equalsIgnoreCase(productProperty) && !"database".equalsIgnoreCase(categoryProperty);
    }

}
