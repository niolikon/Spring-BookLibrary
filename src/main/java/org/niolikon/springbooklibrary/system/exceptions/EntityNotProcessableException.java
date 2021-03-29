package org.niolikon.springbooklibrary.system.exceptions;

public class EntityNotProcessableException extends RuntimeException {
    public EntityNotProcessableException() {
        super();
    }

    public EntityNotProcessableException(String message) {
        super(message);
    }

    public EntityNotProcessableException(String message, Throwable cause) {
        super(message, cause);
    }
}
