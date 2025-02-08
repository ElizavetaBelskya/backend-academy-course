package ru.backend.academy.hometask4.dto.transfer;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class TransferTaskStatusResponse {
    private Long taskId;
    private String taskStatus;
}
