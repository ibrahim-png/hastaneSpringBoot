package com.hastane.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "brans")
public class Brans {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID oid;

    private Short status;
    private String ad;

    @Column(name = "hastane_oid")
    private UUID hastaneOid;

    public UUID getOid() { return oid; }
    public void setOid(UUID oid) { this.oid = oid; }
    public Short getStatus() { return status; }
    public void setStatus(Short status) { this.status = status; }
    public String getAd() { return ad; }
    public void setAd(String ad) { this.ad = ad; }
    public UUID getHastaneOid() { return hastaneOid; }
    public void setHastaneOid(UUID hastaneOid) { this.hastaneOid = hastaneOid; }
}
