package ru.tinkoff.seminar.kafkatesting.common.mapper;

import ru.tinkoff.seminar.kafkatesting.common.dto.PersonDto;
import ru.tinkoff.seminar.kafkatesting.common.model.Child;
import ru.tinkoff.seminar.kafkatesting.common.model.Person;

import java.util.stream.Collectors;

public class PersonMapper {

    private PersonMapper() {
    }

    public static PersonDto mapPerson(Person person) {
        return PersonDto.builder()
                .id(person.getId())
                .name(person.getName())
                .aboutMe(person.getAboutMe())
                .birthdate(person.getBirthdate())
                .build();
    }


}
