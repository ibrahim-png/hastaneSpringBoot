package com.hastane.repository;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import com.hastane.entity.Randevu;

public interface RandevuRepository extends JpaRepository<Randevu, UUID> {

    boolean existsByDoktorOidAndRandevuTarihiAndRandevuSaatiAndDurum(
            UUID doktorOid,
            Integer randevuTarihi,
            Integer randevuSaati,
            String durum);

    List<Randevu> findByDoktorOidAndRandevuTarihiOrderByRandevuSaatiAsc(
            UUID doktorOid,
            Integer randevuTarihi);

    List<Randevu> findByDoktorOidAndRandevuTarihiAndDurumOrderByRandevuSaatiAsc(
            UUID doktorOid,
            Integer randevuTarihi,
            String durum);
}
