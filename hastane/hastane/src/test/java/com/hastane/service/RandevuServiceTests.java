package com.hastane.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hastane.dto.RandevuRequest;
import com.hastane.dto.DoluSaatResponse;
import com.hastane.dto.PersonelRandevuResponse;
import com.hastane.entity.Doktor;
import com.hastane.entity.Hasta;
import com.hastane.entity.Randevu;
import com.hastane.exception.GecersizRandevuBilgisiException;
import com.hastane.exception.RandevuCakismaException;
import com.hastane.repository.RandevuRepository;
import com.hastane.repository.DoktorRepository;
import com.hastane.repository.HastaRepository;

@ExtendWith(MockitoExtension.class)
class RandevuServiceTests {

    @Mock
    private RandevuRepository randevuRepository;

    @Mock
    private DoktorRepository doktorRepository;

    @Mock
    private HastaRepository hastaRepository;

    private RandevuService randevuService;
    private RandevuRequest request;

    @BeforeEach
    void setUp() {
        randevuService = new RandevuService(
                randevuRepository,
                doktorRepository,
                hastaRepository);

        request = new RandevuRequest();
        request.setDoktorOid(UUID.randomUUID());
        request.setHastaOid(UUID.randomUUID());
        request.setHastaneOid(UUID.randomUUID());
        request.setRandevuTarihi(20260820);
        request.setRandevuSaati(143000);
    }

    @Test
    void ayniTarihVeSaatteAktifRandevuVarsaYeniRandevuOlusturmaz() {
        when(randevuRepository.existsByDoktorOidAndRandevuTarihiAndRandevuSaatiAndDurum(
                request.getDoktorOid(),
                request.getRandevuTarihi(),
                request.getRandevuSaati(),
                "AKTIF"))
                .thenReturn(true);

        assertThrows(
                RandevuCakismaException.class,
                () -> randevuService.randevuOlustur(request));

        verify(randevuRepository, never()).save(any(Randevu.class));
    }

