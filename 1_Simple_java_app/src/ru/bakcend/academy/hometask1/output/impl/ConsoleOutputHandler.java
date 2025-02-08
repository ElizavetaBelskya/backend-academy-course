package ru.bakcend.academy.hometask1.output.impl;

import ru.bakcend.academy.hometask1.output.OutputHandler;

public class ConsoleOutputHandler implements OutputHandler {

    private static final String START_MESSAGE = "Введите команду (create, read, update, delete, exit): ";
    private static final String FINISH_MESSAGE = "Программа завершена";

    public ConsoleOutputHandler() {
        displayMessage(START_MESSAGE);
    }

    public void displayMessage(String message) {
        System.out.println(message);
    }

    public void close() {
        displayMessage(FINISH_MESSAGE);
    }

}
