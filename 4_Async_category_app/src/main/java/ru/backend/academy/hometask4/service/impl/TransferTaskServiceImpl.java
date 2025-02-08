package ru.backend.academy.hometask4.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.backend.academy.hometask4.exception.not_found.CategoryNotFoundException;
import ru.backend.academy.hometask4.exception.not_found.TransferTaskNotFoundException;
import ru.backend.academy.hometask4.model.Category;
import ru.backend.academy.hometask4.model.TaskStatus;
import ru.backend.academy.hometask4.model.TransferTask;
import ru.backend.academy.hometask4.repository.CategoryRepository;
import ru.backend.academy.hometask4.repository.TransferTaskRepository;
import ru.backend.academy.hometask4.service.TransferTaskService;

@Service
@RequiredArgsConstructor
public class TransferTaskServiceImpl implements TransferTaskService {

    private final TransferTaskRepository taskRepository;

    private final CategoryRepository categoryRepository;

    private final AsyncTransferTaskService asyncTransferTaskService;

    public Long createTransferTask(String sourceCategoryId, String targetCategoryId) {
        Category sourceCategory = categoryRepository.findById(sourceCategoryId).orElseThrow(() -> new CategoryNotFoundException(sourceCategoryId));
        Category targetCategory = categoryRepository.findById(targetCategoryId).orElseThrow(() -> new CategoryNotFoundException(targetCategoryId));
        TransferTask transferTask = TransferTask.builder()
                .sourceCategory(sourceCategory)
                .targetCategory(targetCategory)
                .taskStatus(TaskStatus.WAITING).build();
        TransferTask savedTask = taskRepository.save(transferTask);
        asyncTransferTaskService.performTransfer(savedTask);
        return savedTask.getTaskId();
    }

    public TransferTask getTaskById(Long taskId) {
        return taskRepository.findById(taskId).orElseThrow(() -> new TransferTaskNotFoundException(taskId));
    }


}
