package id.ac.ui.cs.advprog.mysawit.auth;

import com.fasterxml.jackson.databind.ObjectMapper;

import id.ac.ui.cs.advprog.mysawit.auth.dto.LoginRequest;
import id.ac.ui.cs.advprog.mysawit.auth.dto.RegisterRequest;
import id.ac.ui.cs.advprog.mysawit.auth.model.Role;
import id.ac.ui.cs.advprog.mysawit.auth.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
public class AuthControllerTest {

    private MockMvc mvc;

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        this.mvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    void registerAndLogin_shouldWork() throws Exception {
        String username = "testuser";
        userRepository.findByUsername(username).ifPresent(u -> userRepository.delete(u));

        RegisterRequest reg = new RegisterRequest();
        reg.setFullname("Test User");
        reg.setUsername(username);
        reg.setPassword("pass123");
        reg.setRole(Role.BURUH);

        mvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(reg)))
                .andExpect(status().isOk());

        LoginRequest auth = new LoginRequest();
        auth.setUsername(username);
        auth.setPassword("pass123");

        mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(auth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString());
    }
}
