package com.hastane.controller;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;

import com.hastane.dto.GirisRequest;
import com.hastane.exception.KimlikDogrulamaException;
import com.hastane.security.JwtTokenService;
import com.hastane.security.TurnstileService;

class AuthControllerTests {

    @Test
    void turnstileReddederseParolaKontrolEdilmez() {
        AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
        JwtTokenService jwtTokenService = mock(JwtTokenService.class);
        TurnstileService turnstileService = mock(TurnstileService.class);
        when(turnstileService.dogrula("gecersiz-token")).thenReturn(false);
        AuthController controller = new AuthController(
                authenticationManager,
                jwtTokenService,
                turnstileService);

        assertThrows(
                KimlikDogrulamaException.class,
                () -> controller.giris(new GirisRequest(
                        "doktor@example.com",
                        "parola",
                        "gecersiz-token")));

        verifyNoInteractions(authenticationManager, jwtTokenService);
    }
}
