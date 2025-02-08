package ru.tinkoff.seminar.kafkatesting.kafka.producer;

import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import ru.tinkoff.seminar.kafkatesting.common.dto.avro.PersonDtoAvro;
import ru.tinkoff.seminar.kafkatesting.common.mapper.PersonAvroMapper;
import ru.tinkoff.seminar.kafkatesting.common.model.Person;

import java.util.HashMap;
import java.util.Map;

public class KafkaAvroProducer {
    private final String bootstrapAddress;
    private final String topic;
    private final KafkaTemplate<String, PersonDtoAvro> kafkaTemplate;

    public KafkaAvroProducer(String bootstrapAddress, String topic, String ack) {
        this.bootstrapAddress = bootstrapAddress;
        this.topic = topic;
        ProducerFactory<String, PersonDtoAvro> producerFactory = producerFactory(ack);
        this.kafkaTemplate = kafkaTemplate(producerFactory);
    }

    public ProducerFactory<String, PersonDtoAvro> producerFactory(String ack) {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        configProps.put(ProducerConfig.ACKS_CONFIG, ack);
        configProps.put(KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG, "mock://mock");
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    public KafkaTemplate<String, PersonDtoAvro> kafkaTemplate(ProducerFactory<String, PersonDtoAvro> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    public void sendMessage(Person person) {
        this.kafkaTemplate.send(topic, String.valueOf(person.getId()), PersonAvroMapper.mapPerson(person));
    }

}
