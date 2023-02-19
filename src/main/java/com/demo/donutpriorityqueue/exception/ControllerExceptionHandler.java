package com.demo.donutpriorityqueue.exception;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.NoResultException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerExceptionHandler {

    /**
     * Handles NOT_FOUND exceptions
     * @param e exception
     * @return the response dto of an error
     */
    @ExceptionHandler(NoResultException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse catchNotFoundException(Exception e) {
        return new ErrorResponse(HttpStatus.NOT_FOUND, e.getMessage());
    }

    /**
     * Handles CONFLICT exceptions
     * @param e exception
     * @return the response dto of an error
     */
    @ExceptionHandler(EntityExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse catchConflictException(Exception e) {
        return new ErrorResponse(HttpStatus.CONFLICT, e.getMessage());
    }

    /**
     * Handles IllegalStateException exceptions
     * @param e exception
     * @return the response dto of an error
     */
    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse catchBadRequestException(Exception e) {
        return new ErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }
}
