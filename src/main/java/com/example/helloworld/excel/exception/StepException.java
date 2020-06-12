package com.example.helloworld.excel.exception;

public class StepException extends Throwable{

    public StepException(String message) {
        this(message, null);
    }

    public StepException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
