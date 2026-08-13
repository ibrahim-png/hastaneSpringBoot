package com.hastane.dto;

public class RandevuRequest {

    private Long doktorId;
    private Long hastaId;
    private Long hastaneId;
    private Integer randevuTarihi;
    private Integer randevuSaati;

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
}