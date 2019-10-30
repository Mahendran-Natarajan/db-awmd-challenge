package com.db.awmd.challenge.exception;

public class NegativeBalance extends RuntimeException {
    public NegativeBalance(String message) {
        super(message);
    }
}
