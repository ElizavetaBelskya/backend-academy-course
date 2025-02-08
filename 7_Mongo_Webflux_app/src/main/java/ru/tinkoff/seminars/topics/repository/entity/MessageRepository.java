package ru.tinkoff.seminars.topics.repository.entity;


import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.tinkoff.seminars.topics.model.entity.TopicRelation;

@Repository
public interface MessageRepository extends ReactiveCrudRepository<TopicRelation, Long> {

    @Query(value = "delete from TOPIC_TO_MESSAGE where topic_id = $1")
    Mono<Void> deleteAllByTopicId(Long id);

    @Query(value = "delete from TOPIC_TO_MESSAGE where message_id = $1")
    Mono<Void> deleteAllByMessageId(Long id);

    @Query(value = "select message_id from TOPIC_TO_MESSAGE where topic_id = $1")
    Flux<Long> findByTopicId(Long id);


}
