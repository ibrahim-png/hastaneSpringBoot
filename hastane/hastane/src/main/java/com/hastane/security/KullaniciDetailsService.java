package com.hastane.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.hastane.repository.KullaniciRepository;

@Service
public class KullaniciDetailsService implements UserDetailsService {

    private final KullaniciRepository kullaniciRepository;

    public KullaniciDetailsService(KullaniciRepository kullaniciRepository) {
        this.kullaniciRepository = kullaniciRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return kullaniciRepository.findByEmailIgnoreCase(email.strip())
                .map(KullaniciPrincipal::new)
                .orElseThrow(() -> new UsernameNotFoundException("E-posta veya sifre hatali."));
    }
}
