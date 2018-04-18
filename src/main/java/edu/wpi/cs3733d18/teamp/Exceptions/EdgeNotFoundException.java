package edu.wpi.cs3733d18.teamp.Exceptions;

public class EdgeNotFoundException extends Exception {
    private String message;

    public EdgeNotFoundException(){}

    public EdgeNotFoundException(String message){
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
