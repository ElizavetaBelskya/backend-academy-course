package ru.seminar.homework.hw5.util;
import java.util.UUID;

public class UniqueIdGenerator {

    public static String generateUniqueId() {
        return UUID.randomUUID().toString().replace("-", "").toUpperCase();
    }

}
