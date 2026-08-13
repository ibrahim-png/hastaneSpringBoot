package com.hastane.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "randevu")
public class Randevu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Short status;

    @Column(name = "doktor_id")
    private Long doktorId;

    @Column(name = "hasta_id")
    private Long hastaId;

    @Column(name = "hastane_id")
    private Long hastaneId;

    @Column(name = "randevu_tarihi")
    private Integer randevuTarihi;

    @Column(name = "randevu_saati")
    private Integer randevuSaati;

    @Column(name = "process_date")
    private Integer processDate;

    @Column(name = "process_time")
    private Integer processTime;

    private String durum;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Short getStatus() {
        return status;
    }

    public void setStatus(Short status) {
        this.status = status;
    }

    public Long getDoktorId() {
        return doktorId;
    }

    public void setDoktorId(Long doktorId) {
        this.doktorId = doktorId;
    }

    public Long getHastaId() {
        return hastaId;
    }

    public void setHastaId(Long hastaId) {
        this.hastaId = hastaId;
    }

    public Long getHastaneId() {
        return hastaneId;
    }

    public void setHastaneId(Long hastaneId) {
        this.hastaneId = hastaneId;
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