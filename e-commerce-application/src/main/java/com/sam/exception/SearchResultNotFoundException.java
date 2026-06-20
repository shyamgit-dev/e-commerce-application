package com.sam.exception;

public class SearchResultNotFoundException extends RuntimeException{
    public SearchResultNotFoundException(String message) {
        super(message);
    }
}
