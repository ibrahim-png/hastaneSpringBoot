package com.hastane.dto;

import java.util.UUID;

public class DoktorRequest {

    private UUID hastaneOid;
    private UUID bransOid;
    private UUID kullaniciOid;
    private String ad;
    private String soyad;
    private String unvan;
    private Integer randevuSuresiDk;

    public UUID getHastaneOid() {
        return hastaneOid;
    }

    public void setHastaneOid(UUID hastaneOid) {
        this.hastaneOid = hastaneOid;
    }

    public UUID getBransOid() {
        return bransOid;
    }

    public void setBransOid(UUID bransOid) {
        this.bransOid = bransOid;
    }

    public UUID getKullaniciOid() {
        return kullaniciOid;
    }

    public void setKullaniciOid(UUID kullaniciOid) {
        this.kullaniciOid = kullaniciOid;
    }

    public String getAd() {
        return ad;
    }

    public void setAd(String ad) {
        this.ad = ad;
    }

    public String getSoyad() {
        return soyad;
    }

    public void setSoyad(String soyad) {
        this.soyad = soyad;
    }

    public String getUnvan() {
        return unvan;
    }

    public void setUnvan(String unvan) {
        this.unvan = unvan;
    }

    public Integer getRandevuSuresiDk() {
        return randevuSuresiDk;
    }

    public void setRandevuSuresiDk(Integer randevuSuresiDk) {
        this.randevuSuresiDk = randevuSuresiDk;
    }
}
