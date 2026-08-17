package com.hastane.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "doktor")
public class Doktor {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "oid")
    private UUID oid;

    private Short status;

    @Column(name = "hastane_oid")
    private UUID hastaneOid;

    @Column(name = "brans_oid")
    private UUID bransOid;

    @Column(name = "kullanici_oid")
    private UUID kullaniciOid;

    private String ad;

    private String soyad;

    private String unvan;

    @Column(name = "randevu_suresi_dk")
    private Integer randevuSuresiDk;

    private Short aktif;

    @Column(name = "created_date")
    private Integer createdDate;

    @Column(name = "created_time")
    private Integer createdTime;

    public UUID getOid() {
        return oid;
    }

    public void setOid(UUID oid) {
        this.oid = oid;
    }

    public Short getStatus() {
        return status;
    }

    public void setStatus(Short status) {
        this.status = status;
    }

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

    public Short getAktif() {
        return aktif;
    }

    public void setAktif(Short aktif) {
        this.aktif = aktif;
    }

    public Integer getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Integer createdDate) {
        this.createdDate = createdDate;
    }

    public Integer getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Integer createdTime) {
        this.createdTime = createdTime;
    }
}
