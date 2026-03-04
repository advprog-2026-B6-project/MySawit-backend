package id.ac.ui.cs.advprog.mysawit;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;

import static org.mockito.Mockito.mockStatic;

class MySawitApplicationMainTest {

    @Test
    void mainMethodRunsApplication() {
        try (var mocked = mockStatic(SpringApplication.class)) {
            MySawitApplication.main(new String[]{});
            mocked.verify(() -> SpringApplication.run(MySawitApplication.class, new String[]{}));
        }
    }
}
