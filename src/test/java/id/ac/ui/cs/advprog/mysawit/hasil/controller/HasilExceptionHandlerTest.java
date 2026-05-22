package id.ac.ui.cs.advprog.mysawit.hasil.controller;

import id.ac.ui.cs.advprog.mysawit.hasil.service.DailySubmissionLimitException;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HasilExceptionHandlerTest {
    private final HasilExceptionHandler handler = new HasilExceptionHandler();

    @Test
    void handleDailyLimit_returnsConflict() {
        ResponseEntity<Map<String, String>> response = handler.handleDailyLimit(
                new DailySubmissionLimitException("daily limit"));

        assertEquals(409, response.getStatusCode().value());
        assertEquals("daily limit", response.getBody().get("error"));
    }

    @Test
    void handleBadRequest_returnsBadRequest() {
        ResponseEntity<Map<String, String>> response = handler.handleBadRequest(
                new IllegalArgumentException("bad request"));

        assertEquals(400, response.getStatusCode().value());
        assertEquals("bad request", response.getBody().get("error"));
    }

    @Test
    void handleForbidden_returnsForbidden() {
        ResponseEntity<Map<String, String>> response = handler.handleForbidden(
                new AccessDeniedException("forbidden"));

        assertEquals(403, response.getStatusCode().value());
        assertEquals("forbidden", response.getBody().get("error"));
    }
}
