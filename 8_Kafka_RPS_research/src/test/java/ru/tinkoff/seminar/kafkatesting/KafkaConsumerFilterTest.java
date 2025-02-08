package ru.tinkoff.seminar.kafkatesting;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import ru.tinkoff.seminar.kafkatesting.common.config.BaseConfig;
import ru.tinkoff.seminar.kafkatesting.common.model.Person;
import ru.tinkoff.seminar.kafkatesting.common.repository.ChildRepository;
import ru.tinkoff.seminar.kafkatesting.common.repository.PersonRepository;
import ru.tinkoff.seminar.kafkatesting.kafka.consumer.KafkaConsumer;
import ru.tinkoff.seminar.kafkatesting.kafka.listener.KafkaFilterListener;
import ru.tinkoff.seminar.kafkatesting.kafka.producer.KafkaProducer;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@SpringBootTest(classes = {
        ChildRepository.class,
        PersonRepository.class,
        KafkaConsumer.class,
        KafkaFilterListener.class,
        BaseConfig.class
}, properties = {
        "topic=topic",
        "kafka.bootstrapAddress=${spring.embedded.kafka.brokers}",
        "kafka.group.id=group"
})
@EnableAutoConfiguration
@DisplayName("Пакетное чтение с фильтром в консьюмере с гарантией чтения")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@EmbeddedKafka(partitions = 1, topics = {"topic"})
public class KafkaConsumerFilterTest {

    @Value("${spring.embedded.kafka.brokers}")
    String bootstrapAddress;
    String topic = "topic";

    @Autowired
    PersonRepository personRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    KafkaFilterListener kafkaConsumerListener;


    @Order(1)
    @Test
    @DisplayName("Отправка c гарантией чтения (A)")
    void test_with_ack_all_A() throws JsonProcessingException, InterruptedException {
        KafkaProducer kafkaProducerAllAck = new KafkaProducer(bootstrapAddress, topic, "all", objectMapper);

        List<Person> allPersons = personRepository.findA();

        long startTime = System.currentTimeMillis();
        log.info("Start time - {}", startTime);
        for (Person person : allPersons) {
            kafkaProducerAllAck.sendMessage(person);
        }
        long endTime = System.currentTimeMillis();
        long sendingTime = endTime - startTime;
        long rps = allPersons.size() / sendingTime;

        log.info("Sending time producer - {}", sendingTime);
        log.info("RPS - {}", rps);
        assertTrue(kafkaConsumerListener.getLatch().await(2, TimeUnit.MINUTES));
    }

    @Order(2)
    @Test
    @DisplayName("Отправка c гарантией чтения (B)")
    void test_with_ack_all_B() throws JsonProcessingException, InterruptedException {
        KafkaProducer kafkaProducerAllAck = new KafkaProducer(bootstrapAddress, topic, "all", objectMapper);
        List<Person> allPersons = personRepository.findB();

        long startTime = System.currentTimeMillis();
        log.info("Start time - {}", startTime);
        for (Person person : allPersons) {
            kafkaProducerAllAck.sendMessage(person);
        }
        long endTime = System.currentTimeMillis();
        long sendingTime = endTime - startTime;
        long rps = allPersons.size() / sendingTime;

        log.info("Sending time producer - {}", sendingTime);
        log.info("RPS - {}", rps);

        assertTrue(kafkaConsumerListener.getLatch().await(2, TimeUnit.MINUTES));
    }

    @AfterEach
    public void resetLatch() {
        kafkaConsumerListener.setLatch(new CountDownLatch(900_000));
    }



}
