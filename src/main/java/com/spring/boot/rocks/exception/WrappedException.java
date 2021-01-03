package com.spring.boot.rocks.exception;

public class WrappedException extends RuntimeException{

    public WrappedException(Throwable e) {
        super(e);
    }
}
