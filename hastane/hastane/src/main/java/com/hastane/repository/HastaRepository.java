package com.hastane.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hastane.entity.Hasta;

import jakarta.persistence.LockModeType;

public interface HastaRepository extends JpaRepository<Hasta, UUID> {
    Optional<Hasta> findByTckn(String tckn);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select h from Hasta h where h.tckn = :tckn")
    Optional<Hasta> kilitleyerekTcknIleBul(@Param("tckn") String tckn);
}
