package com.mulesoft.exception;

public class ServiceFaultException extends RuntimeException {



    public ServiceFaultException(String message) {
        super(message);

    }

}