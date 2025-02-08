package ru.tinkoff.seminars.topics.model.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder

@Table(name = "MESSAGE_TO_COMMENT", schema = "public")
public class Comment {

    @Id
    private Long id;

    @Column("message_id")
    private Long messageId;

    @Column("comment_id")
    private Long commentId;


    public Comment(Long messageId, Long commentId) {
        this.messageId = messageId;
        this.commentId = commentId;
    }

}
