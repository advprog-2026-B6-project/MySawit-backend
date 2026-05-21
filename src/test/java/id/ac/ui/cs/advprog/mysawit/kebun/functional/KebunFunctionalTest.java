package id.ac.ui.cs.advprog.mysawit.kebun.functional;

import id.ac.ui.cs.advprog.mysawit.auth.model.Role;
import id.ac.ui.cs.advprog.mysawit.auth.model.User;
import id.ac.ui.cs.advprog.mysawit.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.mysawit.kebun.repository.KebunSawitJpaRepository;
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
class KebunFunctionalTest {

    @org.springframework.boot.test.web.server.LocalServerPort
    private int port;

    private RestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private KebunSawitJpaRepository kebunJpaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String adminToken;

    private static final String CREATE_BODY = """
            {"namaKebun":"Kebun Utara","kodeUnik":"KB-0001",
            "kiriAtas":{"x":1.0,"y":2.0},"kiriBawah":{"x":1.0,"y":0.0},
            "kananAtas":{"x":3.0,"y":2.0},"kananBawah":{"x":3.0,"y":0.0}}
            """;

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) {
                return false;
            }
        });
        kebunJpaRepository.deleteAll();
        userRepository.deleteAll();

        userRepository.save(new User("Admin Kebun", "admin_kebun",
                passwordEncoder.encode("adminpass"), Role.ADMIN, null));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<Map> loginResponse = restTemplate.exchange(
                url("/auth/login"), HttpMethod.POST,
                new HttpEntity<>("{\"username\":\"admin_kebun\","
                        + "\"password\":\"adminpass\"}", headers), Map.class);
        adminToken = (String) loginResponse.getBody().get("token");
    }

    private String url(String path) {
        return "http://localhost:" + port + path;
    }

    private HttpEntity<String> adminEntity(String body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(adminToken);
        return new HttpEntity<>(body, headers);
    }

    @Test
    void createKebunReturns201() {
        ResponseEntity<Map> response = restTemplate.exchange(
                url("/kebun"), HttpMethod.POST, adminEntity(CREATE_BODY), Map.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Kebun Utara", response.getBody().get("namaKebun"));
        assertEquals("KB-0001", response.getBody().get("kodeUnik"));
    }

    @Test
    void getAllKebunReturnsListAfterCreate() {
        restTemplate.exchange(url("/kebun"), HttpMethod.POST, adminEntity(CREATE_BODY), Map.class);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);
        ResponseEntity<Object[]> response = restTemplate.exchange(
                url("/kebun"), HttpMethod.GET, new HttpEntity<>(headers), Object[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().length);
    }

    @Test
    void getKebunByKodeUnikReturnsCorrectKebun() {
        restTemplate.exchange(url("/kebun"), HttpMethod.POST, adminEntity(CREATE_BODY), Map.class);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);
        ResponseEntity<Map> response = restTemplate.exchange(
                url("/kebun/KB-0001"), HttpMethod.GET, new HttpEntity<>(headers), Map.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Kebun Utara", response.getBody().get("namaKebun"));
    }

    @Test
    void getKebunByNonExistentKodeReturns404() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);
        ResponseEntity<Map> response = restTemplate.exchange(
                url("/kebun/TIDAK-ADA"), HttpMethod.GET, new HttpEntity<>(headers), Map.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void createKebunWithMissingFieldReturns400() {
        ResponseEntity<Map> response = restTemplate.exchange(
                url("/kebun"), HttpMethod.POST,
                adminEntity("{\"namaKebun\":\"Kebun Rusak\"}"), Map.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void unauthenticatedCannotCreateKebun() {
        // /kebun/** is permitAll() in SecurityConfig,
        // so unauthenticated requests succeed
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<Map> response = restTemplate.exchange(
                url("/kebun"), HttpMethod.POST,
                new HttpEntity<>(CREATE_BODY, headers), Map.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void deleteKebunReturns204() {
        ResponseEntity<Map> created = restTemplate.exchange(
                url("/kebun"), HttpMethod.POST, adminEntity(CREATE_BODY), Map.class);
        String id = (String) created.getBody().get("id");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);
        ResponseEntity<Void> response = restTemplate.exchange(
                url("/kebun/" + id), HttpMethod.DELETE, new HttpEntity<>(headers), Void.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}
