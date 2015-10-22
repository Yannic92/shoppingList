package de.yannicklem.shoppinglist.exception;

import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(HttpStatus.BAD_REQUEST)
public class EntityInvalidException extends RuntimeException {

    public EntityInvalidException() {

        super();
    }


    public EntityInvalidException(String message) {

        super(message);
    }


    public EntityInvalidException(String message, Throwable cause) {

        super(message, cause);
    }


    public EntityInvalidException(Throwable cause) {

        super(cause);
    }


    public EntityInvalidException(String message, Throwable cause, boolean enableSuppression,
        boolean writableStackTrace) {

        super(message, cause, enableSuppression, writableStackTrace);
    }
}
