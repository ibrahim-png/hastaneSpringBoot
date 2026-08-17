package com.hastane.dto;

import java.util.UUID;

public record GirisResponse(
        String accessToken,
        String tokenType,
        long expiresIn,
        UUID kullaniciOid,
        String email,
        String rol) {
}
