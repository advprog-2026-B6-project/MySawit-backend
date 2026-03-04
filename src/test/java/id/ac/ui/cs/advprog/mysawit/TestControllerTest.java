package id.ac.ui.cs.advprog.mysawit;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TestControllerTest {

    @Test
    void testJsonReturnsExpectedResponse() {
        TestController controller = new TestController();
        Map<String, String> result = controller.testJson();
        assertEquals("Hello from backend", result.get("text"));
    }
}
