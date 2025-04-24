package com.example.workflow.exception;

public class NoSushAnswerException extends RuntimeException {
    public NoSushAnswerException(String message) {
        super(message);
    }
}
