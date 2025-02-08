package ru.tinkoff.seminar.kafkatesting.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.tinkoff.seminar.kafkatesting.common.model.Child;

@Repository
public interface ChildRepository extends JpaRepository<Child, Long> {

}
