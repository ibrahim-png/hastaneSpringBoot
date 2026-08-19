package com.hastane.dto;

public record HastaBilgileriRequest(
        String ad,
        String soyad,
        String tckn,
        String telefon,
        String turnstileToken) {

    public HastaBilgileriRequest(
            String ad,
            String soyad,
            String tckn,
            String telefon) {
        this(ad, soyad, tckn, telefon, null);
    }
}
