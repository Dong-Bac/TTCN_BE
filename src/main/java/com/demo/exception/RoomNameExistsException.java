package com.demo.exception;

public class RoomNameExistsException extends RuntimeException {
    public RoomNameExistsException(String message){
        super(message);
    }
}
