package com.hastane.dto;

import java.util.UUID;

public record PersonelRandevuResponse(
        UUID randevuOid,
        Integer randevuTarihi,
        Integer randevuSaati,
        String hastaAdSoyad,
        String doktorAdSoyad,
        String brans,
        String durum) {
}
