package com.hastane.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler({
            GecersizDoktorBilgisiException.class,
            GecersizRandevuBilgisiException.class
    })
    public ResponseEntity<HataResponse> gecersizIstek(RuntimeException exception) {
        return hata(HttpStatus.BAD_REQUEST, exception);
    }

    @ExceptionHandler(KimlikDogrulamaException.class)
    public ResponseEntity<HataResponse> kimlikDogrulama(RuntimeException exception) {
        return hata(HttpStatus.UNAUTHORIZED, exception);
    }

    @ExceptionHandler(DoktorBulunamadiException.class)
    public ResponseEntity<HataResponse> bulunamadi(RuntimeException exception) {
        return hata(HttpStatus.NOT_FOUND, exception);
    }

    @ExceptionHandler(RandevuCakismaException.class)
    public ResponseEntity<HataResponse> cakisma(RuntimeException exception) {
        return hata(HttpStatus.CONFLICT, exception);
    }

    private ResponseEntity<HataResponse> hata(
            HttpStatus status,
            RuntimeException exception) {
        return ResponseEntity.status(status)
                .body(new HataResponse(exception.getMessage()));
    }

    public record HataResponse(String mesaj) {
    }
}
