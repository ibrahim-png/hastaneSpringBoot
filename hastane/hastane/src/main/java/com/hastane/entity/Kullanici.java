package com.hastane.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "kullanici")
public class Kullanici {

    @Id
    @Column(name = "oid")
    private UUID oid;

    private Short status;

    private String email;

    @Column(name = "password_hash")
    private String passwordHash;

    private String rol;

    private Short aktif;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public Short getAktif() {
        return aktif;
    }

    public void setAktif(Short aktif) {
        this.aktif = aktif;
    }
}
