package id.ac.ui.cs.advprog.mysawit.pengiriman.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApiResponseTest {

    @Test
    void testDefaultConstructor() {
        ApiResponse<String> response = new ApiResponse<>();

        assertFalse(response.isSuccess());
        assertNull(response.getMessage());
        assertNull(response.getData());
    }

    @Test
    void testParameterizedConstructor() {
        ApiResponse<String> response = new ApiResponse<>(true, "Success message", "Data");

        assertTrue(response.isSuccess());
        assertEquals("Success message", response.getMessage());
        assertEquals("Data", response.getData());
    }

    @Test
    void testSuccessStaticMethod() {
        ApiResponse<String> response = ApiResponse.success("Operation successful", "Result data");

        assertTrue(response.isSuccess());
        assertEquals("Operation successful", response.getMessage());
        assertEquals("Result data", response.getData());
    }

    @Test
    void testErrorStaticMethod() {
        ApiResponse<String> response = ApiResponse.error("Error occurred");

        assertFalse(response.isSuccess());
        assertEquals("Error occurred", response.getMessage());
        assertNull(response.getData());
    }

    @Test
    void testSetSuccess() {
        ApiResponse<String> response = new ApiResponse<>();
        response.setSuccess(true);

        assertTrue(response.isSuccess());
    }

    @Test
    void testSetMessage() {
        ApiResponse<String> response = new ApiResponse<>();
        response.setMessage("Test message");

        assertEquals("Test message", response.getMessage());
    }

    @Test
    void testSetData() {
        ApiResponse<String> response = new ApiResponse<>();
        response.setData("Test data");

        assertEquals("Test data", response.getData());
    }

    @Test
    void testSuccessWithNullData() {
        ApiResponse<String> response = ApiResponse.success("Success", null);

        assertTrue(response.isSuccess());
        assertEquals("Success", response.getMessage());
        assertNull(response.getData());
    }

    @Test
    void testWithIntegerData() {
        ApiResponse<Integer> response = ApiResponse.success("Number retrieved", 42);

        assertTrue(response.isSuccess());
        assertEquals(42, response.getData());
    }

    @Test
    void testWithObjectData() {
        TestObject obj = new TestObject("test", 123);
        ApiResponse<TestObject> response = ApiResponse.success("Object retrieved", obj);

        assertTrue(response.isSuccess());
        assertEquals(obj, response.getData());
        assertEquals("test", response.getData().getName());
        assertEquals(123, response.getData().getValue());
    }

    // Helper class for testing with objects
    private static class TestObject {
        private String name;
        private int value;

        TestObject(String name, int value) {
            this.name = name;
            this.value = value;
        }

        String getName() { return name; }
        int getValue() { return value; }
    }
}
