package com.hastane.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hastane.dto.BransResponse;
import com.hastane.dto.DoktorSecimResponse;
import com.hastane.dto.HastaBilgileriRequest;
import com.hastane.dto.MevcutRandevuResponse;
import com.hastane.dto.MisafirRandevuRequest;
import com.hastane.dto.MisafirRandevuResponse;
import com.hastane.dto.MusaitSaatResponse;
import com.hastane.entity.Doktor;
import com.hastane.entity.Hasta;
import com.hastane.entity.Randevu;
import com.hastane.exception.DoktorBulunamadiException;
import com.hastane.exception.GecersizRandevuBilgisiException;
import com.hastane.exception.RandevuCakismaException;
import com.hastane.repository.BransRepository;
import com.hastane.repository.DoktorRepository;
import com.hastane.repository.HastaRepository;
import com.hastane.repository.RandevuRepository;

@Service
public class MisafirRandevuService {

    private static final short AKTIF = 1;
    private static final ZoneId ISTANBUL = ZoneId.of("Europe/Istanbul");
    private static final LocalTime MESAI_BASLANGICI = LocalTime.of(9, 0);
    private static final LocalTime MESAI_BITISI = LocalTime.of(17, 0);

    private final BransRepository bransRepository;
    private final DoktorRepository doktorRepository;
    private final HastaRepository hastaRepository;
    private final RandevuRepository randevuRepository;

    public MisafirRandevuService(
            BransRepository bransRepository,
            DoktorRepository doktorRepository,
            HastaRepository hastaRepository,
            RandevuRepository randevuRepository) {
        this.bransRepository = bransRepository;
        this.doktorRepository = doktorRepository;
        this.hastaRepository = hastaRepository;
        this.randevuRepository = randevuRepository;
    }

