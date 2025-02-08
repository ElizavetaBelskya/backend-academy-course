package ru.backend.academy.hometask4.repository.jpa;

import org.springframework.context.annotation.Conditional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.backend.academy.hometask4.config.DatabaseCondition;
import ru.backend.academy.hometask4.db_model.Category;
import ru.backend.academy.hometask4.db_model.Product;

import java.util.List;

@Repository
@Conditional(DatabaseCondition.class)
public interface ProductJpaRepository extends JpaRepository<Product, String> {

    List<Product> findAllByCategory(Category category);



}
