package ru.backend.academy.hometask4.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.backend.academy.hometask4.model.Category;
import ru.backend.academy.hometask4.util.UniqueIdGenerator;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {

    Category defaultCategory = new Category(UniqueIdGenerator.generateUniqueId(), "default", "default");

    Optional<Category> findByUrl(String url);

}
