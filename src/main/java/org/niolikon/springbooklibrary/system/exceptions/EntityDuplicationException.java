package org.niolikon.springbooklibrary.system.exceptions;

public class EntityDuplicationException extends RuntimeException {
    public EntityDuplicationException() {
        super();
    }

    public EntityDuplicationException(String message) {
        super(message);
    }

    public EntityDuplicationException(String message, Throwable cause) {
        super(message, cause);
    }
}
