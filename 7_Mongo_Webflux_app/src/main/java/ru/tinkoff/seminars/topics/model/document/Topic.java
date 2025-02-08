package ru.tinkoff.seminars.topics.model.document;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;


@Document(collection = "topics")
@Setter
@Getter
@NoArgsConstructor
public class Topic {

    @Id
    private Long id;

    private LocalDateTime createdAt;

    private String markdownText;

    public Topic(Long id, String text) {
        this.id = id;
        this.markdownText = text;
        this.createdAt = LocalDateTime.now();
    }

    public Topic(String text) {
        this.markdownText = text;
        this.createdAt = LocalDateTime.now();
    }

}