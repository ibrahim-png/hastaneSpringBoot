package com.hastane.dto;

import java.util.UUID;

public record MisafirRandevuResponse(
        UUID randevuOid,
        Integer randevuTarihi,
        Integer randevuSaati,
        String doktor,
        String durum) {
}
