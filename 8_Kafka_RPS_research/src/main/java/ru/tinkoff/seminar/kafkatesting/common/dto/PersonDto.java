package ru.tinkoff.seminar.kafkatesting.common.dto;

import lombok.*;
import ru.tinkoff.seminar.kafkatesting.common.mapper.PersonMapper;
import ru.tinkoff.seminar.kafkatesting.common.model.Child;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PersonDto {
    private Long id;
    private String name;
    private String aboutMe;
    private LocalDate birthdate;
    private List<PersonDto> children;

}
