package com.hastane.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class DoktorBulunamadiException extends RuntimeException {

    public DoktorBulunamadiException(String message) {
        super(message);
    }
}
