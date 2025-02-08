package ru.backend.academy.hometask4.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.backend.academy.hometask4.model.TransferTask;

@Repository
public interface TransferTaskRepository extends JpaRepository<TransferTask, Long> {
}
