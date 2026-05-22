package id.ac.ui.cs.advprog.mysawit.hasil.functional;

import id.ac.ui.cs.advprog.mysawit.auth.model.Role;
import id.ac.ui.cs.advprog.mysawit.auth.model.User;
import id.ac.ui.cs.advprog.mysawit.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.mysawit.hasil.model.Hasil;
import id.ac.ui.cs.advprog.mysawit.hasil.model.HasilStatus;
import id.ac.ui.cs.advprog.mysawit.hasil.repository.HasilJpaRepository;
import id.ac.ui.cs.advprog.mysawit.hasil.repository.HasilRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class HasilFunctionalTest {

    @org.springframework.boot.test.web.server.LocalServerPort
    private int port;

    private RestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HasilJpaRepository hasilJpaRepository;

    @Autowired
    private HasilRepository hasilRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) {
                return false;
            }
        });
        clearHasilRepository();
        userRepository.deleteAll();

        userRepository.save(new User("Mandor Satu", "mandor1",
                passwordEncoder.encode("mandorpass"), Role.MANDOR, null, null));
        userRepository.save(new User("Buruh Satu", "buruh1",
                passwordEncoder.encode("buruhpass"), Role.BURUH, null, "mandor1"));
    }

    private void clearHasilRepository() {
        hasilJpaRepository.deleteAll();
    }

    private String url(String path) {
        return "http://localhost:" + port + path;
    }

    private String getToken(String username, String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String body = String.format(
                "{\"username\":\"%s\",\"password\":\"%s\"}", username, password);
        ResponseEntity<Map> response = restTemplate.exchange(
                url("/auth/login"), HttpMethod.POST, new HttpEntity<>(body, headers), Map.class);
        return (String) response.getBody().get("token");
    }

    private HttpEntity<String> authEntity(String token, String body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        return new HttpEntity<>(body, headers);
    }

    @ParameterizedTest
    @CsvSource({
        "buruh1,buruhpass,/hasil-reports/me/today",
        "buruh1,buruhpass,/hasil-reports/me/history",
        "mandor1,mandorpass,/hasil-reports/mandor/history"
    })
    void authenticatedUserCanReadHasilEndpoint(String username, String password, String endpoint) {
        String token = getToken(username, password);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        ResponseEntity<Object> response = restTemplate.exchange(
                url(endpoint), HttpMethod.GET,
                new HttpEntity<>(headers), Object.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void mandorCanApproveSubmittedReport() {
        Hasil report = hasilRepository.save(
                Hasil.of("test-approve", "buruh1", LocalDate.now(),
                        100.0, "Panen test", List.of("foto.jpg"),
                        true, HasilStatus.SUBMITTED));

        String token = getToken("mandor1", "mandorpass");
        ResponseEntity<Map> response = restTemplate.exchange(
                url("/hasil-reports/mandor/" + report.getId() + "/approve"),
                HttpMethod.PUT, authEntity(token, null), Map.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("VERIFIED", response.getBody().get("status"));
    }

    @Test
    void mandorCanRejectSubmittedReport() {
        Hasil report = hasilRepository.save(
                Hasil.of("test-reject", "buruh1", LocalDate.now().minusDays(1),
                        80.0, "Panen reject", List.of("foto.jpg"),
                        true, HasilStatus.SUBMITTED));

        String token = getToken("mandor1", "mandorpass");
        ResponseEntity<Map> response = restTemplate.exchange(
                url("/hasil-reports/mandor/" + report.getId() + "/reject"),
                HttpMethod.PUT,
                authEntity(token, "{\"rejectionReason\":\"Foto tidak jelas\"}"),
                Map.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("REJECTED", response.getBody().get("status"));
    }

    @Test
    void unauthenticatedCannotAccessHasilEndpoint() {
        ResponseEntity<Map> response = restTemplate.getForEntity(
                url("/hasil-reports/me/today"), Map.class);
        // Spring Security 6 without httpBasic/formLogin returns 403
        // by default for unauthenticated requests
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void availableForPengirimanReturnsOk() {
        String token = getToken("mandor1", "mandorpass");
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        ResponseEntity<Object[]> response = restTemplate.exchange(
                url("/hasil-reports/pengiriman/available"), HttpMethod.GET,
                new HttpEntity<>(headers), Object[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}
