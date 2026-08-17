package com.hastane.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hastane.dto.RandevuRequest;
import com.hastane.dto.DoluSaatResponse;
import com.hastane.entity.Randevu;
import com.hastane.entity.Doktor;
import com.hastane.exception.DoktorBulunamadiException;
import com.hastane.exception.GecersizRandevuBilgisiException;
import com.hastane.exception.RandevuCakismaException;
import com.hastane.repository.DoktorRepository;
import com.hastane.repository.RandevuRepository;

@Service
public class RandevuService {

    private final RandevuRepository randevuRepository;
    private final DoktorRepository doktorRepository;

    public RandevuService(
            RandevuRepository randevuRepository,
            DoktorRepository doktorRepository) {
        this.randevuRepository = randevuRepository;
        this.doktorRepository = doktorRepository;
    }

    @Transactional
    public Randevu randevuOlustur(RandevuRequest request) {

        boolean doktorunRandevusuVar =
                randevuRepository.existsByDoktorOidAndRandevuTarihiAndRandevuSaatiAndDurum(
                        request.getDoktorOid(),
                        request.getRandevuTarihi(),
                        request.getRandevuSaati(),
                        "AKTIF");

        if (doktorunRandevusuVar) {
            throw new RandevuCakismaException(
                    "Doktorun belirtilen tarih ve saatte aktif bir randevusu bulunmaktadır.");
        }

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
        randevu.setDoktorOid(request.getDoktorOid());
        randevu.setHastaOid(request.getHastaOid());
        randevu.setHastaneOid(request.getHastaneOid());
        randevu.setRandevuTarihi(request.getRandevuTarihi());
        randevu.setRandevuSaati(request.getRandevuSaati());
        randevu.setProcessDate(processDate);
        randevu.setProcessTime(processTime);
        randevu.setDurum("AKTIF");

        return randevuRepository.save(randevu);
    }

    @Transactional(readOnly = true)
    public List<Randevu> doktorunGunlukRandevulariniGetir(
            UUID doktorOid,
            Integer randevuTarihi) {
        if (doktorOid == null) {
            throw new GecersizRandevuBilgisiException("Doktor OID zorunludur.");
        }
        randevuTarihiniDogrula(randevuTarihi);

        return randevuRepository.findByDoktorOidAndRandevuTarihiOrderByRandevuSaatiAsc(
                doktorOid,
                randevuTarihi);
    }

    @Transactional(readOnly = true)
    public List<Randevu> oturumdakiDoktorunGunlukRandevulariniGetir(
            UUID kullaniciOid,
            Integer randevuTarihi) {
        randevuTarihiniDogrula(randevuTarihi);

        Doktor doktor = doktorRepository.findByKullaniciOidAndAktif(kullaniciOid, (short) 1)
                .orElseThrow(() -> new DoktorBulunamadiException(
                        "Oturumdaki kullaniciya ait aktif doktor kaydi bulunamadi."));

        return randevuRepository.findByDoktorOidAndRandevuTarihiOrderByRandevuSaatiAsc(
                doktor.getOid(),
                randevuTarihi);
    }

    @Transactional(readOnly = true)
    public List<DoluSaatResponse> doktorunDoluSaatleriniGetir(
            UUID doktorOid,
            Integer randevuTarihi) {
        if (doktorOid == null) {
            throw new GecersizRandevuBilgisiException("Doktor OID zorunludur.");
        }
        randevuTarihiniDogrula(randevuTarihi);

        return randevuRepository
                .findByDoktorOidAndRandevuTarihiAndDurumOrderByRandevuSaatiAsc(
                        doktorOid,
                        randevuTarihi,
                        "AKTIF")
                .stream()
                .map(randevu -> new DoluSaatResponse(
                        randevu.getRandevuTarihi(),
                        randevu.getRandevuSaati()))
                .toList();
    }

    private void randevuTarihiniDogrula(Integer randevuTarihi) {
        if (randevuTarihi == null || randevuTarihi.toString().length() != 8) {
            throw new GecersizRandevuBilgisiException(
                    "Randevu tarihi YYYYMMDD formatında olmalıdır.");
        }

        try {
            LocalDate.parse(randevuTarihi.toString(), DateTimeFormatter.BASIC_ISO_DATE);
        } catch (DateTimeParseException exception) {
            throw new GecersizRandevuBilgisiException(
                    "Geçerli bir randevu tarihi gönderilmelidir.");
        }
    }
}
