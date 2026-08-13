package com.hastane.service;

import java.time.LocalDateTime;

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
            Long doktorId,
            Long hastaId,
            Long hastaneId,
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
        randevu.setDoktorId(doktorId);
        randevu.setHastaId(hastaId);
        randevu.setHastaneId(hastaneId);
        randevu.setRandevuTarihi(randevuTarihi);
        randevu.setRandevuSaati(randevuSaati);
        randevu.setProcessDate(processDate);
        randevu.setProcessTime(processTime);
        randevu.setDurum("AKTIF");

        return randevuRepository.save(randevu);
    }
}