package ru.backend.academy.hometask4.service;

import ru.backend.academy.hometask4.model.TransferTask;

public interface TransferTaskService {
    Long createTransferTask(String sourceCategoryId, String targetCategoryId);

    TransferTask getTaskById(Long taskId);
}
