package ru.tinkoff.seminar.kafkatesting;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import ru.tinkoff.seminar.kafkatesting.common.model.Person;
import ru.tinkoff.seminar.kafkatesting.common.repository.ChildRepository;
import ru.tinkoff.seminar.kafkatesting.common.repository.PersonRepository;
import ru.tinkoff.seminar.kafkatesting.kafka.consumer.KafkaAvroConsumer;
import ru.tinkoff.seminar.kafkatesting.kafka.listener.KafkaAvroListener;
import ru.tinkoff.seminar.kafkatesting.kafka.producer.KafkaAvroProducer;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@SpringBootTest(classes = {
        ChildRepository.class,
        PersonRepository.class,
        KafkaAvroConsumer.class,
        KafkaAvroListener.class
}, properties = {
        "topic=topic",
        "kafka.bootstrapAddress=${spring.embedded.kafka.brokers}",
        "kafka.group.id=group"
})
@EnableAutoConfiguration
@DisplayName("Avro")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@EmbeddedKafka(partitions = 1, topics = {"topic"})
public class KafkaAvroTest {

    @Value("${spring.embedded.kafka.brokers}")
    String bootstrapAddress;
    String topic = "topic";

    @Autowired
    PersonRepository personRepository;


    @Autowired
    KafkaAvroListener kafkaListener;


    @Order(1)
    @Test
    @DisplayName("Отправка без гарантии чтения (A)")
    void test_with_ack_0_A() throws InterruptedException {
        KafkaAvroProducer kafkaProducerNoAck = new KafkaAvroProducer(bootstrapAddress, topic, "0");

        List<Person> allPersons = personRepository.findA();

        long startTime = System.currentTimeMillis();
        log.info("Start time - {}", startTime);
        for (Person person : allPersons) {
            kafkaProducerNoAck.sendMessage(person);
        }
        long endTime = System.currentTimeMillis();
        long sendingTime = endTime - startTime;
        long rps = allPersons.size() / sendingTime;

        log.info("Sending time producer - {}", sendingTime);
        log.info("RPS - {}", rps);
        assertTrue(kafkaListener.getLatch().await(1, TimeUnit.MINUTES));
    }


    @Order(2)
    @Test
    @DisplayName("Отправка c гарантией чтения (A)")
    void test_with_ack_all_A() throws InterruptedException {
        KafkaAvroProducer kafkaProducerAllAck = new KafkaAvroProducer(bootstrapAddress, topic, "all");

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
        assertTrue(kafkaListener.getLatch().await(1, TimeUnit.MINUTES));
    }

    @Order(3)
    @Test
    @DisplayName("Отправка без гарантии чтения (B)")
    void test_with_ack_0_B() throws InterruptedException {
        KafkaAvroProducer kafkaProducerNoAck = new KafkaAvroProducer(bootstrapAddress, topic, "0");

        List<Person> allPersons = personRepository.findB();

        long startTime = System.currentTimeMillis();
        log.info("Start time - {}", startTime);
        for (Person person : allPersons) {
            kafkaProducerNoAck.sendMessage(person);
        }
        long endTime = System.currentTimeMillis();
        long sendingTime = endTime - startTime;
        long rps = allPersons.size() / sendingTime;

        log.info("Sending time producer - {}", sendingTime);
        log.info("RPS - {}", rps);
        assertTrue(kafkaListener.getLatch().await(1, TimeUnit.MINUTES));
    }

    @Order(4)
    @Test
    @DisplayName("Отправка c гарантией чтения (B)")
    void test_with_ack_all_B() throws InterruptedException {
        KafkaAvroProducer kafkaProducerAllAck = new KafkaAvroProducer(bootstrapAddress, topic, "all");

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

        assertTrue(kafkaListener.getLatch().await(1, TimeUnit.MINUTES));
    }

    @AfterEach
    public void resetLatch() {
        kafkaListener.setLatch(new CountDownLatch(1_000_000));
    }



}
