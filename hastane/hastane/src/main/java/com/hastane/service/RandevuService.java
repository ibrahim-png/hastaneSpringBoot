package com.hastane.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.hastane.entity.Randevu;
import com.hastane.repository.RandevuRepository;

@Service
public class RandevuService {

    private final RandevuRepository randevuRepository;

    public RandevuService(RandevuRepository randevuRepository) {
        this.randevuRepository = randevuRepository;
    }

    public Randevu randevuOlustur(
            UUID doktorOid,
            UUID hastaOid,
            UUID hastaneOid,
            Integer randevuTarihi,
            Integer randevuSaati) {

        LocalDateTime now = LocalDateTime.now();

        int processDate =
                now.getYear() * 10000
                + now.getMonthValue() * 100
                + now.getDayOfMonth();

        int processTime =
                now.getHour() * 10000
                + now.getMinute() * 100
                + now.getSecond();

        Randevu randevu = new Randevu();

        randevu.setStatus((short) 1);
        randevu.setDoktorOid(doktorOid);
        randevu.setHastaOid(hastaOid);
        randevu.setHastaneOid(hastaneOid);
        randevu.setRandevuTarihi(randevuTarihi);
        randevu.setRandevuSaati(randevuSaati);
        randevu.setProcessDate(processDate);
        randevu.setProcessTime(processTime);
        randevu.setDurum("AKTIF");

        return randevuRepository.save(randevu);
    }
}