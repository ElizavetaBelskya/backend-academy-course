package ru.tinkoff.seminars.topics.controller;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;
import reactor.core.publisher.Mono;
import ru.tinkoff.seminars.topics.model.document.Topic;
import ru.tinkoff.seminars.topics.repository.document.TopicRepository;
import ru.tinkoff.seminars.topics.repository.entity.CommentRepository;
import ru.tinkoff.seminars.topics.repository.entity.MessageRepository;

import java.util.Objects;

@SpringBootTest
@AutoConfigureWebTestClient
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TopicControllerTest {

    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0");

    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest")
            .withUsername("postgres")
            .withPassword("postgres")
            .withDatabaseName("topics");

    static GenericContainer<?> redisContainer = new GenericContainer<>(DockerImageName.parse("redis:5.0.3-alpine"))
            .withExposedPorts(6379);;

    static {
        mongoDBContainer.start();
        postgreSQLContainer.start();
        redisContainer.start();
    }


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
    private WebTestClient webTestClient;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private CommentRepository commentRepository;


    @BeforeEach
    void setUp() {
        webTestClient = webTestClient
                .mutate().baseUrl("http://localhost:8080")
                .build();
    }

    @Test
    @Order(1)
    void testPost() {
        Topic topic = new Topic( 1L, "Test topic");
        webTestClient.post().uri("/topics")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(topic), Topic.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.markdownText")
                .isEqualTo("Test topic")
                .jsonPath("$.id")
                .isNotEmpty();
    }

    @Test
    @Order(2)
    void testGetTopicById() {
        webTestClient.get()
                .uri("/topics/{id}", 1L)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.markdownText").isEqualTo("Test topic")
                .jsonPath("$.id").exists();
    }

    @Test
    @Order(3)
    void testPutTopic() {
        Topic topic = new Topic( 1L, "Updated text");
        webTestClient.put()
                .uri("/topics/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(topic), Topic.class)
                .exchange()
                .expectStatus().isAccepted()
                .expectBody()
                .jsonPath("$.markdownText").isEqualTo("Updated text")
                .jsonPath("$.id").isEqualTo(1L);
        String updatedText = Objects.requireNonNull(topicRepository.findById(1L).block()).getMarkdownText();
        Assertions.assertNotNull(updatedText);
        Assertions.assertEquals("Updated text", updatedText);
    }

    @Test
    @Order(4)
    void testPutTopicNonExistingIdAndGetNotFound() {
        Topic topic = new Topic( 100L, "Updated text");
        webTestClient.put()
                .uri("/topics/{id}", 100L)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(topic), Topic.class)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @Order(5)
    void testCreateMessage() {
        Topic topic1 = new Topic( 2L, "Comment 1");
        webTestClient.post()
                .uri("/topics/{idt}/messages", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(topic1), Topic.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.markdownText").isEqualTo("Comment 1")
                .jsonPath("$.id").isEqualTo(2L);

        Topic topic2 = new Topic( 3L, "Comment 2");
        webTestClient.post()
                .uri("/topics/{idt}/messages", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(topic2), Topic.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.markdownText").isEqualTo("Comment 2")
                .jsonPath("$.id").isEqualTo(3L);

        Assertions.assertEquals(2L, messageRepository.findByTopicId(1L).blockFirst());
        Assertions.assertEquals("Comment 1", topicRepository.findById(2L).block().getMarkdownText());
    }

    @Test
    @Order(6)
    void testGetMessages() {
        webTestClient.get()
                .uri("/topics/{idt}/messages", 1L)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.size()").isEqualTo(2);
    }

    @Test
    @Order(7)
    void testDeleteMessage() {
        Topic newMessage = new Topic( 4L, "Comment message");
        webTestClient.post()
                .uri("/topics/{idt}/messages", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(newMessage), Topic.class)
                .exchange().returnResult(Topic.class);

        Assertions.assertEquals(Boolean.TRUE, messageRepository.findByTopicId(1L).hasElement(4L).block());

        webTestClient.delete()
                .uri("/topics/{idt}/messages/{idm}", 1L, 4L)
                .exchange()
                .expectStatus().isNoContent();

        Assertions.assertEquals(Boolean.FALSE, messageRepository.findByTopicId(1L).hasElement(4L).block());
        Assertions.assertTrue(topicRepository.findById(4L).blockOptional().isEmpty());
    }

    @Test
    @Order(8)
    void testAddComment() {
        Assertions.assertEquals(Boolean.FALSE, commentRepository.findAll().hasElements().block());

        Topic newMessage = new Topic( 5L, "Good answer!");
        webTestClient.post()
                .uri("/topics/{idt}/messages/{idm}/comments", 1L, 3L)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(newMessage), Topic.class)
                .exchange()
                .expectBody()
                .jsonPath("$.markdownText")
                .isEqualTo("Good answer!");

        Assertions.assertEquals(Boolean.TRUE, commentRepository.findAll().hasElements().block());
    }

    @Test
    @Order(9)
    void testUpdateComment() {
        Topic newMessage = new Topic( 5L, "Good!!!");
        webTestClient.put()
                .uri("/topics/{idt}/messages/{idm}/comments", 1L, 3L)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(newMessage), Topic.class)
                .exchange()
                .expectStatus().isAccepted()
                .expectBody()
                .jsonPath("$.markdownText")
                .isEqualTo("Good!!!");

        Assertions.assertEquals("Good!!!", topicRepository.findById(5L).block().getMarkdownText());
    }

    @Test
    @Order(10)
    void testDeleteComment() {
        webTestClient.delete()
                .uri("/topics/{idt}/messages/{idm}/comments/{idc}", 1L, 3L, 5L)
                .exchange()
                .expectStatus().isNoContent();

        Assertions.assertEquals(Boolean.FALSE, commentRepository.findAll().hasElements().block());
        Assertions.assertTrue(topicRepository.findById(5L).blockOptional().isEmpty());
    }

    @Test
    @Order(11)
    void testGetNonExistingMessage() {
        webTestClient.get()
                .uri("/topics/{idt}/messages/{idm}", 1L, 333L)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @Order(12)
    void testGetNotExistingTopic() {
        webTestClient.get()
                .uri("/topics/{id}", 100L)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @Order(13)
    void testDeleteTopic() {
        webTestClient.delete()
                .uri("/topics/{id}", 1L)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody().isEmpty();
        Assertions.assertFalse(messageRepository.findByTopicId(1L).hasElements().blockOptional().get());
        Assertions.assertTrue(topicRepository.findById(1L).blockOptional().isEmpty());
    }

    @Test
    @Order(14)
    void testDeleteCommentWithNotExistingMessage() {
        webTestClient.delete()
                .uri("/topics/{idt}/messages/{idm}/comments/{idc}", 1L, 11L, 5L)
                .exchange()
                .expectStatus().isNotFound();
    }


}
