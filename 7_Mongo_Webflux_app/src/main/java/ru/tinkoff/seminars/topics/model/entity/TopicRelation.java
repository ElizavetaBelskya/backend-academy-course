package ru.tinkoff.seminars.topics.model.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder

@Table("TOPIC_TO_MESSAGE")
public class TopicRelation {

    @Id
    @Column("id")
    private Long id;

    @Column("topic_id")
    private Long topicId;

    @Column("message_id")
    private Long messageId;

    public TopicRelation(Long idt, Long idm) {
        this.topicId = idt;
        this.messageId = idm;
    }

}
