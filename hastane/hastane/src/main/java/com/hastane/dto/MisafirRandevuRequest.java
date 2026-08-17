package com.hastane.dto;

import java.util.UUID;

public record MisafirRandevuRequest(
        String ad,
        String soyad,
        String tckn,
        String telefon,
        UUID doktorOid,
        Integer randevuTarihi,
        Integer randevuSaati) {
}
