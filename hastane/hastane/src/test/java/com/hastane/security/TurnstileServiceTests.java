package com.hastane.security;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

import com.hastane.config.TurnstileProperties;

import tools.jackson.databind.ObjectMapper;

class TurnstileServiceTests {

    @Test
    void kapaliykenHariciDogrulamaYapmadanIzinVerir() throws Exception {
        TurnstileProperties properties = properties(false);
        HttpClient httpClient = mock(HttpClient.class);
        TurnstileService service = new TurnstileService(
                properties,
                new ObjectMapper(),
                httpClient);

        assertTrue(service.dogrula(null));
        verify(httpClient, never()).send(any(), any());
    }

    @Test
    void eksikTokenReddedilir() throws Exception {
        HttpClient httpClient = mock(HttpClient.class);
        TurnstileService service = new TurnstileService(
                properties(true),
                new ObjectMapper(),
                httpClient);

        assertFalse(service.dogrula("  "));
        verify(httpClient, never()).send(any(), any());
    }

    @Test
    void dogruHostnameVeActionKabulEdilir() throws Exception {
        HttpClient httpClient = mock(HttpClient.class);
        HttpResponse<String> response = response(
                200,
                """
                {"success":true,"hostname":"hastanespringboot.onrender.com","action":"staff_login","error-codes":[]}
                """);
        when(httpClient.send(
                any(HttpRequest.class),
                ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
                .thenReturn(response);
        TurnstileService service = new TurnstileService(
                properties(true),
                new ObjectMapper(),
                httpClient);

        assertTrue(service.dogrula("gecerli-token"));
    }

    @Test
    void beklenmeyenHostnameReddedilir() throws Exception {
        HttpClient httpClient = mock(HttpClient.class);
        HttpResponse<String> response = response(
                200,
                """
                {"success":true,"hostname":"saldirgan.example","action":"staff_login","error-codes":[]}
                """);
        when(httpClient.send(
                any(HttpRequest.class),
                ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
                .thenReturn(response);
        TurnstileService service = new TurnstileService(
                properties(true),
                new ObjectMapper(),
                httpClient);

        assertFalse(service.dogrula("gecerli-token"));
    }

    @Test
    void cloudflareReddiReddedilir() throws Exception {
        HttpClient httpClient = mock(HttpClient.class);
        HttpResponse<String> response = response(
                200,
                """
                {"success":false,"hostname":null,"action":null,"error-codes":["invalid-input-response"]}
                """);
        when(httpClient.send(
                any(HttpRequest.class),
                ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
                .thenReturn(response);
        TurnstileService service = new TurnstileService(
                properties(true),
                new ObjectMapper(),
                httpClient);

        assertFalse(service.dogrula("gecersiz-token"));
    }

    private static TurnstileProperties properties(boolean enabled) {
        TurnstileProperties properties = new TurnstileProperties();
        properties.setEnabled(enabled);
        properties.setSecretKey("test-secret");
        properties.setExpectedHostname("hastanespringboot.onrender.com");
        properties.setExpectedAction("staff_login");
        return properties;
    }

    @SuppressWarnings("unchecked")
    private static HttpResponse<String> response(int status, String body) {
        HttpResponse<String> response = mock(HttpResponse.class);
        when(response.statusCode()).thenReturn(status);
        when(response.body()).thenReturn(body);
        return response;
    }
}
