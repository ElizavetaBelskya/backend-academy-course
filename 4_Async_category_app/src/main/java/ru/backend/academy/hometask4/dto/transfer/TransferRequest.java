package ru.backend.academy.hometask4.dto.transfer;

import lombok.Data;

@Data
public class TransferRequest {

    private String sourceCategoryId;

    private String targetCategoryId;

}
