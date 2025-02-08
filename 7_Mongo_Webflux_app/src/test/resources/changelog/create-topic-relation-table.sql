CREATE TABLE TOPIC_TO_MESSAGE(
    id serial primary key,
    topic_id bigint,
    message_id bigint,
    UNIQUE (topic_id, message_id)
);

create index on TOPIC_TO_MESSAGE(topic_id);

CREATE TABLE MESSAGE_TO_COMMENT(
    id serial primary key,
    message_id bigint,
    comment_id bigint,
    UNIQUE (message_id, comment_id)
);

create index on MESSAGE_TO_COMMENT(message_id);

