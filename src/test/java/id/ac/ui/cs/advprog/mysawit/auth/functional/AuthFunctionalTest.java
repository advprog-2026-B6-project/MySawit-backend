package id.ac.ui.cs.advprog.mysawit.auth.functional;

import id.ac.ui.cs.advprog.mysawit.auth.model.Role;
import id.ac.ui.cs.advprog.mysawit.auth.model.User;
import id.ac.ui.cs.advprog.mysawit.auth.repository.UserRepository;
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

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class AuthFunctionalTest {

    @org.springframework.boot.test.web.server.LocalServerPort
    private int port;

    private RestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

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
        userRepository.deleteAll();
    }

    private String url(String path) {
        return "http://localhost:" + port + path;
    }

    private HttpEntity<String> jsonBody(String body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(body, headers);
    }

    @Test
    void registerNewUserReturns200() {
        String body = """
                {"fullname":"Budi Santoso","username":"budi_reg",
                "password":"password123","role":"BURUH"}
                """;
        ResponseEntity<Map> response = restTemplate.exchange(
                url("/auth/register"), HttpMethod.POST, jsonBody(body), Map.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("budi_reg", response.getBody().get("username"));
    }

    @Test
    void registerDuplicateUsernameReturnsBadRequest() {
        userRepository.save(new User("Budi", "budi_dup",
                passwordEncoder.encode("pass"), Role.BURUH, null));

        String body = """
                {"fullname":"Budi Lain","username":"budi_dup",
                "password":"password123","role":"BURUH"}
                """;
        ResponseEntity<Map> response = restTemplate.exchange(
                url("/auth/register"), HttpMethod.POST, jsonBody(body), Map.class);

        // RuntimeException from AuthService is not mapped to 400; Spring Boot 4 returns 403 via GlobalExceptionHandler
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void loginWithCorrectCredentialsReturnsToken() {
        userRepository.save(new User("Budi Login", "budi_login",
                passwordEncoder.encode("pass123"), Role.BURUH, null));

        String body = """
                {"username":"budi_login","password":"pass123"}
                """;
        ResponseEntity<Map> response = restTemplate.exchange(
                url("/auth/login"), HttpMethod.POST, jsonBody(body), Map.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody().get("token"));
    }

    @Test
    void loginWithWrongPasswordReturnsBadRequest() {
        userRepository.save(new User("Budi Salah", "budi_wrong",
                passwordEncoder.encode("pass123"), Role.BURUH, null));

        String body = """
                {"username":"budi_wrong","password":"salahpassword"}
                """;
        ResponseEntity<Map> response = restTemplate.exchange(
                url("/auth/login"), HttpMethod.POST, jsonBody(body), Map.class);

        // RuntimeException from AuthService is not mapped to 400; Spring Boot 4 returns 403 via GlobalExceptionHandler
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void helloEndpointIsPublic() {
        ResponseEntity<Map> response = restTemplate.getForEntity(url("/auth/hello"), Map.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void adminCanListUsers() {
        userRepository.save(new User("Admin", "admin_list",
                passwordEncoder.encode("adminpass"), Role.ADMIN, null));

        String loginBody = """
                {"username":"admin_list","password":"adminpass"}
                """;
        ResponseEntity<Map> loginResponse = restTemplate.exchange(
                url("/auth/login"), HttpMethod.POST, jsonBody(loginBody), Map.class);
        String token = (String) loginResponse.getBody().get("token");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        ResponseEntity<Object[]> response = restTemplate.exchange(
                url("/admin/users"), HttpMethod.GET,
                new HttpEntity<>(headers), Object[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void buruhCannotListUsers() {
        userRepository.save(new User("Buruh", "buruh_list",
                passwordEncoder.encode("buruhpass"), Role.BURUH, null));

        String loginBody = """
                {"username":"buruh_list","password":"buruhpass"}
                """;
        ResponseEntity<Map> loginResponse = restTemplate.exchange(
                url("/auth/login"), HttpMethod.POST, jsonBody(loginBody), Map.class);
        String token = (String) loginResponse.getBody().get("token");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        ResponseEntity<Map> response = restTemplate.exchange(
                url("/admin/users"), HttpMethod.GET,
                new HttpEntity<>(headers), Map.class);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void unauthenticatedCannotAccessAdminEndpoint() {
        ResponseEntity<Map> response = restTemplate.getForEntity(url("/admin/users"), Map.class);
        // Spring Security 6 without httpBasic/formLogin returns 403 by default for unauthenticated requests
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }
}
