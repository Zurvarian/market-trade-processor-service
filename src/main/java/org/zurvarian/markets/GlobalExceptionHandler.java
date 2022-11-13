package org.zurvarian.markets;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.ConstraintViolationException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    HttpStatus handleConstraintViolationException(ConstraintViolationException e) {
        throw new ResponseStatusException(BAD_REQUEST, e.getMessage(), e);
    }
}
