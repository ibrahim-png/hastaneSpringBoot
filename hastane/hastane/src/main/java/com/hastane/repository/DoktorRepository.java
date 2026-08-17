package com.hastane.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hastane.entity.Doktor;

public interface DoktorRepository extends JpaRepository<Doktor, UUID> {

    List<Doktor> findByHastaneOidAndAktif(UUID hastaneOid, Short aktif);

    List<Doktor> findByHastaneOidAndBransOidAndAktif(
            UUID hastaneOid,
            UUID bransOid,
            Short aktif);

    Optional<Doktor> findByKullaniciOidAndAktif(UUID kullaniciOid, Short aktif);
}
