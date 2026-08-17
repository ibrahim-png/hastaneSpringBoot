package com.hastane.dto;

import java.util.UUID;

public record DoktorSecimResponse(
        UUID oid,
        String ad,
        String soyad,
        String unvan,
        Integer randevuSuresiDk) {
}
