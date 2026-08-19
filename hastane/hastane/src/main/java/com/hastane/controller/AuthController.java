package com.hastane.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hastane.dto.GirisRequest;
import com.hastane.dto.GirisResponse;
import com.hastane.dto.KullaniciResponse;
import com.hastane.exception.KimlikDogrulamaException;
import com.hastane.security.JwtTokenService;
import com.hastane.security.KullaniciPrincipal;
import com.hastane.security.TurnstileService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;
    private final TurnstileService turnstileService;

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtTokenService jwtTokenService,
            TurnstileService turnstileService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenService = jwtTokenService;
        this.turnstileService = turnstileService;
    }

    @PostMapping("/giris")
    public ResponseEntity<GirisResponse> giris(@RequestBody GirisRequest girisRequest) {
        if (girisRequest == null
                || girisRequest.email() == null
                || girisRequest.email().isBlank()
                || girisRequest.sifre() == null
                || girisRequest.sifre().isBlank()) {
            throw new KimlikDogrulamaException("E-posta ve sifre zorunludur.");
        }

        if (!turnstileService.dogrula(girisRequest.turnstileToken(), "staff_login")) {
            throw new KimlikDogrulamaException(
                    "Insan dogrulamasi basarisiz. Lutfen tekrar deneyin.");
        }

        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken.unauthenticated(
                            girisRequest.email().strip(),
                            girisRequest.sifre()));
        } catch (AuthenticationException exception) {
            throw new KimlikDogrulamaException("E-posta veya sifre hatali.");
        }

        KullaniciPrincipal kullanici = (KullaniciPrincipal) authentication.getPrincipal();
        if (!"DOKTOR".equals(kullanici.getRol())
                && !"MUDUR".equals(kullanici.getRol())) {
            throw new KimlikDogrulamaException(
                    "Personel girisi yalnizca doktor ve mudur hesaplarina aciktir.");
        }
        return ResponseEntity.ok(jwtTokenService.accessTokenOlustur(kullanici));
    }

    @GetMapping("/ben")
    public KullaniciResponse oturumdakiKullanici(@AuthenticationPrincipal Jwt jwt) {
        return new KullaniciResponse(
                UUID.fromString(jwt.getSubject()),
                jwt.getClaimAsString("email"),
                jwt.getClaimAsString("rol"));
    }
}
