package com.example.workflow.exception;

public class NoSuchCourseException extends RuntimeException {
    public NoSuchCourseException(String message) {
        super(message);
    }
}
