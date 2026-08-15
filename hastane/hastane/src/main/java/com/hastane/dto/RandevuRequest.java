package com.hastane.dto;

import java.util.UUID;

public class RandevuRequest {

    private UUID doktorOid;
    private UUID hastaOid;
    private UUID hastaneOid;
    private Integer randevuTarihi;
    private Integer randevuSaati;

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
}