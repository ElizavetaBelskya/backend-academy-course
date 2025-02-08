package ru.tinkoff.seminars.topics.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.tinkoff.seminars.topics.model.document.Topic;
import ru.tinkoff.seminars.topics.model.entity.Comment;
import ru.tinkoff.seminars.topics.model.entity.TopicRelation;
import ru.tinkoff.seminars.topics.repository.document.TopicRepository;
import ru.tinkoff.seminars.topics.repository.entity.CommentRepository;
import ru.tinkoff.seminars.topics.repository.entity.MessageRepository;

import java.util.List;


@Service
@RequiredArgsConstructor
public class TopicService {

    private final TopicRepository topicRepository;

    private final MessageRepository messageRepository;

    private final CommentRepository commentRepository;

    @Cacheable(value = "topic", key = "#id")
    public Mono<Topic> findById(Long id) {
        return topicRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Topic not found")));
    }

    @CachePut(value = "topic", key = "#id")
    public Mono<Topic> saveToUpdate(Long id, Topic document) {
        document.setId(id);
        return topicRepository.existsById(id)
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Topic not found"));
                    } else {
                        return topicRepository.save(document);
                    }
                });
    }

    @CachePut(value = "topic", key = "#document.getId()")
    public Mono<Topic> save(Topic document) {
        return topicRepository.existsById(document.getId())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new DataIntegrityViolationException("Topic with id " + document.getId() + " already exists"));
                    } else {
                        return topicRepository.save(document);
                    }
                });
    }


    public Mono<TopicRelation> addMessage(Long idt, Long idm) {
        var topicRelation = new TopicRelation(idt, idm);
        return messageRepository.save(topicRelation);
    }

    @CacheEvict(value = {"messages", "topic"}, key = "#topicId")
    public Mono<Void> deleteTopicAndMessages(Long topicId) {
        return topicRepository.existsById(topicId)
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Topic not found"));
                    } else {
                        return messageRepository.deleteAllByTopicId(topicId)
                                .then(topicRepository.deleteById(topicId));
                    }
                });
    }

    @Cacheable(value = "messages", key = "#topicId")
    public Flux<Topic> findAllMessagesByTopicId(Long topicId) {
        Flux<Long> messageIds = messageRepository.findByTopicId(topicId);
        return topicRepository.findAllById(messageIds);
    }

    @CachePut(value = "topic", key = "#topic.getId()")
    public Mono<Topic> createMessage(Long idt, Topic topic) {
        return addMessage(idt, topic.getId())
                .then(this.save(topic));
    }

    @CachePut(value = "topic", key = "#topic.getId()")
    public Mono<Topic> createCommentMessage(Long idt, Long idm, Topic topic) {
        return addMessage(idt, topic.getId())
                .then(addComment(idm, topic.getId()))
                .then(this.save(topic));
    }

    private Mono<Comment> addComment(Long idm, Long idc) {
        Comment comment = new Comment(idm, idc);
        return commentRepository.save(comment);
    }

    public Mono<Void> deleteCommentMessage(Long idt, Long idm, Long idc) {
        return topicRepository.findAllById(List.of(idt, idm, idc)).count()
                .flatMap(count -> {
                    if (count < 3) {
                        return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Topic not found"));
                    } else {
                        return topicRepository.deleteById(idc)
                                .then(messageRepository.deleteAllByMessageId(idm))
                                .then(commentRepository.delete(idm, idc));
                    }
                });
    }

    public Mono<Void> deleteMessage(Long idt, Long idm) {
        return topicRepository.findAllById(List.of(idt, idm)).count()
                .flatMap(count -> {
                    if (count < 2) {
                        return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Topic not found"));
                    } else {
                        return topicRepository.deleteById(idm)
                                .then(messageRepository.deleteAllByMessageId(idm));
                    }
                });
    }

}
