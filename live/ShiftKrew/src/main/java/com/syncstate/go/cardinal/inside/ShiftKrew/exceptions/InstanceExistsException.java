package com.syncstate.go.cardinal.inside.ShiftKrew.exceptions;

public class InstanceExistsException extends RuntimeException{

    private String message;

    public InstanceExistsException(String message)
    {
        super(message);
        this.message = message;
    }
}
