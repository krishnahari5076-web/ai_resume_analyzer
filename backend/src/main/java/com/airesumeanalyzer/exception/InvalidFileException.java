package com.airesumeanalyzer.exception;

// Thrown when the uploaded file is missing, too big, or not a PDF
public class InvalidFileException extends RuntimeException {

    public InvalidFileException(String message) {
        super(message);
    }
}
