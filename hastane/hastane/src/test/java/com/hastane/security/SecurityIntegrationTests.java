package com.hastane.security;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(properties =
        "security.jwt.secret-base64=MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=")
@AutoConfigureMockMvc
class SecurityIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void oturumsuzKullaniciKorunanEndpointiCagiramaz() throws Exception {
        mockMvc.perform(get("/api/doktorlar")
                        .param("hastaneOid", UUID.randomUUID().toString()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void hastaDoktorGuncelleyemez() throws Exception {
        mockMvc.perform(patch("/api/doktorlar/{doktorOid}", UUID.randomUUID())
                        .with(jwt()
                                .jwt(token -> token
                                        .subject(UUID.randomUUID().toString())
                                        .claim("rol", "HASTA"))
                                .authorities(new SimpleGrantedAuthority("ROLE_HASTA")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"aktif\":0}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void gecersizBearerTokenReddedilir() throws Exception {
        mockMvc.perform(get("/api/auth/ben")
                        .header("Authorization", "Bearer gecersiz-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void eksikGirisBilgileri401Doner() throws Exception {
        mockMvc.perform(post("/api/auth/giris")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }
}
