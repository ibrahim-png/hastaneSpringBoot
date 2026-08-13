package com.hastane.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hastane.dto.RandevuRequest;
import com.hastane.entity.Randevu;
import com.hastane.service.RandevuService;

@RestController
@RequestMapping("/api/randevular")
public class RandevuController {

    private final RandevuService randevuService;

    public RandevuController(RandevuService randevuService) {
        this.randevuService = randevuService;
    }

    @PostMapping
    public ResponseEntity<Randevu> randevuOlustur(
            @RequestBody RandevuRequest request) {

        Randevu randevu = randevuService.randevuOlustur(
                request.getDoktorId(),
                request.getHastaId(),
                request.getHastaneId(),
                request.getRandevuTarihi(),
                request.getRandevuSaati()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(randevu);
    }
}