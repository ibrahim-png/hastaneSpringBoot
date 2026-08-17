package com.hastane.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class KimlikDogrulamaException extends RuntimeException {

    public KimlikDogrulamaException(String message) {
        super(message);
    }
}
