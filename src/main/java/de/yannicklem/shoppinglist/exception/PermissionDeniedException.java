package de.yannicklem.shoppinglist.exception;

import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(HttpStatus.FORBIDDEN)
public class PermissionDeniedException extends RuntimeException {

    public PermissionDeniedException() {

        super();
    }


    public PermissionDeniedException(String message) {

        super(message);
    }


    public PermissionDeniedException(String message, Throwable cause) {

        super(message, cause);
    }


    public PermissionDeniedException(Throwable cause) {

        super(cause);
    }


    public PermissionDeniedException(String message, Throwable cause, boolean enableSuppression,
        boolean writableStackTrace) {

        super(message, cause, enableSuppression, writableStackTrace);
    }
}
