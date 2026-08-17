package com.hastane.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.Duration;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;

import com.hastane.config.JwtProperties;
import com.hastane.config.SecurityConfig;
import com.hastane.dto.GirisResponse;
import com.hastane.entity.Kullanici;

class JwtTokenServiceTests {

    @Test
    void kullaniciBilgileriyleImzaliJwtOlusturur() {
        JwtProperties properties = new JwtProperties();
        properties.setSecretBase64(
                "MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=");
        properties.setIssuer("hastane-api");
        properties.setAudience("hastane-client");
        properties.setAccessTokenTtl(Duration.ofMinutes(15));

        SecurityConfig config = new SecurityConfig();
        SecretKey secretKey = config.jwtSecretKey(properties);
        JwtEncoder encoder = config.jwtEncoder(secretKey);
        JwtDecoder decoder = config.jwtDecoder(secretKey, properties);

        Kullanici kullanici = new Kullanici();
        kullanici.setOid(UUID.randomUUID());
        kullanici.setEmail("doktor@hastane.com");
        kullanici.setPasswordHash("ornek-hash");
        kullanici.setRol("DOKTOR");
        kullanici.setAktif((short) 1);

        JwtTokenService service = new JwtTokenService(encoder, properties);
        GirisResponse response = service.accessTokenOlustur(new KullaniciPrincipal(kullanici));
        Jwt jwt = decoder.decode(response.accessToken());

        assertNotNull(response.accessToken());
        assertEquals("Bearer", response.tokenType());
        assertEquals(900, response.expiresIn());
        assertEquals(kullanici.getOid().toString(), jwt.getSubject());
        assertEquals("DOKTOR", jwt.getClaimAsString("rol"));
        assertEquals("doktor@hastane.com", jwt.getClaimAsString("email"));
    }
}
