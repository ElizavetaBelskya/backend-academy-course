package ru.tinkoff.seminar.kafkatesting.kafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import ru.tinkoff.seminar.kafkatesting.common.dto.PersonDto;
import ru.tinkoff.seminar.kafkatesting.common.mapper.PersonMapper;
import ru.tinkoff.seminar.kafkatesting.common.model.Person;

import java.util.HashMap;
import java.util.Map;

public class KafkaProducer {
    private final String bootstrapAddress;
    private final String topic;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public KafkaProducer(String bootstrapAddress, String topic, String ack, ObjectMapper objectMapper) {
        this.bootstrapAddress = bootstrapAddress;
        this.topic = topic;
        ProducerFactory<String, String> producerFactory = producerFactory(ack);
        this.kafkaTemplate = kafkaTemplate(producerFactory);
        this.objectMapper = objectMapper;
    }

    public ProducerFactory<String, String> producerFactory(String ack) {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.ACKS_CONFIG, ack);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    public KafkaTemplate<String, String> kafkaTemplate(ProducerFactory<String, String> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    public void sendMessage(Person person) throws JsonProcessingException {
        this.kafkaTemplate.send(topic,
                String.valueOf(person.getId()),
                objectMapper.writeValueAsString(PersonMapper.mapPerson(person)));
    }


}
