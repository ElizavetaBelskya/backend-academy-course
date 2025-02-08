package ru.backend.academy.hometask4.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.backend.academy.hometask4.dto.transfer.TransferRequest;
import ru.backend.academy.hometask4.dto.transfer.TransferTaskIdResponse;
import ru.backend.academy.hometask4.dto.transfer.TransferTaskStatusResponse;
import ru.backend.academy.hometask4.model.TransferTask;
import ru.backend.academy.hometask4.service.TransferTaskService;

@RestController
@RequiredArgsConstructor
public class TransferTaskController {

    private final TransferTaskService taskService;

    @PostMapping("/move-products-task")
    public ResponseEntity<TransferTaskIdResponse> createTransferTask(@RequestBody TransferRequest request) {
        String sourceCategoryId = request.getSourceCategoryId();
        String targetCategoryId = request.getTargetCategoryId();
        Long taskId = taskService.createTransferTask(sourceCategoryId, targetCategoryId);
        TransferTaskIdResponse response = new TransferTaskIdResponse(taskId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/task-status")
    public ResponseEntity<TransferTaskStatusResponse> getTaskStatus(@RequestParam("taskId") Long taskId) {
        TransferTask task = taskService.getTaskById(taskId);
        TransferTaskStatusResponse response = new TransferTaskStatusResponse(task.getTaskId(), String.valueOf(task.getTaskStatus()));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


}
