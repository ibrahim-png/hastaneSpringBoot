package com.hastane.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hastane.dto.BransResponse;
import com.hastane.dto.DoktorSecimResponse;
import com.hastane.dto.HastaBilgileriRequest;
import com.hastane.dto.MevcutRandevuResponse;
import com.hastane.dto.MisafirRandevuRequest;
import com.hastane.dto.MisafirRandevuResponse;
import com.hastane.dto.MusaitSaatResponse;
import com.hastane.exception.GecersizRandevuBilgisiException;
import com.hastane.security.TurnstileService;
import com.hastane.service.MisafirRandevuService;

@RestController
@RequestMapping("/api/public")
public class PublicRandevuController {

    private final MisafirRandevuService misafirRandevuService;
    private final TurnstileService turnstileService;

    public PublicRandevuController(
            MisafirRandevuService misafirRandevuService,
            TurnstileService turnstileService) {
        this.misafirRandevuService = misafirRandevuService;
        this.turnstileService = turnstileService;
    }

    @GetMapping("/branslar")
    public List<BransResponse> aktifBranslariGetir() {
        return misafirRandevuService.aktifBranslariGetir();
    }

    @GetMapping("/doktorlar")
    public List<DoktorSecimResponse> branstakiAktifDoktorlariGetir(
            @RequestParam UUID bransOid) {
        return misafirRandevuService.branstakiAktifDoktorlariGetir(bransOid);
    }

    @GetMapping("/doktorlar/{doktorOid}/saatler")
    public List<MusaitSaatResponse> doktorunSaatleriniGetir(
            @PathVariable UUID doktorOid,
            @RequestParam Integer randevuTarihi) {
        return misafirRandevuService.doktorunSaatleriniGetir(
                doktorOid,
                randevuTarihi);
    }

    @PostMapping("/randevular")
    public ResponseEntity<MisafirRandevuResponse> randevuOlustur(
            @RequestBody MisafirRandevuRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(misafirRandevuService.randevuOlustur(request));
    }

    @PostMapping("/randevular/sorgula")
    public List<MevcutRandevuResponse> hastaninAktifRandevulariniGetir(
            @RequestBody HastaBilgileriRequest request) {
        if (request == null
                || !turnstileService.dogrula(request.turnstileToken(), "patient_lookup")) {
            throw new GecersizRandevuBilgisiException(
                    "Insan dogrulamasi basarisiz. Lutfen tekrar deneyin.");
        }
        return misafirRandevuService.hastaninAktifRandevulariniGetir(request);
    }

    @PatchMapping("/randevular/{randevuOid}/iptal")
    public ResponseEntity<Void> randevuIptalEt(
            @PathVariable UUID randevuOid,
            @RequestBody HastaBilgileriRequest request) {
        misafirRandevuService.randevuIptalEt(randevuOid, request);
        return ResponseEntity.noContent().build();
    }
}
