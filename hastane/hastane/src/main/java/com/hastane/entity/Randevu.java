package com.hastane.entity;

import java.util.UUID;

import jakarta.persistence.*;

@Entity
@Table(name = "randevu")
public class Randevu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "oid")
    private UUID oid;

    private Short status;

    @Column(name = "doktor_oid")
    private UUID doktorOid;

    @Column(name = "hasta_oid")
    private UUID hastaOid;

    @Column(name = "hastane_oid")
    private UUID hastaneOid;

    @Column(name = "randevu_tarihi")
    private Integer randevuTarihi;

    @Column(name = "randevu_saati")
    private Integer randevuSaati;

    @Column(name = "process_date")
    private Integer processDate;

    @Column(name = "process_time")
    private Integer processTime;

    private String durum;


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

    public UUID getDoktorOid() {
        return doktorOid;
    }

    public void setDoktorOid(UUID doktorOid) {
        this.doktorOid = doktorOid;
    }

    public UUID getHastaOid() {
        return hastaOid;
    }

    public void setHastaOid(UUID hastaOid) {
        this.hastaOid = hastaOid;
    }

    public UUID getHastaneOid() {
        return hastaneOid;
    }

    public void setHastaneOid(UUID hastaneOid) {
        this.hastaneOid = hastaneOid;
    }

    public Integer getRandevuTarihi() {
        return randevuTarihi;
    }

    public void setRandevuTarihi(Integer randevuTarihi) {
        this.randevuTarihi = randevuTarihi;
    }

    public Integer getRandevuSaati() {
        return randevuSaati;
    }

    public void setRandevuSaati(Integer randevuSaati) {
        this.randevuSaati = randevuSaati;
    }

    public Integer getProcessDate() {
        return processDate;
    }

    public void setProcessDate(Integer processDate) {
        this.processDate = processDate;
    }

    public Integer getProcessTime() {
        return processTime;
    }

    public void setProcessTime(Integer processTime) {
        this.processTime = processTime;
    }

    public String getDurum() {
        return durum;
    }

    public void setDurum(String durum) {
        this.durum = durum;
    }
}