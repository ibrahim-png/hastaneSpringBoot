package com.hastane.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hastane.dto.RandevuRequest;
import com.hastane.dto.DoluSaatResponse;
import com.hastane.entity.Randevu;
import com.hastane.service.RandevuService;

@RestController
@RequestMapping("/api/randevular")//https://hastanespringboot.onrender.com/api/randevular
public class RandevuController {

    private final RandevuService randevuService;

    public RandevuController(RandevuService randevuService) {
        this.randevuService = randevuService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('MUDUR', 'HASTA')")
    public ResponseEntity<Randevu> randevuOlustur(
            @RequestBody RandevuRequest request,
            @AuthenticationPrincipal Jwt jwt) {

        if ("HASTA".equals(jwt.getClaimAsString("rol"))) {
            request.setHastaOid(UUID.fromString(jwt.getSubject()));
        }

        Randevu randevu = randevuService.randevuOlustur(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(randevu);
    }

    @GetMapping(params = {"doktorOid", "randevuTarihi"})
    @PreAuthorize("hasRole('MUDUR')")
    public ResponseEntity<List<Randevu>> doktorunGunlukRandevulariniGetir(
            @RequestParam UUID doktorOid,
            @RequestParam Integer randevuTarihi) {
        List<Randevu> randevular = randevuService.doktorunGunlukRandevulariniGetir(
                doktorOid,
                randevuTarihi);

        return ResponseEntity.ok(randevular);
    }

    @GetMapping(value = "/doktor", params = "randevuTarihi")
    @PreAuthorize("hasRole('DOKTOR')")
    public ResponseEntity<List<Randevu>> oturumdakiDoktorunGunlukRandevulariniGetir(
            @RequestParam Integer randevuTarihi,
            @AuthenticationPrincipal Jwt jwt) {

        List<Randevu> randevular = randevuService
                .oturumdakiDoktorunGunlukRandevulariniGetir(
                        UUID.fromString(jwt.getSubject()),
                        randevuTarihi);

        return ResponseEntity.ok(randevular);
    }

    @GetMapping(value = "/dolu-saatler", params = {"doktorOid", "randevuTarihi"})
    @PreAuthorize("hasAnyRole('MUDUR', 'DOKTOR', 'HASTA')")
    public ResponseEntity<List<DoluSaatResponse>> doktorunDoluSaatleriniGetir(
            @RequestParam UUID doktorOid,
            @RequestParam Integer randevuTarihi) {
        List<DoluSaatResponse> doluSaatler = randevuService.doktorunDoluSaatleriniGetir(
                doktorOid,
                randevuTarihi);

        return ResponseEntity.ok(doluSaatler);
    }
}
