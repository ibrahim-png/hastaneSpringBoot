package com.hastane.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hastane.dto.MisafirRandevuRequest;
import com.hastane.dto.MisafirRandevuResponse;
import com.hastane.dto.MusaitSaatResponse;
import com.hastane.dto.HastaBilgileriRequest;
import com.hastane.dto.MevcutRandevuResponse;
import com.hastane.entity.Doktor;
import com.hastane.entity.Hasta;
import com.hastane.entity.Randevu;
import com.hastane.exception.RandevuCakismaException;
import com.hastane.exception.GecersizRandevuBilgisiException;
import com.hastane.repository.BransRepository;
import com.hastane.repository.DoktorRepository;
import com.hastane.repository.HastaRepository;
import com.hastane.repository.RandevuRepository;

@ExtendWith(MockitoExtension.class)
class MisafirRandevuServiceTests {

    @Mock private BransRepository bransRepository;
    @Mock private DoktorRepository doktorRepository;
    @Mock private HastaRepository hastaRepository;
    @Mock private RandevuRepository randevuRepository;

    private MisafirRandevuService service;
    private Doktor doktor;
    private int gelecekHaftaIci;

    @BeforeEach
    void setUp() {
        service = new MisafirRandevuService(
                bransRepository,
                doktorRepository,
                hastaRepository,
                randevuRepository);

        doktor = new Doktor();
        doktor.setOid(UUID.randomUUID());
        doktor.setHastaneOid(UUID.randomUUID());
        doktor.setAd("Ayse");
        doktor.setSoyad("Yilmaz");
        doktor.setUnvan("Dr.");
        doktor.setRandevuSuresiDk(30);

        LocalDate tarih = LocalDate.now(ZoneId.of("Europe/Istanbul")).plusDays(1);
        while (tarih.getDayOfWeek() == DayOfWeek.SATURDAY
                || tarih.getDayOfWeek() == DayOfWeek.SUNDAY) {
            tarih = tarih.plusDays(1);
        }
        gelecekHaftaIci = tarih.getYear() * 10000
                + tarih.getMonthValue() * 100
                + tarih.getDayOfMonth();
    }

    @Test
    void doluSaatDisabledBosSaatEnabledDoner() {
        when(doktorRepository.findByOidAndAktif(doktor.getOid(), (short) 1))
                .thenReturn(Optional.of(doktor));
        Randevu doluRandevu = new Randevu();
        doluRandevu.setRandevuSaati(90000);
        when(randevuRepository
                .findByDoktorOidAndRandevuTarihiAndDurumOrderByRandevuSaatiAsc(
                        doktor.getOid(), gelecekHaftaIci, "AKTIF"))
                .thenReturn(List.of(doluRandevu));

        List<MusaitSaatResponse> sonuc = service.doktorunSaatleriniGetir(
                doktor.getOid(),
                gelecekHaftaIci);

        assertFalse(sonuc.stream().filter(saat -> saat.saat() == 90000)
                .findFirst().orElseThrow().musait());
        assertTrue(sonuc.stream().filter(saat -> saat.saat() == 93000)
                .findFirst().orElseThrow().musait());
    }

    @Test
    void yeniHastaVeRandevuTekServisIslemindeOlusturulur() {
        when(doktorRepository.kilitleyerekBul(doktor.getOid(), (short) 1))
                .thenReturn(Optional.of(doktor));
        when(randevuRepository
                .findByDoktorOidAndRandevuTarihiAndDurumOrderByRandevuSaatiAsc(
                        doktor.getOid(), gelecekHaftaIci, "AKTIF"))
                .thenReturn(List.of());
        when(hastaRepository.findByTckn("12345678901")).thenReturn(Optional.empty());
        when(hastaRepository.save(any(Hasta.class))).thenAnswer(invocation -> {
            Hasta hasta = invocation.getArgument(0);
            hasta.setOid(UUID.randomUUID());
            return hasta;
        });
        when(randevuRepository.save(any(Randevu.class))).thenAnswer(invocation -> {
            Randevu randevu = invocation.getArgument(0);
            randevu.setOid(UUID.randomUUID());
            return randevu;
        });

        MisafirRandevuResponse sonuc = service.randevuOlustur(new MisafirRandevuRequest(
                "Ali", "Kaya", "12345678901", "05551234567",
                doktor.getOid(), gelecekHaftaIci, 90000));

        assertEquals(gelecekHaftaIci, sonuc.randevuTarihi());
        assertEquals(90000, sonuc.randevuSaati());
        verify(hastaRepository).save(any(Hasta.class));
        verify(randevuRepository).save(any(Randevu.class));
    }

