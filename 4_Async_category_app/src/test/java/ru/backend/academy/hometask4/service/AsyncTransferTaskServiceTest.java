package ru.backend.academy.hometask4.service;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import ru.backend.academy.hometask4.model.Category;
import ru.backend.academy.hometask4.model.Product;
import ru.backend.academy.hometask4.model.TaskStatus;
import ru.backend.academy.hometask4.model.TransferTask;
import ru.backend.academy.hometask4.repository.ProductRepository;
import ru.backend.academy.hometask4.repository.TransferTaskRepository;
import ru.backend.academy.hometask4.service.impl.AsyncTransferTaskService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class AsyncTransferTaskServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private TransferTaskRepository taskRepository;

    @InjectMocks
    private AsyncTransferTaskService transferTaskService;

    @Test
    public void perform_transfer_should_change_task_status_twice() throws Exception {
        TransferTask transferTask = new TransferTask();
        Category sourceCategory = new Category();
        Category targetCategory = new Category();
        List<Product> productsToMove = List.of(Product.builder().itemNumber("1").category(sourceCategory).build(),
                Product.builder().itemNumber("2").category(sourceCategory).build());
        transferTask.setSourceCategory(sourceCategory);
        transferTask.setTargetCategory(targetCategory);

        TransferTask transferTaskSpy = spy(transferTask);

        when(productRepository.findAllByCategory(sourceCategory)).thenReturn(productsToMove);
        transferTaskService.performTransfer(transferTaskSpy);

        assertEquals(TaskStatus.DONE, transferTaskSpy.getTaskStatus());
        verify(productRepository).saveAll(productsToMove);
        verify(productRepository).findAllByCategory(sourceCategory);
        verify(transferTaskSpy, times(1)).setTaskStatus(TaskStatus.IN_PROGRESS);
        verify(transferTaskSpy, times(1)).setTaskStatus(TaskStatus.DONE);
    }

    @Test
    public void perform_transfer_should_change_status_to_error_when_exception_occurred() throws Exception {
        TransferTask transferTask = new TransferTask();
        Category sourceCategory = new Category();
        Category targetCategory = new Category();
        List<Product> productsToMove = List.of(Product.builder().itemNumber("1").category(sourceCategory).build(),
                Product.builder().itemNumber("2").category(sourceCategory).build());
        transferTask.setSourceCategory(sourceCategory);
        transferTask.setTargetCategory(targetCategory);
        TransferTask transferTaskSpy = spy(transferTask);

        when(productRepository.saveAll(anyList())).thenThrow(new DataAccessException("Database error") {});
        when(taskRepository.save(transferTaskSpy)).thenReturn(transferTaskSpy);

        transferTaskService.performTransfer(transferTaskSpy);
        assertEquals(TaskStatus.ERROR, transferTaskSpy.getTaskStatus());
        verify(productRepository, times(1)).findAllByCategory(sourceCategory);
        verify(transferTaskSpy, times(1)).setTaskStatus(TaskStatus.IN_PROGRESS);
        verify(transferTaskSpy, times(1)).setTaskStatus(TaskStatus.ERROR);
    }

}
