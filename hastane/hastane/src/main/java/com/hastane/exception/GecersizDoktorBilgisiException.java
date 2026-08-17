package com.hastane.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class GecersizDoktorBilgisiException extends RuntimeException {

    public GecersizDoktorBilgisiException(String message) {
        super(message);
    }
}
