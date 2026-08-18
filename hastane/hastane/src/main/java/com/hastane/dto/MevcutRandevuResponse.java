package com.hastane.dto;

import java.util.UUID;

public record MevcutRandevuResponse(
        UUID randevuOid,
        Integer randevuTarihi,
        Integer randevuSaati,
        String doktor,
        String durum) {
}
