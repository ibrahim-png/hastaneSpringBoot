package com.hastane.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.hastane.entity.Randevu;

public interface RandevuRepository extends JpaRepository<Randevu, Long> {

}