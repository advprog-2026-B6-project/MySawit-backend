package id.ac.ui.cs.advprog.mysawit;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class TestController {

    @CrossOrigin(origins = {"http://localhost:3000", "https://my-sawit-frontend.vercel.app"})
    @GetMapping("/test-json")
    public Map<String, String> testJson() {
        return Map.of("text", "Hello from backend");
    }
}

// ignore ci for now as its demanding a verification xml it keeps rejecting