package id.ac.ui.cs.advprog.mysawit.common.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HealthControllerTest {

    @Test
    void health_returnsOk() {
        ResponseEntity<String> response = new HealthController().health();

        assertEquals(200, response.getStatusCode().value());
        assertEquals("ok", response.getBody());
    }
}
