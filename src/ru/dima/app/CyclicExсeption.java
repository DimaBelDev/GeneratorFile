package ru.dima.app;

public class CyclicExсeption extends RuntimeException{

    public CyclicExсeption(String message, String file1, String file2) {
        super(message + file1 + file2);
    }

}