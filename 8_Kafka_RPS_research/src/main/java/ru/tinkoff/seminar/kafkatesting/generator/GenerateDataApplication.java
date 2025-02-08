package ru.tinkoff.seminar.kafkatesting.generator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
		"ru.tinkoff.seminar.kafkatesting.common.repository",
		"ru.tinkoff.seminar.kafkatesting.generator",
		"ru.tinkoff.seminar.kafkatesting.common.model"
})
@EnableJpaRepositories(basePackages = "ru.tinkoff.seminar.kafkatesting.common.repository")
@EntityScan(basePackages = "ru.tinkoff.seminar.kafkatesting.common.model")
public class GenerateDataApplication {
	public static void main(String[] args) {
		SpringApplication.run(GenerateDataApplication.class, args);
	}

}
