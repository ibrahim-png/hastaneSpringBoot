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
import com.hastane.entity.Doktor;
import com.hastane.entity.Randevu;
import com.hastane.exception.GecersizRandevuBilgisiException;
import com.hastane.exception.RandevuCakismaException;
import com.hastane.repository.RandevuRepository;
import com.hastane.repository.DoktorRepository;

@ExtendWith(MockitoExtension.class)
class RandevuServiceTests {

    @Mock
    private RandevuRepository randevuRepository;

    @Mock
    private DoktorRepository doktorRepository;

    private RandevuService randevuService;
    private RandevuRequest request;

    @BeforeEach
    void setUp() {
        randevuService = new RandevuService(randevuRepository, doktorRepository);

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

        Randevu randevu = new Randevu();
        when(randevuRepository.findByDoktorOidAndRandevuTarihiOrderByRandevuSaatiAsc(
                request.getDoktorOid(),
                request.getRandevuTarihi()))
                .thenReturn(List.of(randevu));

        List<Randevu> sonuc = randevuService.oturumdakiDoktorunGunlukRandevulariniGetir(
                kullaniciOid,
                request.getRandevuTarihi());

        assertEquals(List.of(randevu), sonuc);
        verify(doktorRepository).findByKullaniciOidAndAktif(kullaniciOid, (short) 1);
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
}
