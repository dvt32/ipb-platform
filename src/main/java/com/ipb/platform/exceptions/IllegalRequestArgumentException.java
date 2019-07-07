package com.ipb.platform.exceptions;


public class IllegalRequestArgumentException extends RuntimeException {
    public IllegalRequestArgumentException() {
        super();
    }
    public IllegalRequestArgumentException(String message, Throwable cause) {
        super(message, cause);
    }
    public IllegalRequestArgumentException(String message) {
        super(message);
    }
    public IllegalRequestArgumentException(Throwable cause) {
        super(cause);
    }
}