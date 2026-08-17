package com.hastane.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hastane.dto.DoktorGuncelleRequest;
import com.hastane.dto.DoktorRequest;
import com.hastane.entity.Doktor;
import com.hastane.exception.DoktorBulunamadiException;
import com.hastane.exception.GecersizDoktorBilgisiException;
import com.hastane.repository.DoktorRepository;

@Service
public class DoktorService {

    private static final short AKTIF = 1;
    private static final int VARSAYILAN_RANDEVU_SURESI_DK = 30;

    private final DoktorRepository doktorRepository;

    public DoktorService(DoktorRepository doktorRepository) {
        this.doktorRepository = doktorRepository;
    }

    @Transactional
    public Doktor doktorEkle(DoktorRequest request) {
        doktorBilgileriniDogrula(request);

        LocalDateTime now = LocalDateTime.now();

        Doktor doktor = new Doktor();
        doktor.setStatus(AKTIF);
        doktor.setHastaneOid(request.getHastaneOid());
        doktor.setBransOid(request.getBransOid());
        doktor.setKullaniciOid(request.getKullaniciOid());
        doktor.setAd(request.getAd().strip());
        doktor.setSoyad(request.getSoyad().strip());
        doktor.setUnvan(request.getUnvan() == null ? null : request.getUnvan().strip());
        doktor.setRandevuSuresiDk(
                request.getRandevuSuresiDk() == null
                        ? VARSAYILAN_RANDEVU_SURESI_DK
                        : request.getRandevuSuresiDk());
        doktor.setAktif(AKTIF);
        doktor.setCreatedDate(
                now.getYear() * 10000
                        + now.getMonthValue() * 100
                        + now.getDayOfMonth());
        doktor.setCreatedTime(
                now.getHour() * 10000
                        + now.getMinute() * 100
                        + now.getSecond());

        return doktorRepository.save(doktor);
    }

    @Transactional(readOnly = true)
    public List<Doktor> hastanedekiAktifDoktorlariGetir(UUID hastaneOid) {
        if (hastaneOid == null) {
            throw new GecersizDoktorBilgisiException("Hastane OID zorunludur.");
        }

        return doktorRepository.findByHastaneOidAndAktif(hastaneOid, AKTIF);
    }

    @Transactional(readOnly = true)
    public List<Doktor> hastaneVeBranstakiAktifDoktorlariGetir(
            UUID hastaneOid,
            UUID bransOid) {
        if (hastaneOid == null) {
            throw new GecersizDoktorBilgisiException("Hastane OID zorunludur.");
        }
        if (bransOid == null) {
            throw new GecersizDoktorBilgisiException("Branş OID zorunludur.");
        }

        return doktorRepository.findByHastaneOidAndBransOidAndAktif(
                hastaneOid,
                bransOid,
                AKTIF);
    }

    @Transactional
    public Doktor doktorGuncelle(UUID doktorOid, DoktorGuncelleRequest request) {
        guncellenecekDoktorBilgileriniDogrula(request);

        Doktor doktor = doktorRepository.findById(doktorOid)
                .orElseThrow(() -> new DoktorBulunamadiException(
                        "Belirtilen OID ile doktor bulunamadı."));

        if (request.getHastaneOid() != null) {
            doktor.setHastaneOid(request.getHastaneOid());
        }
        if (request.getBransOid() != null) {
            doktor.setBransOid(request.getBransOid());
        }
        if (request.getKullaniciOid() != null) {
            doktor.setKullaniciOid(request.getKullaniciOid());
        }
        if (request.getAd() != null) {
            doktor.setAd(request.getAd().strip());
        }
        if (request.getSoyad() != null) {
            doktor.setSoyad(request.getSoyad().strip());
        }
        if (request.getUnvan() != null) {
            doktor.setUnvan(request.getUnvan().strip());
        }
        if (request.getRandevuSuresiDk() != null) {
            doktor.setRandevuSuresiDk(request.getRandevuSuresiDk());
        }
        if (request.getAktif() != null) {
            doktor.setAktif(request.getAktif());
        }

        return doktorRepository.save(doktor);
    }

    private void doktorBilgileriniDogrula(DoktorRequest request) {
        if (request == null) {
            throw new GecersizDoktorBilgisiException("Doktor bilgileri zorunludur.");
        }
        if (request.getHastaneOid() == null) {
            throw new GecersizDoktorBilgisiException("Hastane OID zorunludur.");
        }
        if (request.getBransOid() == null) {
            throw new GecersizDoktorBilgisiException("Branş OID zorunludur.");
        }
        if (request.getKullaniciOid() == null) {
            throw new GecersizDoktorBilgisiException("Kullanıcı OID zorunludur.");
        }
        if (request.getAd() == null || request.getAd().isBlank()) {
            throw new GecersizDoktorBilgisiException("Doktor adı zorunludur.");
        }
        if (request.getSoyad() == null || request.getSoyad().isBlank()) {
            throw new GecersizDoktorBilgisiException("Doktor soyadı zorunludur.");
        }
        if (request.getRandevuSuresiDk() != null && request.getRandevuSuresiDk() <= 0) {
            throw new GecersizDoktorBilgisiException(
                    "Randevu süresi sıfırdan büyük olmalıdır.");
        }
    }

    private void guncellenecekDoktorBilgileriniDogrula(DoktorGuncelleRequest request) {
        if (request == null) {
            throw new GecersizDoktorBilgisiException("Güncellenecek doktor bilgileri zorunludur.");
        }
        if (request.getHastaneOid() == null
                && request.getBransOid() == null
                && request.getKullaniciOid() == null
                && request.getAd() == null
                && request.getSoyad() == null
                && request.getUnvan() == null
                && request.getRandevuSuresiDk() == null
                && request.getAktif() == null) {
            throw new GecersizDoktorBilgisiException(
                    "Güncellenecek en az bir doktor bilgisi gönderilmelidir.");
        }
        if (request.getAd() != null && request.getAd().isBlank()) {
            throw new GecersizDoktorBilgisiException("Doktor adı boş olamaz.");
        }
        if (request.getSoyad() != null && request.getSoyad().isBlank()) {
            throw new GecersizDoktorBilgisiException("Doktor soyadı boş olamaz.");
        }
        if (request.getRandevuSuresiDk() != null && request.getRandevuSuresiDk() <= 0) {
            throw new GecersizDoktorBilgisiException(
                    "Randevu süresi sıfırdan büyük olmalıdır.");
        }
        if (request.getAktif() != null
                && request.getAktif() != 0
                && request.getAktif() != 1) {
            throw new GecersizDoktorBilgisiException(
                    "Aktif değeri 0 veya 1 olmalıdır.");
        }
    }
}