    @Test
    void doluSaatSecilirseHastaKaydiOlusturulmaz() {
        when(doktorRepository.kilitleyerekBul(doktor.getOid(), (short) 1))
                .thenReturn(Optional.of(doktor));
        Randevu doluRandevu = new Randevu();
        doluRandevu.setRandevuSaati(90000);
        when(randevuRepository
                .findByDoktorOidAndRandevuTarihiAndDurumOrderByRandevuSaatiAsc(
                        doktor.getOid(), gelecekHaftaIci, "AKTIF"))
                .thenReturn(List.of(doluRandevu));

        assertThrows(RandevuCakismaException.class, () -> service.randevuOlustur(
                new MisafirRandevuRequest(
                        "Ali", "Kaya", "12345678901", "05551234567",
                        doktor.getOid(), gelecekHaftaIci, 90000)));

        verify(hastaRepository, never()).save(any(Hasta.class));
        verify(randevuRepository, never()).save(any(Randevu.class));
    }

    @Test
    void dogrulananHastaninGelecekAktifRandevulariniGetirir() {
        Hasta hasta = hastaOlustur();
        Randevu randevu = new Randevu();
        randevu.setOid(UUID.randomUUID());
        randevu.setHastaOid(hasta.getOid());
        randevu.setDoktorOid(doktor.getOid());
        randevu.setRandevuTarihi(gelecekHaftaIci);
        randevu.setRandevuSaati(90000);
        randevu.setDurum("AKTIF");
        when(hastaRepository.findByTckn("12345678901")).thenReturn(Optional.of(hasta));
        when(randevuRepository
                .findByHastaOidAndDurumOrderByRandevuTarihiAscRandevuSaatiAsc(
                        hasta.getOid(), "AKTIF"))
                .thenReturn(List.of(randevu));
        when(doktorRepository.findAllById(List.of(doktor.getOid())))
                .thenReturn(List.of(doktor));

        List<MevcutRandevuResponse> sonuc = service.hastaninAktifRandevulariniGetir(
                hastaBilgileri());

        assertEquals(1, sonuc.size());
        assertEquals(randevu.getOid(), sonuc.getFirst().randevuOid());
        assertEquals("Dr. Ayse Yilmaz", sonuc.getFirst().doktor());
    }

    @Test
    void hastaKendiAktifRandevusunuIptalEdebilir() {
        Hasta hasta = hastaOlustur();
        Randevu randevu = new Randevu();
        randevu.setOid(UUID.randomUUID());
        randevu.setHastaOid(hasta.getOid());
        randevu.setDurum("AKTIF");
        randevu.setStatus((short) 1);
        when(hastaRepository.findByTckn("12345678901")).thenReturn(Optional.of(hasta));
        when(randevuRepository.findById(randevu.getOid())).thenReturn(Optional.of(randevu));

        service.randevuIptalEt(randevu.getOid(), hastaBilgileri());

        assertEquals("IPTAL", randevu.getDurum());
        assertEquals((short) 0, randevu.getStatus());
        verify(randevuRepository).save(randevu);
    }

    @Test
    void hastaBilgileriEslesmezseRandevulariGostermez() {
        Hasta hasta = hastaOlustur();
        when(hastaRepository.findByTckn("12345678901")).thenReturn(Optional.of(hasta));

        assertThrows(GecersizRandevuBilgisiException.class,
                () -> service.hastaninAktifRandevulariniGetir(
                        new HastaBilgileriRequest(
                                "Baska", "Kisi", "12345678901", "05551234567")));

        verify(randevuRepository, never())
                .findByHastaOidAndDurumOrderByRandevuTarihiAscRandevuSaatiAsc(
                        any(), any());
    }

    private Hasta hastaOlustur() {
        Hasta hasta = new Hasta();
        hasta.setOid(UUID.randomUUID());
        hasta.setAd("Ali");
        hasta.setSoyad("Kaya");
        hasta.setTckn("12345678901");
        hasta.setTelefon("05551234567");
        return hasta;
    }

    private HastaBilgileriRequest hastaBilgileri() {
        return new HastaBilgileriRequest(
                "Ali", "Kaya", "12345678901", "05551234567");
    }
}
