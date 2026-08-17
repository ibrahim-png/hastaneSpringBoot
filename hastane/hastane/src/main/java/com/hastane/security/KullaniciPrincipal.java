package com.hastane.security;

import java.io.Serial;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.hastane.entity.Kullanici;

public class KullaniciPrincipal implements UserDetails {

    @Serial
    private static final long serialVersionUID = 1L;

    private final UUID oid;
    private final String email;
    private final String passwordHash;
    private final String rol;
    private final boolean aktif;

    public KullaniciPrincipal(Kullanici kullanici) {
        this.oid = kullanici.getOid();
        this.email = kullanici.getEmail();
        this.passwordHash = kullanici.getPasswordHash();
        this.rol = kullanici.getRol().strip().toUpperCase(Locale.ROOT);
        this.aktif = Short.valueOf((short) 1).equals(kullanici.getAktif());
    }

    public UUID getOid() {
        return oid;
    }

    public String getRol() {
        return rol;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + rol));
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isEnabled() {
        return aktif;
    }
}
