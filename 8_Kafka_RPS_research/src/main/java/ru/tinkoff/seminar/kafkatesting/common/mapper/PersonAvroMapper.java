package ru.tinkoff.seminar.kafkatesting.common.mapper;

import ru.tinkoff.seminar.kafkatesting.common.dto.avro.ChildAvro;
import ru.tinkoff.seminar.kafkatesting.common.dto.avro.PersonDtoAvro;
import ru.tinkoff.seminar.kafkatesting.common.model.Child;
import ru.tinkoff.seminar.kafkatesting.common.model.Person;

import java.util.stream.Collectors;

public class PersonAvroMapper {

    private PersonAvroMapper() {
    }

    public static PersonDtoAvro mapPerson(Person person) {
        return PersonDtoAvro.newBuilder()
                .setId(person.getId())
                .setAboutMe(person.getAboutMe())
                .setBirthdate(person.getBirthdate().toString())
                .setName(person.getName())
                .setChildren(person.getChildren().stream()
                        .map(Child::getChild)
                        .map(child -> ChildAvro.newBuilder()
                                .setId(child.getId())
                                .setAboutMe(child.getAboutMe())
                                .setBirthdate(child.getBirthdate().toString())
                                .setName(child.getName()).build()).collect(Collectors.toList())
                ).build();
    }

}
