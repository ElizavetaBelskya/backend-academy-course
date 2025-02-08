package ru.backend.academy.hometask4.model;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "transfer_task")
public class TransferTask {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long taskId;

    @ManyToOne
    @JoinColumn(name = "source_category_id")
    private Category sourceCategory;

    @ManyToOne
    @JoinColumn(name = "target_category_id")
    private Category targetCategory;

    @Enumerated(EnumType.STRING)
    private TaskStatus taskStatus;

}
