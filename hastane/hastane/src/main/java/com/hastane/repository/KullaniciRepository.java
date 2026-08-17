package com.hastane.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hastane.entity.Kullanici;

public interface KullaniciRepository extends JpaRepository<Kullanici, UUID> {

    Optional<Kullanici> findByEmailIgnoreCase(String email);
}
