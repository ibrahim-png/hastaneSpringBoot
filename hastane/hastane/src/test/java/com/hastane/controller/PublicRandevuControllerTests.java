package com.hastane.controller;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import com.hastane.dto.HastaBilgileriRequest;
import com.hastane.exception.GecersizRandevuBilgisiException;
import com.hastane.security.TurnstileService;
import com.hastane.service.MisafirRandevuService;

class PublicRandevuControllerTests {

    @Test
    void turnstileReddederseHastaBilgileriSorgulanmaz() {
        MisafirRandevuService misafirRandevuService = mock(MisafirRandevuService.class);
        TurnstileService turnstileService = mock(TurnstileService.class);
        when(turnstileService.dogrula("gecersiz-token", "patient_lookup"))
                .thenReturn(false);
        PublicRandevuController controller = new PublicRandevuController(
                misafirRandevuService,
                turnstileService);
        HastaBilgileriRequest request = new HastaBilgileriRequest(
                "Ada",
                "Hasta",
                "12345678901",
                "05551234567",
                "gecersiz-token");

        assertThrows(
                GecersizRandevuBilgisiException.class,
                () -> controller.hastaninAktifRandevulariniGetir(request));

        verifyNoInteractions(misafirRandevuService);
    }
}
