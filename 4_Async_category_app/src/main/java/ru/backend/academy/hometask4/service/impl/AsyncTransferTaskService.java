package ru.backend.academy.hometask4.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import ru.backend.academy.hometask4.model.Category;
import ru.backend.academy.hometask4.model.Product;
import ru.backend.academy.hometask4.model.TaskStatus;
import ru.backend.academy.hometask4.model.TransferTask;
import ru.backend.academy.hometask4.repository.ProductRepository;
import ru.backend.academy.hometask4.repository.TransferTaskRepository;

import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class AsyncTransferTaskService {

    private final TransactionTemplate transactionTemplate;

    private final TransferTaskRepository taskRepository;

    private final ProductRepository productRepository;

    @Async
    public void performTransfer(TransferTask transferTask) {
        Category sourceCategory = transferTask.getSourceCategory();
        Category targetCategory = transferTask.getTargetCategory();
        List<Product> productsToMove = productRepository.findAllByCategory(sourceCategory);
        transferTask.setTaskStatus(TaskStatus.IN_PROGRESS);
        taskRepository.save(transferTask);
//        transactionTemplate.executeWithoutResult(status -> {
            try {
                for (Product product : productsToMove) {
                    product.setCategory(targetCategory);
                }
                productRepository.saveAll(productsToMove);
                transferTask.setTaskStatus(TaskStatus.DONE);
            } catch (Exception e) {
//                status.setRollbackOnly();
                log.error("Exception occurred while async operation was doing", e);
                transferTask.setTaskStatus(TaskStatus.ERROR);
            } finally {
                taskRepository.save(transferTask);
            }
//        });
    }


}
