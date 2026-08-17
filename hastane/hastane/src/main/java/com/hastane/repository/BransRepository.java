package com.hastane.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hastane.entity.Brans;

public interface BransRepository extends JpaRepository<Brans, UUID> {
    List<Brans> findByStatusOrderByAdAsc(Short status);
}
