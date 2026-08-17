package com.hastane.dto;

import java.util.UUID;

public record KullaniciResponse(UUID kullaniciOid, String email, String rol) {
}
