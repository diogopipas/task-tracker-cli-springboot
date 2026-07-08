package com.diogo.tasktracker.model;

public enum Status {
    TODO("todo"),
    IN_PROGRESS("in-progress"),
    DONE("done");

    private final String value;

    Status(String value){
        this.value = value;
    }

    public String getValue(){
        return value;
    }

    public static Status fromValue(String value){
        for(Status status : Status.values()){
            if(status.value.equals(value)){
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown status: " + value);
    }
}
