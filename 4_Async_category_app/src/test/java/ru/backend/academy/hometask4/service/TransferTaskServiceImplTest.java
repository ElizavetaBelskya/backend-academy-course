package ru.backend.academy.hometask4.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.backend.academy.hometask4.exception.not_found.CategoryNotFoundException;
import ru.backend.academy.hometask4.exception.not_found.TransferTaskNotFoundException;
import ru.backend.academy.hometask4.model.Category;
import ru.backend.academy.hometask4.model.TaskStatus;
import ru.backend.academy.hometask4.model.TransferTask;
import ru.backend.academy.hometask4.repository.CategoryRepository;
import ru.backend.academy.hometask4.repository.TransferTaskRepository;
import ru.backend.academy.hometask4.service.impl.AsyncTransferTaskService;
import ru.backend.academy.hometask4.service.impl.TransferTaskServiceImpl;

import java.util.Optional;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;



@ExtendWith(MockitoExtension.class)
public class TransferTaskServiceImplTest {

    @Mock
    private TransferTaskRepository taskRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private AsyncTransferTaskService asyncTransferTaskService;

    @InjectMocks
    private TransferTaskServiceImpl transferTaskService;

    private Category sourceCategory;
    private Category targetCategory;
    private TransferTask transferTask;

    @BeforeEach
    public void setUp() {
        sourceCategory = new Category("SOURCEID", "source", "source");
        targetCategory = new Category("TARGETID", "target", "target");
        transferTask = TransferTask.builder()
                .taskId(1L)
                .sourceCategory(sourceCategory)
                .targetCategory(targetCategory)
                .taskStatus(TaskStatus.WAITING)
                .build();
    }

    @Test
    public void get_task_by_id_when_task_exists_then_return_task() {
        Long taskId = 1L;
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(transferTask));
        TransferTask foundTask = transferTaskService.getTaskById(taskId);
        assertEquals(transferTask, foundTask);
    }

    @Test
    public void get_task_by_id_when_task_does_not_exist_then_throw_exception() {
        Long taskId = 1L;
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());
        assertThrows(TransferTaskNotFoundException.class, () -> transferTaskService.getTaskById(taskId));
    }

    @Test
    public void create_transfer_task_when_categories_exist_then_task_created() {
        when(categoryRepository.findById("SOURCEID")).thenReturn(Optional.of(sourceCategory));
        when(categoryRepository.findById("TARGETID")).thenReturn(Optional.of(targetCategory));
        when(taskRepository.save(any(TransferTask.class))).thenReturn(transferTask);
        Long taskId = transferTaskService.createTransferTask("SOURCEID", "TARGETID");
        assertEquals(transferTask.getTaskId(), taskId);
    }

    @Test
    public void create_transfer_task_when_source_category_not_found_then_exception_thrown() {
        when(categoryRepository.findById("SOURCEID")).thenReturn(Optional.empty());
        assertThrows(CategoryNotFoundException.class, () -> transferTaskService.createTransferTask("SOURCEID", "TARGETID"));
        verify(taskRepository, times(0)).save(any(TransferTask.class));
    }

    @Test
    public void create_transfer_task_when_target_category_not_found_then_exception_thrown() {
        when(categoryRepository.findById("SOURCEID")).thenReturn(Optional.of(sourceCategory));
        when(categoryRepository.findById("TARGETID")).thenReturn(Optional.empty());
        assertThrows(CategoryNotFoundException.class, () -> transferTaskService.createTransferTask("SOURCEID", "TARGETID"));
        verify(taskRepository, times(0)).save(any(TransferTask.class));
    }


}