    @Transactional(readOnly = true)
    public List<BransResponse> aktifBranslariGetir() {
        return bransRepository.findByStatusOrderByAdAsc(AKTIF)
                .stream()
                .map(brans -> new BransResponse(
                        brans.getOid(),
                        brans.getAd(),
                        brans.getHastaneOid()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<DoktorSecimResponse> branstakiAktifDoktorlariGetir(UUID bransOid) {
        if (bransOid == null) {
            throw new GecersizRandevuBilgisiException("Brans OID zorunludur.");
        }

        return doktorRepository.findByBransOidAndAktifOrderByAdAscSoyadAsc(bransOid, AKTIF)
                .stream()
                .map(doktor -> new DoktorSecimResponse(
                        doktor.getOid(),
                        doktor.getAd(),
                        doktor.getSoyad(),
                        doktor.getUnvan(),
                        doktor.getRandevuSuresiDk()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MusaitSaatResponse> doktorunSaatleriniGetir(
            UUID doktorOid,
            Integer randevuTarihi) {
        Doktor doktor = aktifDoktoruGetir(doktorOid);
        LocalDate tarih = randevuTarihiniDogrula(randevuTarihi);
        return saatleriHesapla(doktor, tarih);
    }

    @Transactional(readOnly = true)
    public List<MevcutRandevuResponse> hastaninAktifRandevulariniGetir(
            HastaBilgileriRequest request) {
        hastaBilgileriniDogrula(
                request == null ? null : request.ad(),
                request == null ? null : request.soyad(),
                request == null ? null : request.tckn(),
                request == null ? null : request.telefon());

        Hasta hasta = hastaRepository.findByTckn(request.tckn().strip())
                .orElse(null);
        if (hasta == null) {
            return List.of();
        }

        LocalDateTime simdi = LocalDateTime.now(ISTANBUL);
        int bugun = tarihSayisi(simdi.toLocalDate());
        int suAn = saatSayisi(simdi.toLocalTime());
        List<Randevu> randevular = randevuRepository
                .findByHastaOidAndDurumOrderByRandevuTarihiAscRandevuSaatiAsc(
                        hasta.getOid(),
                        "AKTIF")
                .stream()
                .filter(randevu -> randevu.getRandevuTarihi() > bugun
                        || (randevu.getRandevuTarihi() == bugun
                                && randevu.getRandevuSaati() > suAn))
                .toList();

        Map<UUID, Doktor> doktorlar = doktorRepository.findAllById(
                        randevular.stream().map(Randevu::getDoktorOid).distinct().toList())
                .stream()
                .collect(Collectors.toMap(Doktor::getOid, doktor -> doktor));

        return randevular.stream()
                .map(randevu -> mevcutRandevuResponseOlustur(
                        randevu,
                        doktorlar.get(randevu.getDoktorOid())))
                .toList();
    }

    @Transactional
    public void randevuIptalEt(
            UUID randevuOid,
            HastaBilgileriRequest request) {
        if (randevuOid == null) {
            throw new GecersizRandevuBilgisiException("Randevu OID zorunludur.");
        }
        hastaBilgileriniDogrula(
                request == null ? null : request.ad(),
                request == null ? null : request.soyad(),
                request == null ? null : request.tckn(),
                request == null ? null : request.telefon());

        Hasta hasta = hastaRepository.findByTckn(request.tckn().strip())
                .orElseThrow(() -> new GecersizRandevuBilgisiException(
                        "Bu TCKN ile kayitli hasta bulunamadi."));

        Randevu randevu = randevuRepository.findById(randevuOid)
                .filter(mevcut -> mevcut.getHastaOid().equals(hasta.getOid()))
                .filter(mevcut -> "AKTIF".equals(mevcut.getDurum()))
                .orElseThrow(() -> new GecersizRandevuBilgisiException(
                        "Iptal edilebilecek aktif randevu bulunamadi."));

        randevu.setDurum("IPTAL");
        randevu.setStatus((short) 0);
        randevuRepository.save(randevu);
    }

    @Transactional
    public MisafirRandevuResponse randevuOlustur(MisafirRandevuRequest request) {
        bilgileriDogrula(request);
        Doktor doktor = aktifDoktoruKilitleyerekGetir(request.doktorOid());
        LocalDate tarih = randevuTarihiniDogrula(request.randevuTarihi());

        boolean saatMusait = saatleriHesapla(doktor, tarih).stream()
                .anyMatch(saat -> saat.saat().equals(request.randevuSaati()) && saat.musait());
        if (!saatMusait) {
            throw new RandevuCakismaException(
                    "Secilen randevu saati artik musait degildir.");
        }

        Hasta hasta = hastaRepository.findByTckn(request.tckn().strip())
                .orElseGet(() -> hastaRepository.save(yeniHasta(request)));

        LocalDateTime now = LocalDateTime.now(ISTANBUL);
        Randevu randevu = new Randevu();
        randevu.setStatus(AKTIF);
        randevu.setDoktorOid(doktor.getOid());
        randevu.setHastaOid(hasta.getOid());
        randevu.setHastaneOid(doktor.getHastaneOid());
        randevu.setRandevuTarihi(request.randevuTarihi());
        randevu.setRandevuSaati(request.randevuSaati());
        randevu.setProcessDate(tarihSayisi(now.toLocalDate()));
        randevu.setProcessTime(saatSayisi(now.toLocalTime()));
        randevu.setDurum("AKTIF");
        Randevu kaydedilen = randevuRepository.save(randevu);

        String doktorAdi = java.util.stream.Stream.of(
                        doktor.getUnvan(),
                        doktor.getAd(),
                        doktor.getSoyad())
                .filter(deger -> deger != null && !deger.isBlank())
                .collect(Collectors.joining(" "));
        return new MisafirRandevuResponse(
                kaydedilen.getOid(),
                kaydedilen.getRandevuTarihi(),
                kaydedilen.getRandevuSaati(),
                doktorAdi,
                kaydedilen.getDurum());
    }

    private List<MusaitSaatResponse> saatleriHesapla(Doktor doktor, LocalDate tarih) {
        int sure = doktor.getRandevuSuresiDk() == null ? 30 : doktor.getRandevuSuresiDk();
        if (sure <= 0) {
            throw new GecersizRandevuBilgisiException(
                    "Doktorun randevu suresi gecersizdir.");
        }

        Set<Integer> doluSaatler = randevuRepository
                .findByDoktorOidAndRandevuTarihiAndDurumOrderByRandevuSaatiAsc(
                        doktor.getOid(),
                        tarihSayisi(tarih),
                        "AKTIF")
                .stream()
                .map(Randevu::getRandevuSaati)
                .collect(Collectors.toSet());

        LocalDate bugun = LocalDate.now(ISTANBUL);
        LocalTime simdi = LocalTime.now(ISTANBUL);
        boolean haftaSonu = tarih.getDayOfWeek() == DayOfWeek.SATURDAY
                || tarih.getDayOfWeek() == DayOfWeek.SUNDAY;

        List<MusaitSaatResponse> saatler = new ArrayList<>();
        for (LocalTime saat = MESAI_BASLANGICI;
                !saat.plusMinutes(sure).isAfter(MESAI_BITISI);
                saat = saat.plusMinutes(sure)) {
            int saatDegeri = saatSayisi(saat);
            boolean gecmis = tarih.isBefore(bugun)
                    || (tarih.equals(bugun) && !saat.isAfter(simdi));
            saatler.add(new MusaitSaatResponse(
                    saatDegeri,
                    !haftaSonu && !gecmis && !doluSaatler.contains(saatDegeri)));
        }
        return saatler;
    }

    private Doktor aktifDoktoruGetir(UUID doktorOid) {
        if (doktorOid == null) {
            throw new GecersizRandevuBilgisiException("Doktor OID zorunludur.");
        }
        return doktorRepository.findByOidAndAktif(doktorOid, AKTIF)
                .orElseThrow(() -> new DoktorBulunamadiException(
                        "Aktif doktor bulunamadi."));
    }

    private Doktor aktifDoktoruKilitleyerekGetir(UUID doktorOid) {
        if (doktorOid == null) {
            throw new GecersizRandevuBilgisiException("Doktor OID zorunludur.");
        }
        return doktorRepository.kilitleyerekBul(doktorOid, AKTIF)
                .orElseThrow(() -> new DoktorBulunamadiException(
                        "Aktif doktor bulunamadi."));
    }

    private Hasta yeniHasta(MisafirRandevuRequest request) {
        LocalDateTime now = LocalDateTime.now(ISTANBUL);
        Hasta hasta = new Hasta();
        hasta.setStatus(AKTIF);
        hasta.setAd(request.ad().strip());
        hasta.setSoyad(request.soyad().strip());
        hasta.setTckn(request.tckn().strip());
        hasta.setTelefon(telefonuNormalizeEt(request.telefon()));
        hasta.setProcessDate(tarihSayisi(now.toLocalDate()));
        hasta.setProcessTime(saatSayisi(now.toLocalTime()));
        return hasta;
    }

    private void bilgileriDogrula(MisafirRandevuRequest request) {
        if (request == null) {
            throw new GecersizRandevuBilgisiException("Randevu bilgileri zorunludur.");
        }
        hastaBilgileriniDogrula(
                request.ad(),
                request.soyad(),
                request.tckn(),
                request.telefon());
        if (request.randevuSaati() == null) {
            throw new GecersizRandevuBilgisiException("Randevu saati zorunludur.");
        }
    }

    private void hastaBilgileriniDogrula(
            String ad,
            String soyad,
            String tckn,
            String telefon) {
        if (ad == null || ad.isBlank() || soyad == null || soyad.isBlank()) {
            throw new GecersizRandevuBilgisiException("Ad ve soyad zorunludur.");
        }
        if (tckn == null || !tckn.matches("[0-9]{11}")) {
            throw new GecersizRandevuBilgisiException("TCKN 11 rakamdan olusmalidir.");
        }
        if (telefon == null
                || !telefon.replaceAll("[^0-9]", "").matches("[0-9]{10,15}")) {
            throw new GecersizRandevuBilgisiException("Gecerli bir telefon numarasi girilmelidir.");
        }
    }

    private MevcutRandevuResponse mevcutRandevuResponseOlustur(
            Randevu randevu,
            Doktor doktor) {
        String doktorAdi = doktor == null
                ? "Doktor bilgisi bulunamadi"
                : java.util.stream.Stream.of(
                                doktor.getUnvan(),
                                doktor.getAd(),
                                doktor.getSoyad())
                        .filter(deger -> deger != null && !deger.isBlank())
                        .collect(Collectors.joining(" "));
        return new MevcutRandevuResponse(
                randevu.getOid(),
                randevu.getRandevuTarihi(),
                randevu.getRandevuSaati(),
                doktorAdi,
                randevu.getDurum());
    }

    private LocalDate randevuTarihiniDogrula(Integer tarih) {
        if (tarih == null || tarih.toString().length() != 8) {
            throw new GecersizRandevuBilgisiException("Randevu tarihi gecersizdir.");
        }
        try {
            return LocalDate.parse(tarih.toString(), DateTimeFormatter.BASIC_ISO_DATE);
        } catch (DateTimeParseException exception) {
            throw new GecersizRandevuBilgisiException("Randevu tarihi gecersizdir.");
        }
    }

    private int tarihSayisi(LocalDate tarih) {
        return tarih.getYear() * 10000 + tarih.getMonthValue() * 100 + tarih.getDayOfMonth();
    }

    private int saatSayisi(LocalTime saat) {
        return saat.getHour() * 10000 + saat.getMinute() * 100 + saat.getSecond();
    }

    private String telefonuNormalizeEt(String telefon) {
        return telefon == null ? "" : telefon.replaceAll("[^0-9+]", "");
    }
}
