package ru.tinkoff.seminar.kafkatesting.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.tinkoff.seminar.kafkatesting.common.model.Person;

import java.util.List;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {

    @Query("select p from Person p left join fetch p.children c left join fetch c.child where p.category = 'A'")
    List<Person> findA();


    @Query("select p from Person p left join fetch p.children c left join fetch c.child where p.category = 'B'")
    List<Person> findB();

}
