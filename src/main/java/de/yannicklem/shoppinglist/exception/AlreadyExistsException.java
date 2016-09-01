package de.yannicklem.shoppinglist.exception;

import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AlreadyExistsException extends RuntimeException {

    public AlreadyExistsException() {

        super();
    }


    public AlreadyExistsException(String message) {

        super(message);
    }


    public AlreadyExistsException(String message, Throwable cause) {

        super(message, cause);
    }


    public AlreadyExistsException(Throwable cause) {

        super(cause);
    }


    public AlreadyExistsException(String message, Throwable cause, boolean enableSuppression,
        boolean writableStackTrace) {

        super(message, cause, enableSuppression, writableStackTrace);
    }
}
