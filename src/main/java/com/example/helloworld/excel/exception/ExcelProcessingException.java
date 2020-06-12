package com.example.helloworld.excel.exception;

import java.util.List;

public class ExcelProcessingException extends Throwable{

    private int code;

    private List<String> errorMessages;

    public ExcelProcessingException() {
        this(500);
    }

    public ExcelProcessingException(int code) {
        this(code, "Error while processing the request", null);
    }

    public ExcelProcessingException(int code, String message) {
        this(code, message, null);
    }

    public ExcelProcessingException(int code, List<String> errorMessages) {
        this(code, null, null);
        this.errorMessages = errorMessages;
    }

    public ExcelProcessingException(int code, String message, Throwable throwable) {
        super(message, throwable);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public List<String> getErrorMessages() {
        return errorMessages;
    }
}
