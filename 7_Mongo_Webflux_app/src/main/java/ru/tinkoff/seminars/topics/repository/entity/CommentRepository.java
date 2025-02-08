package ru.tinkoff.seminars.topics.repository.entity;


import org.springframework.data.r2dbc.repository.Query;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import ru.tinkoff.seminars.topics.model.entity.Comment;

@Repository
public interface CommentRepository extends ReactiveCrudRepository<Comment, Long> {

    @Query("delete from MESSAGE_TO_COMMENT where message_id = $1 and comment_id = $2")
    Mono<Void> delete(Long idm, Long idc);
}
