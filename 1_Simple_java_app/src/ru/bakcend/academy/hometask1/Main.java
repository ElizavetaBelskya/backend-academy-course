package ru.bakcend.academy.hometask1;


import ru.bakcend.academy.hometask1.util.CommandProcessor;
import ru.bakcend.academy.hometask1.input.InputHandler;
import ru.bakcend.academy.hometask1.repository.ProductRepository;
import ru.bakcend.academy.hometask1.repository.impl.ProductRepositoryListImpl;
import ru.bakcend.academy.hometask1.service.ProductService;
import ru.bakcend.academy.hometask1.service.impl.ProductServiceImpl;
import ru.bakcend.academy.hometask1.input.impl.ScannerInputHandler;
import ru.bakcend.academy.hometask1.output.OutputHandler;
import ru.bakcend.academy.hometask1.output.impl.ConsoleOutputHandler;

public class Main {
    public static final String EXIT_COMMAND = "exit";
    public static void main(String[] args) {
        InputHandler inputHandler = new ScannerInputHandler();
        ProductRepository productRepository = new ProductRepositoryListImpl();
        ProductService productService = new ProductServiceImpl(productRepository);
        CommandProcessor commandProcessor = new CommandProcessor(productService);
        OutputHandler outputHandler = new ConsoleOutputHandler();
        boolean exit = false;
        while (!exit) {
            String input = inputHandler.getUserInput();
            String result = commandProcessor.processCommand(input);
            if (EXIT_COMMAND.equals(result)) {
                exit = true;
            } else {
                outputHandler.displayMessage(result);
            }
        }
        inputHandler.close();
        outputHandler.close();
    }

}