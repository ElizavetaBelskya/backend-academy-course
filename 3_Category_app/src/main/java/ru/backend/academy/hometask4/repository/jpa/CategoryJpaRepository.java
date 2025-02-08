package ru.backend.academy.hometask4.repository.jpa;

import org.springframework.context.annotation.Conditional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.backend.academy.hometask4.config.DatabaseCondition;
import ru.backend.academy.hometask4.db_model.Category;
import ru.backend.academy.hometask4.util.UniqueIdGenerator;

import java.util.Optional;

@Repository
@Conditional(DatabaseCondition.class)
public interface CategoryJpaRepository extends JpaRepository<Category, String> {

    Category defaultCategory = new Category(UniqueIdGenerator.generateUniqueId(), "default", "default");

    Optional<Category> findByUrl(String url);

}
