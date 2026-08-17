package com.hastane.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hastane.entity.Doktor;

import jakarta.persistence.LockModeType;

public interface DoktorRepository extends JpaRepository<Doktor, UUID> {

    List<Doktor> findByHastaneOidAndAktif(UUID hastaneOid, Short aktif);

    List<Doktor> findByHastaneOidAndBransOidAndAktif(
            UUID hastaneOid,
            UUID bransOid,
            Short aktif);

    Optional<Doktor> findByKullaniciOidAndAktif(UUID kullaniciOid, Short aktif);

    Optional<Doktor> findByOidAndAktif(UUID oid, Short aktif);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select d from Doktor d where d.oid = :oid and d.aktif = :aktif")
    Optional<Doktor> kilitleyerekBul(
            @Param("oid") UUID oid,
            @Param("aktif") Short aktif);

    List<Doktor> findByBransOidAndAktifOrderByAdAscSoyadAsc(UUID bransOid, Short aktif);
}
