package com.hastane.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hastane.entity.Hasta;

public interface HastaRepository extends JpaRepository<Hasta, UUID> {
    Optional<Hasta> findByTckn(String tckn);
}
