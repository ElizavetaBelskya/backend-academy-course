package ru.tinkoff.seminars.topics.repository.document;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import ru.tinkoff.seminars.topics.model.document.Topic;

@Repository
public interface TopicRepository extends ReactiveMongoRepository<Topic, Long> {
    Flux<Topic> findByMarkdownText(String text);

}