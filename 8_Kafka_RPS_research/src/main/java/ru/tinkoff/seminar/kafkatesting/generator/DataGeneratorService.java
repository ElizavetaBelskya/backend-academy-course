package ru.tinkoff.seminar.kafkatesting.generator;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.tinkoff.seminar.kafkatesting.common.model.Child;
import ru.tinkoff.seminar.kafkatesting.common.model.Person;
import ru.tinkoff.seminar.kafkatesting.common.repository.ChildRepository;
import ru.tinkoff.seminar.kafkatesting.common.repository.PersonRepository;

import java.time.LocalDate;
import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
public class DataGeneratorService {

    private final PersonRepository personRepository;
    private final ChildRepository childRepository;

    @PostConstruct
    public void generateData() {
        log.info("Started generating data for tables");
        populatePersonTable(1_000_000, 10, 500_000, 1, 'A');
        log.info("Data is generated for A");
        populatePersonTable(1_000_000, 1000, 20_000, 5, 'B');
        log.info("Data is generated for B");
    }

    private String generateAboutMe(int length) {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        for (int i = 0; i < length; i++) {
            char c = characters.charAt(random.nextInt(characters.length()));
            sb.append(c);
        }
        return sb.toString();
    }

    private LocalDate generateRandomDate() {
        long minDay = LocalDate.of(1950, 1, 1).toEpochDay();
        long maxDay = LocalDate.now().toEpochDay();
        long randomDay = minDay + (long) (Math.random() * (maxDay - minDay));
        return LocalDate.ofEpochDay(randomDay);
    }

    public void populatePersonTable(int numOfRecords, int aboutMeLength, int numOfParents, int numOfChildrenPerParent, char category) {
        int c = 0;

        for (int i = 0; i < numOfRecords - numOfParents * (numOfChildrenPerParent + 1); i++) {
            Person person = new Person();
            person.setName("Person " + c);
            person.setAboutMe(generateAboutMe(aboutMeLength));
            person.setBirthdate(generateRandomDate());
            person.setCategory(category);
            personRepository.save(person);
            c++;
        }

        for (int i = 0; i < numOfParents; i++) {
            Person parent = new Person();
            parent.setName("Person " + c);
            parent.setAboutMe(generateAboutMe(aboutMeLength));
            parent.setBirthdate(generateRandomDate());
            parent.setCategory(category);
            personRepository.save(parent);
            c++;

            for (int j = 0; j < numOfChildrenPerParent; j++) {
                Child child = new Child();
                child.setParent(parent);
                Person childPerson = new Person();
                childPerson.setName("Person " + c);
                childPerson.setAboutMe(generateAboutMe(aboutMeLength));
                childPerson.setBirthdate(generateRandomDate());
                childPerson.setCategory(category);
                personRepository.save(childPerson);
                c++;
                child.setChild(childPerson);
                childRepository.save(child);
            }
        }
    }


}
