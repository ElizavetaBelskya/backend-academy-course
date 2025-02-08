package ru.tinkoff.belskya.hw7.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiConfig {
    private Info buildInfo() {
        return new Info().title("Education Platform API").version("1.1");
    }

    @Bean
    public OpenAPI customOpenApi() {
        return new OpenAPI().info(buildInfo());
    }

}
