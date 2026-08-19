package com.hastane.security;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hastane.config.TurnstileProperties;

import tools.jackson.databind.ObjectMapper;

@Service
public class TurnstileService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TurnstileService.class);
    private static final int MAKSIMUM_TOKEN_UZUNLUGU = 2048;

    private final TurnstileProperties properties;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    @Autowired
    public TurnstileService(
            TurnstileProperties properties,
            ObjectMapper objectMapper) {
        this(
                properties,
                objectMapper,
                HttpClient.newBuilder()
                        .connectTimeout(Duration.ofSeconds(3))
                        .build());
    }

    TurnstileService(
            TurnstileProperties properties,
            ObjectMapper objectMapper,
            HttpClient httpClient) {
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.httpClient = httpClient;
    }

    public boolean dogrula(String token) {
        if (!properties.isEnabled()) {
            return true;
        }

        if (token == null || token.isBlank() || token.length() > MAKSIMUM_TOKEN_UZUNLUGU) {
            return false;
        }

        if (bos(properties.getSecretKey()) || bos(properties.getExpectedHostname())) {
            LOGGER.error("Turnstile uretim ayarlari eksik.");
            return false;
        }

        try {
            String requestBody = objectMapper.writeValueAsString(
                    new SiteverifyRequest(properties.getSecretKey(), token));
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(properties.getSiteverifyUrl()))
                    .timeout(Duration.ofSeconds(5))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> httpResponse = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString());
            if (httpResponse.statusCode() < 200 || httpResponse.statusCode() >= 300) {
                LOGGER.warn("Turnstile Siteverify HTTP {} dondu.", httpResponse.statusCode());
                return false;
            }

            SiteverifyResponse response = objectMapper.readValue(
                    httpResponse.body(),
                    SiteverifyResponse.class);
            boolean dogru = response.success()
                    && properties.getExpectedHostname().equalsIgnoreCase(response.hostname())
                    && properties.getExpectedAction().equals(response.action());
            if (!dogru) {
                LOGGER.warn(
                        "Turnstile dogrulamasi reddedildi. hostname={}, action={}, errors={}",
                        response.hostname(),
                        response.action(),
                        response.errorCodes());
            }
            return dogru;
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            LOGGER.warn("Turnstile dogrulamasi kesintiye ugradi.");
            return false;
        } catch (Exception exception) {
            LOGGER.warn("Turnstile dogrulamasi tamamlanamadi: {}", exception.getMessage());
            return false;
        }
    }

    private static boolean bos(String value) {
        return value == null || value.isBlank();
    }

    private record SiteverifyRequest(String secret, String response) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record SiteverifyResponse(
            boolean success,
            String hostname,
            String action,
            @JsonProperty("error-codes") List<String> errorCodes) {
    }
}
