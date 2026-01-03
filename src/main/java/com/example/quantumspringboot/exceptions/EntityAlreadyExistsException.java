package com.example.quantumspringboot.exceptions;

public class EntityAlreadyExistsException extends RuntimeException{
    public EntityAlreadyExistsException(String str){
        super(str);
    }
}
