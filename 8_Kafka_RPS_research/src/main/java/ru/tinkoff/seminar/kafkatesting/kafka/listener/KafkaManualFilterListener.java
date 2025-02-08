package ru.tinkoff.seminar.kafkatesting.kafka.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CountDownLatch;

@Slf4j
@Component
public class KafkaManualFilterListener {

    private CountDownLatch latch = new CountDownLatch(900_000);
    long endTime;
    long startTime;

    @KafkaListener(topics = "${topic}", containerFactory = "kafkaListenerContainerBatchFactory")
    public void listen(@Payload List<String> message) {
        if (latch.getCount() == 900_000) {
            startTime = System.currentTimeMillis();
        }

        for (String m : message) {
            if (filter(m)) {
                if (latch.getCount() == 1) {
                    endTime = System.currentTimeMillis();
                    long sendingTime = endTime - startTime;

                    long rps = 900_000 / sendingTime;
                    log.info("Sending time listener - {}", sendingTime);
                    log.info("RPS - {}", rps);
                }
                latch.countDown();
            }
        }
    }

    public CountDownLatch getLatch() {
        return latch;
    }

    public void setLatch(CountDownLatch latch) {
        this.latch = latch;
    }


    private boolean filter(String personJson) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode nameNode = mapper.readTree(personJson).findValue("name");
            if (nameNode != null && nameNode.isTextual()) {
                long nameNumber = Long.parseLong(nameNode.asText().split(" ")[1]);
                if (nameNumber >= 100_000) {
                    return true;
                }
            }
        } catch (JsonProcessingException e) {
            return false;
        }
        return false;
    }


}
