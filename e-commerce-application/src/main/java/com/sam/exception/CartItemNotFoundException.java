package com.sam.exception;

public class CartItemNotFoundException extends RuntimeException{

    public CartItemNotFoundException(String message) {
        super(message);
    }
}
