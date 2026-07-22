package com.airesumeanalyzer.exception;

// Thrown when PDF text extraction or the AI provider call fails
public class FileProcessingException extends RuntimeException {

    public FileProcessingException(String message) {
        super(message);
    }

    public FileProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
