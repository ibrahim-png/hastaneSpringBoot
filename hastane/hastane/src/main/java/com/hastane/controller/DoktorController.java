package com.hastane.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hastane.dto.DoktorGuncelleRequest;
import com.hastane.dto.DoktorRequest;
import com.hastane.entity.Doktor;
import com.hastane.service.DoktorService;

@RestController
@RequestMapping("/api/doktorlar")
public class DoktorController {

    private final DoktorService doktorService;

    public DoktorController(DoktorService doktorService) {
        this.doktorService = doktorService;
    }

    @PostMapping
    @PreAuthorize("hasRole('MUDUR')")
    public ResponseEntity<Doktor> doktorEkle(@RequestBody DoktorRequest request) {
        Doktor doktor = doktorService.doktorEkle(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(doktor);
    }

    @GetMapping(params = {"hastaneOid", "!bransOid"})
    public ResponseEntity<List<Doktor>> hastanedekiAktifDoktorlariGetir(
            @RequestParam UUID hastaneOid) {
        List<Doktor> doktorlar = doktorService.hastanedekiAktifDoktorlariGetir(hastaneOid);

        return ResponseEntity.ok(doktorlar);
    }

    @GetMapping(params = {"hastaneOid", "bransOid"})
    public ResponseEntity<List<Doktor>> hastaneVeBranstakiAktifDoktorlariGetir(
            @RequestParam UUID hastaneOid,
            @RequestParam UUID bransOid) {
        List<Doktor> doktorlar = doktorService.hastaneVeBranstakiAktifDoktorlariGetir(
                hastaneOid,
                bransOid);

        return ResponseEntity.ok(doktorlar);
    }

    @PatchMapping("/{doktorOid}")
    @PreAuthorize("hasRole('MUDUR')")
    public ResponseEntity<Doktor> doktorGuncelle(
            @PathVariable UUID doktorOid,
            @RequestBody DoktorGuncelleRequest request) {
        Doktor doktor = doktorService.doktorGuncelle(doktorOid, request);

        return ResponseEntity.ok(doktor);
    }
}
