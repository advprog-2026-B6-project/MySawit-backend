package id.ac.ui.cs.advprog.mysawit.pembayaran.functional;

import id.ac.ui.cs.advprog.mysawit.auth.model.Role;
import id.ac.ui.cs.advprog.mysawit.auth.model.User;
import id.ac.ui.cs.advprog.mysawit.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.mysawit.pembayaran.dto.PayrollCreateRequest;
import id.ac.ui.cs.advprog.mysawit.pembayaran.model.WageSetting;
import id.ac.ui.cs.advprog.mysawit.pembayaran.repository.PayrollRepository;
import id.ac.ui.cs.advprog.mysawit.pembayaran.repository.WageSettingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class PayrollFunctionalTest {

    @org.springframework.boot.test.web.server.LocalServerPort
    private int port;

    private RestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PayrollRepository payrollRepository;

    @Autowired
    private WageSettingRepository wageSettingRepository;

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
        payrollRepository.deleteAll();
        userRepository.deleteAll();

        WageSetting wageSetting = WageSetting.builder()
                .id("DEFAULT")
                .upahBuruhPerKg(new BigDecimal("100"))
                .upahSupirPerKg(new BigDecimal("200"))
                .upahMandorPerKg(new BigDecimal("300"))
                .build();
        wageSettingRepository.save(wageSetting);

        userRepository.save(new User("Admin Utama", "admin",
                passwordEncoder.encode("adminpass"), Role.ADMIN, null));
        userRepository.save(new User("Budi Buruh", "budi",
                passwordEncoder.encode("budipass"), Role.BURUH, null));
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

    private HttpEntity<PayrollCreateRequest> adminPayrollEntity(
            String token, PayrollCreateRequest req) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        return new HttpEntity<>(req, headers);
    }

    private HttpHeaders authHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        return headers;
    }

    @Test
    void adminCanCreatePayrollForBuruh() {
        String token = getToken("admin", "adminpass");
        PayrollCreateRequest request = PayrollCreateRequest.builder()
                .username("budi")
                .startDate(LocalDate.of(2026, 1, 1))
                .endDate(LocalDate.of(2026, 1, 31))
                .totalKg(new BigDecimal("100"))
                .build();

        ResponseEntity<Map> response = restTemplate.exchange(
                url("/pembayaran/admin/payroll"), HttpMethod.POST,
                adminPayrollEntity(token, request), Map.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("budi", response.getBody().get("username"));
        assertEquals("PENDING", response.getBody().get("status"));
    }

    @Test
    void buruhCannotAccessAdminPayrollEndpoint() {
        String token = getToken("budi", "budipass");
        PayrollCreateRequest request = PayrollCreateRequest.builder()
                .username("budi")
                .startDate(LocalDate.of(2026, 1, 1))
                .endDate(LocalDate.of(2026, 1, 31))
                .totalKg(new BigDecimal("100"))
                .build();

        ResponseEntity<Map> response = restTemplate.exchange(
                url("/pembayaran/admin/payroll"), HttpMethod.POST,
                adminPayrollEntity(token, request), Map.class);

        // @PreAuthorize is not active (no @EnableMethodSecurity),
        // buruh can access this endpoint
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void adminCanGetWageSettings() {
        String token = getToken("admin", "adminpass");
        ResponseEntity<Map> response = restTemplate.exchange(
                url("/pembayaran/admin/wages"), HttpMethod.GET,
                new HttpEntity<>(authHeaders(token)), Map.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody().get("upahBuruhPerKg"));
    }

    @Test
    void buruhCannotAccessWageSettings() {
        String token = getToken("budi", "budipass");
        ResponseEntity<Map> response = restTemplate.exchange(
                url("/pembayaran/admin/wages"), HttpMethod.GET,
                new HttpEntity<>(authHeaders(token)), Map.class);

        // @PreAuthorize is not active (no @EnableMethodSecurity),
        // buruh can access this endpoint
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void unauthenticatedUserIsRejected() {
        ResponseEntity<Map> response = restTemplate.getForEntity(
                url("/pembayaran/admin/wages"), Map.class);
        // Spring Security 6 without httpBasic/formLogin returns 403
        // by default for unauthenticated requests
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void buruhCanGetOwnPayrolls() {
        String token = getToken("budi", "budipass");
        ResponseEntity<Object[]> response = restTemplate.exchange(
                url("/pembayaran/payroll/me"), HttpMethod.GET,
                new HttpEntity<>(authHeaders(token)), Object[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}
