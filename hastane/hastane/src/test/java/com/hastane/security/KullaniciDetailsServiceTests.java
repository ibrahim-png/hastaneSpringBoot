package com.hastane.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.hastane.entity.Kullanici;
import com.hastane.repository.KullaniciRepository;

@ExtendWith(MockitoExtension.class)
class KullaniciDetailsServiceTests {

    @Mock
    private KullaniciRepository kullaniciRepository;

    @Test
    void kullanicininRolunuSpringYetkisineDonusturur() {
        Kullanici kullanici = kullanici("doktor@hastane.com", "DOKTOR", (short) 1);
        when(kullaniciRepository.findByEmailIgnoreCase("doktor@hastane.com"))
                .thenReturn(Optional.of(kullanici));

        KullaniciDetailsService service = new KullaniciDetailsService(kullaniciRepository);
        KullaniciPrincipal sonuc = (KullaniciPrincipal) service
                .loadUserByUsername(" doktor@hastane.com ");

        assertEquals(kullanici.getOid(), sonuc.getOid());
        assertEquals("ROLE_DOKTOR", sonuc.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    void pasifKullaniciGirisIcinEtkinDegildir() {
        Kullanici kullanici = kullanici("hasta@hastane.com", "HASTA", (short) 0);
        when(kullaniciRepository.findByEmailIgnoreCase("hasta@hastane.com"))
                .thenReturn(Optional.of(kullanici));

        KullaniciDetailsService service = new KullaniciDetailsService(kullaniciRepository);
        KullaniciPrincipal sonuc = (KullaniciPrincipal) service
                .loadUserByUsername("hasta@hastane.com");

        assertFalse(sonuc.isEnabled());
    }

    @Test
    void bulunamayanEpostaReddedilir() {
        when(kullaniciRepository.findByEmailIgnoreCase("yok@hastane.com"))
                .thenReturn(Optional.empty());

        KullaniciDetailsService service = new KullaniciDetailsService(kullaniciRepository);

        assertThrows(
                UsernameNotFoundException.class,
                () -> service.loadUserByUsername("yok@hastane.com"));
    }

    private Kullanici kullanici(String email, String rol, short aktif) {
        Kullanici kullanici = new Kullanici();
        kullanici.setOid(UUID.randomUUID());
        kullanici.setEmail(email);
        kullanici.setPasswordHash("{bcrypt}ornek");
        kullanici.setRol(rol);
        kullanici.setAktif(aktif);
        return kullanici;
    }
}
