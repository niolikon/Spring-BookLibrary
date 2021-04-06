package org.niolikon.springbooklibrary.system.exceptions;

public class OperationNotAcceptableException extends RuntimeException {
    public OperationNotAcceptableException() {
        super();
    }

    public OperationNotAcceptableException(String message) {
        super(message);
    }

    public OperationNotAcceptableException(String message, Throwable cause) {
        super(message, cause);
    }
}
