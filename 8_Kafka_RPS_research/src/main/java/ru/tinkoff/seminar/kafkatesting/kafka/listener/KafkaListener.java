package ru.tinkoff.seminar.kafkatesting.kafka.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

@Slf4j
@Component
public class KafkaListener {

    private CountDownLatch latch = new CountDownLatch(1_000_000);
    long endTime;
    long startTime;

    @org.springframework.kafka.annotation.KafkaListener(topics = "${topic}", containerFactory = "kafkaListenerContainerFactory")
    public void listen(@Payload String message) {
        if (latch.getCount() == 1_000_000) {
            startTime = System.currentTimeMillis();
        }

        if (latch.getCount() == 1) {
            endTime = System.currentTimeMillis();
            long sendingTime = endTime - startTime;
            long rps = 1_000_000 / sendingTime;
            log.info("Sending time listener - {}", sendingTime);
            log.info("RPS - {}", rps);
        }
        latch.countDown();
    }

    public CountDownLatch getLatch() {
        return latch;
    }

    public void setLatch(CountDownLatch latch) {
        this.latch = latch;
    }

}
