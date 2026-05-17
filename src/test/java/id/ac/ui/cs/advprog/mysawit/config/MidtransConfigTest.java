package id.ac.ui.cs.advprog.mysawit.config;

import com.midtrans.Midtrans;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MidtransConfigTest {

    @AfterEach
    void tearDown() {
        Midtrans.serverKey = null;
        Midtrans.clientKey = null;
        Midtrans.isProduction = false;
    }

    @Test
    void testInit() {
        MidtransConfig config = new MidtransConfig();

        ReflectionTestUtils.setField(config, "serverKey", "test-server-key");
        ReflectionTestUtils.setField(config, "clientKey", "test-client-key");
        ReflectionTestUtils.setField(config, "isProduction", true);

        config.init();

        assertEquals("test-server-key", Midtrans.serverKey);
        assertEquals("test-client-key", Midtrans.clientKey);
        assertEquals(true, Midtrans.isProduction);

        assertEquals("test-server-key", com.midtrans.Midtrans.serverKey);
    }
}
