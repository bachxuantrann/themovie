package com.ptit_intern.themoviedb.exception;

public class UserExisted extends Exception{
    public UserExisted(String message) {
        super(message);
    }
}
