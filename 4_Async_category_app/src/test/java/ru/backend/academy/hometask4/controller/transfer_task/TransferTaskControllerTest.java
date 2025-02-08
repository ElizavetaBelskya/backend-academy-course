package ru.backend.academy.hometask4.controller.transfer_task;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.backend.academy.hometask4.dto.transfer.TransferRequest;
import ru.backend.academy.hometask4.model.Category;
import ru.backend.academy.hometask4.repository.CategoryRepository;
import ru.backend.academy.hometask4.repository.TransferTaskRepository;
import ru.backend.academy.hometask4.util.UniqueIdGenerator;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ContextConfiguration(initializers = TransferTaskControllerTest.DataSourceInitializer.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class TransferTaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TransferTaskRepository transferTaskRepository;

    private ObjectMapper objectMapper;

    private Category testCategory;

    @Container
    private static final PostgreSQLContainer<?> database = new PostgreSQLContainer<>("postgres:12.9-alpine");

    public static class DataSourceInitializer
            implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
                    applicationContext,
                    "spring.datasource.url=" + database.getJdbcUrl(),
                    "spring.datasource.username=" + database.getUsername(),
                    "spring.datasource.password=" + database.getPassword(),
                    "spring.jpa.hibernate.ddl-auto=update"
            );
        }
    }

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        testCategory = new Category(UniqueIdGenerator.generateUniqueId(), "category1", "category1");
        categoryRepository.save(testCategory);
        categoryRepository.save(categoryRepository.defaultCategory);
    }

    @AfterEach
    void clean() {
        transferTaskRepository.deleteAll();
        categoryRepository.deleteAll();
    }


    @Test
    public void create_transfer_task_when_valid_request_then_return_created() throws Exception {
        TransferRequest request = new TransferRequest();
        request.setSourceCategoryId(testCategory.getId());
        request.setTargetCategoryId(categoryRepository.defaultCategory.getId());
        mockMvc.perform(MockMvcRequestBuilders.post("/move-products-task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    public void create_transfer_task_when_non_existent_source_then_return_not_found() throws Exception {
        TransferRequest request = new TransferRequest();
        request.setSourceCategoryId(UniqueIdGenerator.generateUniqueId());
        request.setTargetCategoryId(categoryRepository.defaultCategory.getId());
        mockMvc.perform(MockMvcRequestBuilders.post("/move-products-task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }




}