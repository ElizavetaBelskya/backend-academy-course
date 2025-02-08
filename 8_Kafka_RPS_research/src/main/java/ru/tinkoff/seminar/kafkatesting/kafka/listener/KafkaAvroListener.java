package ru.tinkoff.seminar.kafkatesting.kafka.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.tinkoff.seminar.kafkatesting.common.dto.avro.PersonDtoAvro;

import java.util.List;
import java.util.concurrent.CountDownLatch;

@Slf4j
@Component
public class KafkaAvroListener {

    private CountDownLatch latch = new CountDownLatch(1_000_000);
    long endTime;
    long startTime;

    @KafkaListener(topics = "${topic}", containerFactory = "kafkaListenerAvroContainerFactory")
    public void listen(@Payload List<PersonDtoAvro> message) {
        if (latch.getCount() == 1_000_000) {
            startTime = System.currentTimeMillis();
        }

        for (int i = 0; i < message.size(); i++) {
            if (latch.getCount() == 1) {
                endTime = System.currentTimeMillis();
                long sendingTime = endTime - startTime;
                long rps = 1_000_000 / sendingTime;
                log.info("Sending time listener - {}", sendingTime);
                log.info("RPS - {}", rps);
            }
            latch.countDown();
        }
    }

    public CountDownLatch getLatch() {
        return latch;
    }

    public void setLatch(CountDownLatch latch) {
        this.latch = latch;
    }

}
