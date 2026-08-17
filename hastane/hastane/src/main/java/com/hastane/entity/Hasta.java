package com.hastane.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "hasta")
public class Hasta {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID oid;

    private Short status;
    private String ad;
    private String soyad;
    private String tckn;
    private String telefon;

    @Column(name = "process_date")
    private Integer processDate;

    @Column(name = "process_time")
    private Integer processTime;

    public UUID getOid() { return oid; }
    public void setOid(UUID oid) { this.oid = oid; }
    public Short getStatus() { return status; }
    public void setStatus(Short status) { this.status = status; }
    public String getAd() { return ad; }
    public void setAd(String ad) { this.ad = ad; }
    public String getSoyad() { return soyad; }
    public void setSoyad(String soyad) { this.soyad = soyad; }
    public String getTckn() { return tckn; }
    public void setTckn(String tckn) { this.tckn = tckn; }
    public String getTelefon() { return telefon; }
    public void setTelefon(String telefon) { this.telefon = telefon; }
    public Integer getProcessDate() { return processDate; }
    public void setProcessDate(Integer processDate) { this.processDate = processDate; }
    public Integer getProcessTime() { return processTime; }
    public void setProcessTime(Integer processTime) { this.processTime = processTime; }
}
