package ru.tinkoff.seminars.topics.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.tinkoff.seminars.topics.model.document.Topic;
import ru.tinkoff.seminars.topics.service.TopicService;

@RestController
@RequiredArgsConstructor
public class MessageController {

    private final TopicService service;

    @GetMapping("/topics/{idt}/messages/{idm}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Topic> getMessageById(@PathVariable Long idt, @PathVariable Long idm) {
        return service.findById(idm);
    }

    @GetMapping("/topics/{idt}/messages")
    @ResponseStatus(HttpStatus.OK)
    public Flux<Topic> getMessagesById(@PathVariable Long idt) {
        return service.findAllMessagesByTopicId(idt);
    }

    @PostMapping("/topics/{idt}/messages")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Topic> createMessage(@PathVariable Long idt, @RequestBody Topic topic) {
        return service.createMessage(idt, topic);
    }

    @PutMapping("/topics/{idt}/messages/{idm}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Mono<Topic> putMessage(@PathVariable Long idm, @PathVariable @RequestBody Topic topic) {
        return service.saveToUpdate(idm, topic);
    }

    @DeleteMapping("/topics/{idt}/messages/{idm}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteMessage(@PathVariable Long idt, @PathVariable Long idm) {
        return service.deleteMessage(idt, idm);
    }

    @PostMapping("/topics/{idt}/messages/{idm}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Topic> createCommentMessage(@PathVariable Long idt, @PathVariable Long idm, @RequestBody Topic topic) {
        return service.createCommentMessage(idt, idm, topic);
    }

    @PutMapping("/topics/{idt}/messages/{idm}/comments")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Mono<Topic> editCommentMessage(@PathVariable Long idt, @PathVariable Long idm, @RequestBody Topic topic) {
        return service.saveToUpdate(topic.getId(), topic);
    }

    @DeleteMapping("/topics/{idt}/messages/{idm}/comments/{idc}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteCommentMessage(@PathVariable Long idt, @PathVariable Long idm, @PathVariable Long idc) {
        return service.deleteCommentMessage(idt, idm, idc);
    }


}
