package ru.tinkoff.seminars.topics.controller;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import reactor.core.publisher.Mono;
import ru.tinkoff.seminars.topics.model.document.Topic;
import ru.tinkoff.seminars.topics.service.TopicService;


import java.io.IOException;

import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureWebTestClient
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TopicControllerCacheTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0");

    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest")
            .withUsername("postgres")
            .withPassword("postgres")
            .withDatabaseName("topics");

    @Container
    static GenericContainer<?> redisContainer = new GenericContainer<>(DockerImageName.parse("redis:7.0-alpine"))
            .withExposedPorts(6379);


    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("spring.data.mongodb.database", () -> "test");
        String databaseUrl = "r2dbc:postgresql://localhost:"
                + postgreSQLContainer.getFirstMappedPort()
                + "/topics";

        registry.add("spring.r2dbc.url", () -> databaseUrl);
        registry.add("spring.r2dbc.username", () -> "postgres");
        registry.add("spring.r2dbc.password", () -> "postgres");
        registry.add("spring.liquibase.url", () -> postgreSQLContainer.getJdbcUrl());
        registry.add("spring.liquibase.user", () -> postgreSQLContainer.getUsername());
        registry.add("spring.liquibase.password", () -> postgreSQLContainer.getPassword());
        registry.add("spring.liquibase.change-log", () -> "classpath:changelog/changelog.yml");
        registry.add("spring.data.redis.host", () -> "localhost");
        String port =  redisContainer.getMappedPort(6379).toString();
        registry.add("spring.data.redis.port", () -> port);
    }

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private WebTestClient webTestClient;

    @SpyBean
    private TopicService topicService;


    @AfterAll
    public static void stop() {
        mongoDBContainer.stop();
        postgreSQLContainer.stop();
        redisContainer.stop();
    }


    @BeforeEach
    void setUp() {
        webTestClient = webTestClient
                .mutate().baseUrl("http://localhost:8080")
                .build();
    }

    @Test
    @Order(1)
    public void testGetCache() {
        Topic topic = new Topic( 1L, "Test topic 1");
        webTestClient.post().uri("/topics")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(topic), Topic.class)
                .exchange()
                .expectStatus().isCreated();

        webTestClient.get().uri("/topics/{id}", 1L)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.markdownText")
                .isEqualTo("Test topic 1");

        verify(topicService, never()).findById(1L);

        var cachedTopics = cacheManager.getCache("topic").get(1L).get();
        Assertions.assertNotNull(cachedTopics);
    }

    @Test
    @Order(2)
    public void testPutAndGetCache() {
        Topic topic = new Topic( 1L, "Updated");
        webTestClient.put().uri("/topics/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(topic), Topic.class)
                .exchange()
                .expectStatus().isAccepted();

        webTestClient.get().uri("/topics/{id}", 1L)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.markdownText")
                .isEqualTo("Updated");

        verify(topicService, never()).findById(1L);
    }

    @Test
    @Order(3)
    public void testDeleteCache() {
        webTestClient.delete().uri("/topics/{id}", 1L)
                .exchange()
                .expectStatus().isNotFound();

        webTestClient.get().uri("/topics/{id}", 1L)
                .exchange()
                .expectStatus().isNotFound();

        verify(topicService, times(1)).findById(1L);
    }




}
