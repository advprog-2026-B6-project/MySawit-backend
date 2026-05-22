package id.ac.ui.cs.advprog.mysawit.common.controller;

import id.ac.ui.cs.advprog.mysawit.pengiriman.dto.ApiResponse;
import id.ac.ui.cs.advprog.mysawit.pengiriman.service.exception.PengirimanAuthorizationException;
import id.ac.ui.cs.advprog.mysawit.pengiriman.service.exception.PengirimanNotFoundException;
import id.ac.ui.cs.advprog.mysawit.pengiriman.service.exception.PengirimanStateException;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class GlobalExceptionHandlerTest {
    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleBadRequest_returns400() {
        ResponseEntity<ApiResponse<Object>> response = handler.handleBadRequest(
                new IllegalArgumentException("bad request"));

        assertEquals(400, response.getStatusCode().value());
        assertFalse(response.getBody().isSuccess());
        assertEquals("bad request", response.getBody().getMessage());
    }

    @Test
    void handlePengirimanNotFound_returns404() {
        ResponseEntity<ApiResponse<Object>> response = handler.handlePengirimanNotFound(
                new PengirimanNotFoundException("missing"));

        assertEquals(404, response.getStatusCode().value());
        assertEquals("missing", response.getBody().getMessage());
    }

    @Test
    void handlePengirimanForbidden_returns403() {
        ResponseEntity<ApiResponse<Object>> response = handler.handlePengirimanForbidden(
                new PengirimanAuthorizationException("forbidden"));

        assertEquals(403, response.getStatusCode().value());
        assertEquals("forbidden", response.getBody().getMessage());
    }

    @Test
    void handlePengirimanStateConflict_returns409() {
        ResponseEntity<ApiResponse<Object>> response = handler.handlePengirimanStateConflict(
                new PengirimanStateException("conflict"));

        assertEquals(409, response.getStatusCode().value());
        assertEquals("conflict", response.getBody().getMessage());
    }

    @Test
    void handleUnauthorized_returns401() {
        ResponseEntity<ApiResponse<Object>> response = handler.handleUnauthorized(
                new IllegalStateException("unauthorized"));

        assertEquals(401, response.getStatusCode().value());
        assertEquals("unauthorized", response.getBody().getMessage());
    }

    @Test
    void handleForbidden_returns403() {
        ResponseEntity<ApiResponse<Object>> response = handler.handleForbidden(
                new AccessDeniedException("denied"));

        assertEquals(403, response.getStatusCode().value());
        assertEquals("denied", response.getBody().getMessage());
    }
}
