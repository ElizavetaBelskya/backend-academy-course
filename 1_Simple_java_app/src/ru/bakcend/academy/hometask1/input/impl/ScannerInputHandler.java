package ru.bakcend.academy.hometask1.input.impl;

import ru.bakcend.academy.hometask1.input.InputHandler;

import java.util.Scanner;

public class ScannerInputHandler implements InputHandler {

    private final Scanner scanner;

    public ScannerInputHandler() {
        this.scanner = new Scanner(System.in);
    }

    public String getUserInput() {
        return scanner.nextLine();
    }

    public void close() {
        scanner.close();
    }

}
