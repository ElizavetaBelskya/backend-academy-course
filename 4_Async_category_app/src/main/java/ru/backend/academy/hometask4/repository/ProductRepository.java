package ru.backend.academy.hometask4.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.backend.academy.hometask4.model.Category;
import ru.backend.academy.hometask4.model.Product;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {

    List<Product> findAllByCategory(Category category);

}
