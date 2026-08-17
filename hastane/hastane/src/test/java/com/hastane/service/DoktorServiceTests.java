package com.hastane.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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

import com.hastane.dto.DoktorGuncelleRequest;
import com.hastane.dto.DoktorRequest;
import com.hastane.entity.Doktor;
import com.hastane.exception.DoktorBulunamadiException;
import com.hastane.exception.GecersizDoktorBilgisiException;
import com.hastane.repository.DoktorRepository;

@ExtendWith(MockitoExtension.class)
class DoktorServiceTests {

    @Mock
    private DoktorRepository doktorRepository;

    private DoktorService doktorService;
    private DoktorRequest request;

    @BeforeEach
    void setUp() {
        doktorService = new DoktorService(doktorRepository);

        request = new DoktorRequest();
        request.setHastaneOid(UUID.randomUUID());
        request.setBransOid(UUID.randomUUID());
        request.setKullaniciOid(UUID.randomUUID());
        request.setAd(" Ayşe ");
        request.setSoyad(" Yılmaz ");
        request.setUnvan(" Uzman Doktor ");
    }

    @Test
    void doktorEkleVarsayilanDegerlerleKaydeder() {
        when(doktorRepository.save(any(Doktor.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Doktor sonuc = doktorService.doktorEkle(request);

        assertEquals("Ayşe", sonuc.getAd());
        assertEquals("Yılmaz", sonuc.getSoyad());
        assertEquals("Uzman Doktor", sonuc.getUnvan());
        assertEquals(30, sonuc.getRandevuSuresiDk());
        assertEquals((short) 1, sonuc.getStatus());
        assertEquals((short) 1, sonuc.getAktif());
        assertNotNull(sonuc.getCreatedDate());
        assertNotNull(sonuc.getCreatedTime());
        verify(doktorRepository).save(sonuc);
    }

    @Test
    void hastanedekiYalnizcaAktifDoktorlariSorgular() {
        UUID hastaneOid = request.getHastaneOid();
        Doktor doktor = new Doktor();
        when(doktorRepository.findByHastaneOidAndAktif(hastaneOid, (short) 1))
                .thenReturn(List.of(doktor));

        List<Doktor> sonuc = doktorService.hastanedekiAktifDoktorlariGetir(hastaneOid);

        assertEquals(List.of(doktor), sonuc);
        verify(doktorRepository).findByHastaneOidAndAktif(hastaneOid, (short) 1);
    }

    @Test
    void hastaneVeBranstakiYalnizcaAktifDoktorlariSorgular() {
        UUID hastaneOid = request.getHastaneOid();
        UUID bransOid = request.getBransOid();
        Doktor doktor = new Doktor();
        when(doktorRepository.findByHastaneOidAndBransOidAndAktif(
                hastaneOid,
                bransOid,
                (short) 1))
                .thenReturn(List.of(doktor));

        List<Doktor> sonuc = doktorService.hastaneVeBranstakiAktifDoktorlariGetir(
                hastaneOid,
                bransOid);

        assertEquals(List.of(doktor), sonuc);
        verify(doktorRepository).findByHastaneOidAndBransOidAndAktif(
                hastaneOid,
                bransOid,
                (short) 1);
    }

    @Test
    void doktorunYalnizcaGonderilenBilgileriniGunceller() {
        UUID doktorOid = UUID.randomUUID();
        Doktor doktor = new Doktor();
        doktor.setAd("Ayşe");
        doktor.setSoyad("Yılmaz");
        doktor.setUnvan("Uzman Doktor");
        doktor.setHastaneOid(request.getHastaneOid());

        DoktorGuncelleRequest guncelleme = new DoktorGuncelleRequest();
        guncelleme.setSoyad(" Demir ");
        guncelleme.setUnvan(" Doçent Doktor ");

        when(doktorRepository.findById(doktorOid)).thenReturn(Optional.of(doktor));
        when(doktorRepository.save(doktor)).thenReturn(doktor);

        Doktor sonuc = doktorService.doktorGuncelle(doktorOid, guncelleme);

        assertEquals("Ayşe", sonuc.getAd());
        assertEquals("Demir", sonuc.getSoyad());
        assertEquals("Doçent Doktor", sonuc.getUnvan());
        assertEquals(request.getHastaneOid(), sonuc.getHastaneOid());
        verify(doktorRepository).save(doktor);
    }

    @Test
    void bosGuncellemeIsteginiReddeder() {
        assertThrows(
                GecersizDoktorBilgisiException.class,
                () -> doktorService.doktorGuncelle(
                        UUID.randomUUID(),
                        new DoktorGuncelleRequest()));
    }

    @Test
    void aktifAlaniGuncellenerekDoktoruPasifeCeker() {
        UUID doktorOid = UUID.randomUUID();
        Doktor doktor = new Doktor();
        doktor.setAktif((short) 1);

        DoktorGuncelleRequest guncelleme = new DoktorGuncelleRequest();
        guncelleme.setAktif((short) 0);

        when(doktorRepository.findById(doktorOid)).thenReturn(Optional.of(doktor));
        when(doktorRepository.save(doktor)).thenReturn(doktor);

        Doktor sonuc = doktorService.doktorGuncelle(doktorOid, guncelleme);

        assertEquals((short) 0, sonuc.getAktif());
        verify(doktorRepository).save(doktor);
    }

    @Test
    void gecersizAktifDegeriniReddeder() {
        DoktorGuncelleRequest guncelleme = new DoktorGuncelleRequest();
        guncelleme.setAktif((short) 2);
        assertThrows(
                GecersizDoktorBilgisiException.class,
                () -> doktorService.doktorGuncelle(UUID.randomUUID(), guncelleme));
    }

    @Test
    void bulunamayanDoktorGuncellenemez() {
        UUID doktorOid = UUID.randomUUID();
        DoktorGuncelleRequest guncelleme = new DoktorGuncelleRequest();
        guncelleme.setSoyad("Demir");
        when(doktorRepository.findById(doktorOid)).thenReturn(Optional.empty());

        assertThrows(
                DoktorBulunamadiException.class,
                () -> doktorService.doktorGuncelle(doktorOid, guncelleme));
    }
}