    @Test
    void doktorMusaitseRandevuyuOlusturur() {
        when(randevuRepository.existsByDoktorOidAndRandevuTarihiAndRandevuSaatiAndDurum(
                request.getDoktorOid(),
                request.getRandevuTarihi(),
                request.getRandevuSaati(),
                "AKTIF"))
                .thenReturn(false);
        when(randevuRepository.save(any(Randevu.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Randevu sonuc = randevuService.randevuOlustur(request);

        assertEquals(request.getDoktorOid(), sonuc.getDoktorOid());
        assertEquals(request.getRandevuTarihi(), sonuc.getRandevuTarihi());
        assertEquals(request.getRandevuSaati(), sonuc.getRandevuSaati());
        assertEquals("AKTIF", sonuc.getDurum());
        verify(randevuRepository).save(any(Randevu.class));
    }

    @Test
    void doktorunGunlukRandevulariniSaatSirasiylaSorgular() {
        Randevu ilkRandevu = new Randevu();
        ilkRandevu.setRandevuSaati(90000);
        Randevu ikinciRandevu = new Randevu();
        ikinciRandevu.setRandevuSaati(103000);
        when(randevuRepository.findByDoktorOidAndRandevuTarihiOrderByRandevuSaatiAsc(
                request.getDoktorOid(),
                request.getRandevuTarihi()))
                .thenReturn(List.of(ilkRandevu, ikinciRandevu));

        List<Randevu> sonuc = randevuService.doktorunGunlukRandevulariniGetir(
                request.getDoktorOid(),
                request.getRandevuTarihi());

        assertEquals(List.of(ilkRandevu, ikinciRandevu), sonuc);
        verify(randevuRepository).findByDoktorOidAndRandevuTarihiOrderByRandevuSaatiAsc(
                request.getDoktorOid(),
                request.getRandevuTarihi());
    }

    @Test
    void gecersizRandevuTarihiniReddeder() {
        assertThrows(
                GecersizRandevuBilgisiException.class,
                () -> randevuService.doktorunGunlukRandevulariniGetir(
                        request.getDoktorOid(),
                        20260230));
    }

    @Test
    void doktorSadeceKendiRandevulariniGorur() {
        UUID kullaniciOid = UUID.randomUUID();
        Doktor doktor = new Doktor();
        doktor.setOid(request.getDoktorOid());
        when(doktorRepository.findByKullaniciOidAndAktif(kullaniciOid, (short) 1))
                .thenReturn(Optional.of(doktor));

        Hasta hasta = hastaOlustur();
        Randevu randevu = randevuOlustur(hasta.getOid(), doktor.getOid());
        when(randevuRepository.findByDoktorOidAndRandevuTarihiOrderByRandevuSaatiAsc(
                request.getDoktorOid(),
                request.getRandevuTarihi()))
                .thenReturn(List.of(randevu));
        when(hastaRepository.findAllById(List.of(hasta.getOid()))).thenReturn(List.of(hasta));
        when(doktorRepository.findAllById(List.of(doktor.getOid()))).thenReturn(List.of(doktor));

        List<PersonelRandevuResponse> sonuc = randevuService
                .oturumdakiKullanicininGunlukRandevulariniGetir(
                        kullaniciOid,
                        "DOKTOR",
                        request.getRandevuTarihi());

        assertEquals("Ali Kaya", sonuc.getFirst().hastaAdSoyad());
        verify(doktorRepository).findByKullaniciOidAndAktif(kullaniciOid, (short) 1);
    }

    @Test
    void hastaSadeceKendiRandevulariniGorur() {
        UUID kullaniciOid = request.getHastaOid();
        Hasta hasta = hastaOlustur();
        Doktor doktor = doktorOlustur();
        Randevu randevu = randevuOlustur(hasta.getOid(), doktor.getOid());
        when(randevuRepository.findByHastaOidAndRandevuTarihiOrderByRandevuSaatiAsc(
                kullaniciOid,
                request.getRandevuTarihi()))
                .thenReturn(List.of(randevu));
        when(hastaRepository.findAllById(List.of(hasta.getOid()))).thenReturn(List.of(hasta));
        when(doktorRepository.findAllById(List.of(doktor.getOid()))).thenReturn(List.of(doktor));

        List<PersonelRandevuResponse> sonuc = randevuService
                .oturumdakiKullanicininGunlukRandevulariniGetir(
                        kullaniciOid,
                        "HASTA",
                        request.getRandevuTarihi());

        assertEquals("Ali Kaya", sonuc.getFirst().hastaAdSoyad());
        verify(randevuRepository).findByHastaOidAndRandevuTarihiOrderByRandevuSaatiAsc(
                kullaniciOid,
                request.getRandevuTarihi());
    }

    @Test
    void mudurSecilenGundekiTumRandevulariGorur() {
        UUID kullaniciOid = UUID.randomUUID();
        Hasta hasta = hastaOlustur();
        Doktor doktor = doktorOlustur();
        Randevu randevu = randevuOlustur(hasta.getOid(), doktor.getOid());
        when(randevuRepository.findByRandevuTarihiOrderByRandevuSaatiAsc(
                request.getRandevuTarihi()))
                .thenReturn(List.of(randevu));
        when(hastaRepository.findAllById(List.of(hasta.getOid()))).thenReturn(List.of(hasta));
        when(doktorRepository.findAllById(List.of(doktor.getOid()))).thenReturn(List.of(doktor));

        List<PersonelRandevuResponse> sonuc = randevuService
                .oturumdakiKullanicininGunlukRandevulariniGetir(
                        kullaniciOid,
                        "MUDUR",
                        request.getRandevuTarihi());

        assertEquals("Ali Kaya", sonuc.getFirst().hastaAdSoyad());
        assertEquals("Ayse Yilmaz", sonuc.getFirst().doktorAdSoyad());
        verify(randevuRepository).findByRandevuTarihiOrderByRandevuSaatiAsc(
                request.getRandevuTarihi());
    }

    @Test
    void desteklenmeyenRolReddedilir() {
        assertThrows(
                GecersizRandevuBilgisiException.class,
                () -> randevuService.oturumdakiKullanicininGunlukRandevulariniGetir(
                        UUID.randomUUID(),
                        "BILINMEYEN",
                        request.getRandevuTarihi()));
    }

    @Test
    void hastaIcinYalnizcaDoluTarihVeSaatleriDonusturur() {
        Randevu randevu = new Randevu();
        randevu.setRandevuTarihi(request.getRandevuTarihi());
        randevu.setRandevuSaati(request.getRandevuSaati());
        randevu.setHastaOid(UUID.randomUUID());
        when(randevuRepository
                .findByDoktorOidAndRandevuTarihiAndDurumOrderByRandevuSaatiAsc(
                        request.getDoktorOid(),
                        request.getRandevuTarihi(),
                        "AKTIF"))
                .thenReturn(List.of(randevu));

        List<DoluSaatResponse> sonuc = randevuService.doktorunDoluSaatleriniGetir(
                request.getDoktorOid(),
                request.getRandevuTarihi());

        assertEquals(1, sonuc.size());
        assertEquals(request.getRandevuTarihi(), sonuc.get(0).randevuTarihi());
        assertEquals(request.getRandevuSaati(), sonuc.get(0).randevuSaati());
    }

    private Hasta hastaOlustur() {
        Hasta hasta = new Hasta();
        hasta.setOid(request.getHastaOid());
        hasta.setAd("Ali");
        hasta.setSoyad("Kaya");
        return hasta;
    }

    private Doktor doktorOlustur() {
        Doktor doktor = new Doktor();
        doktor.setOid(request.getDoktorOid());
        doktor.setAd("Ayse");
        doktor.setSoyad("Yilmaz");
        return doktor;
    }

    private Randevu randevuOlustur(UUID hastaOid, UUID doktorOid) {
        Randevu randevu = new Randevu();
        randevu.setOid(UUID.randomUUID());
        randevu.setHastaOid(hastaOid);
        randevu.setDoktorOid(doktorOid);
        randevu.setRandevuTarihi(request.getRandevuTarihi());
        randevu.setRandevuSaati(request.getRandevuSaati());
        randevu.setDurum("AKTIF");
        return randevu;
    }
}
