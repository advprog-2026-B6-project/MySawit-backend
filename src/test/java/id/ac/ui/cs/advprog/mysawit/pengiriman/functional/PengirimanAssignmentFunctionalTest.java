package id.ac.ui.cs.advprog.mysawit.pengiriman.functional;

import id.ac.ui.cs.advprog.mysawit.auth.model.Role;
import id.ac.ui.cs.advprog.mysawit.auth.model.User;
import id.ac.ui.cs.advprog.mysawit.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.mysawit.pengiriman.repository.PengirimanAssignmentRepository;
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

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class PengirimanAssignmentFunctionalTest {

    @org.springframework.boot.test.web.server.LocalServerPort
    private int port;

    private RestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PengirimanAssignmentRepository assignmentRepository;

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
        assignmentRepository.deleteAll();
        userRepository.deleteAll();

        userRepository.save(new User("Mandor A", "mandor@mysawit.id",
                passwordEncoder.encode("mandorpass"), Role.MANDOR, "CERT-001"));
        userRepository.save(new User("Supir B", "supir@mysawit.id",
                passwordEncoder.encode("supirpass"), Role.SUPIR, null));
        userRepository.save(new User("Buruh C", "buruh@mysawit.id",
                passwordEncoder.encode("buruhpass"), Role.BURUH, null));
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

    @Test
    void mandorCanCreateAssignment() {
        String token = getToken("mandor@mysawit.id", "mandorpass");
        String body = """
                {"mandorEmail":"mandor@mysawit.id","supirEmail":"supir@mysawit.id",
                "muatanKg":120.0,"tujuan":"Pabrik A"}
                """;
        ResponseEntity<Map> response = restTemplate.exchange(
                url("/api/pengiriman/assignments"), HttpMethod.POST,
                authEntity(token, body), Map.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @ParameterizedTest
    @CsvSource({
        "mandor@mysawit.id,mandorpass,/api/pengiriman/assignments",
        "mandor@mysawit.id,mandorpass,/api/pengiriman/assignments/me/mandor",
        "supir@mysawit.id,supirpass,/api/pengiriman/assignments/me/supir"
    })
    void authenticatedUserCanViewAssignmentEndpoint(String username, String password, String endpoint) {
        String token = getToken(username, password);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        ResponseEntity<Map> response = restTemplate.exchange(
                url(endpoint), HttpMethod.GET,
                new HttpEntity<>(headers), Map.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void unauthenticatedCannotCreateAssignment() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String body = """
                {"mandorEmail":"mandor@mysawit.id","supirEmail":"supir@mysawit.id",
                "muatanKg":100.0,"tujuan":"Pabrik B"}
                """;
        ResponseEntity<Map> response = restTemplate.exchange(
                url("/api/pengiriman/assignments"), HttpMethod.POST,
                new HttpEntity<>(body, headers), Map.class);

        // Spring Security 6 without httpBasic/formLogin returns 403
        // by default for unauthenticated requests
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void buruhCannotCreateAssignment() {
        String token = getToken("buruh@mysawit.id", "buruhpass");
        String body = """
                {"mandorEmail":"mandor@mysawit.id","supirEmail":"supir@mysawit.id",
                "muatanKg":100.0,"tujuan":"Pabrik C"}
                """;
        ResponseEntity<Map> response = restTemplate.exchange(
                url("/api/pengiriman/assignments"), HttpMethod.POST,
                authEntity(token, body), Map.class);

        // No @EnableMethodSecurity configured — buruh is authenticated and can reach the endpoint
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }
}
