package ru.tinkoff.seminars.topics.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.tinkoff.seminars.topics.model.document.Topic;
import ru.tinkoff.seminars.topics.service.TopicService;

@RestController
@RequestMapping("/topics")
@RequiredArgsConstructor
public class TopicController {

    private final TopicService service;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Topic> getTopicById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Topic> createTopic(@RequestBody Topic document) {
        return service.save(document);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Mono<Topic> putTopic(@PathVariable Long id, @RequestBody Topic document) {
        return service.saveToUpdate(id, document);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Mono<Void> deleteTopic(@PathVariable Long id) {
        return service.deleteTopicAndMessages(id);
    }


}
