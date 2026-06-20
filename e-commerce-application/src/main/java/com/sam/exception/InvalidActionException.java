package com.sam.exception;

public class InvalidActionException extends RuntimeException{
    public InvalidActionException(String message) {
        super(message);
    }
}
