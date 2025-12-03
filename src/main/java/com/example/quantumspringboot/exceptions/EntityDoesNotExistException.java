package com.example.quantumspringboot.exceptions;

public class EntityDoesNotExistException extends RuntimeException{
    public EntityDoesNotExistException(String str){
        super(str);
    }
}
