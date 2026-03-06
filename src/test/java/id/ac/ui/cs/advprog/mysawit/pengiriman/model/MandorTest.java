package id.ac.ui.cs.advprog.mysawit.pengiriman.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MandorTest {

    @Test
    void testMandorCreationWithBuilder() {
        Mandor mandor = Mandor.builder().build();

        assertNotNull(mandor.getId());
        assertNull(mandor.getNama());
        assertNull(mandor.getEmail());
    }

    @Test
    void testMandorCreationWithParams() {
        Mandor mandor = Mandor.builder()
                .nama("Ahmad")
                .email("ahmad@example.com")
                .build();

        assertNotNull(mandor.getId());
        assertEquals("Ahmad", mandor.getNama());
        assertEquals("ahmad@example.com", mandor.getEmail());
    }

    @Test
    void testMandorCreationWithCustomId() {
        UUID customId = UUID.randomUUID();
        Mandor mandor = Mandor.builder()
                .id(customId)
                .nama("Budi")
                .email("budi@example.com")
                .build();

        assertEquals(customId, mandor.getId());
        assertEquals("Budi", mandor.getNama());
        assertEquals("budi@example.com", mandor.getEmail());
    }

    @Test
    void testSetNama() {
        Mandor mandor = Mandor.builder().build();
        mandor.setNama("Charlie");

        assertEquals("Charlie", mandor.getNama());
    }

    @Test
    void testSetEmail() {
        Mandor mandor = Mandor.builder().build();
        mandor.setEmail("charlie@example.com");

        assertEquals("charlie@example.com", mandor.getEmail());
    }

    @Test
    void testSetId() {
        Mandor mandor = Mandor.builder().build();
        UUID newId = UUID.randomUUID();
        mandor.setId(newId);

        assertEquals(newId, mandor.getId());
    }

    @Test
    void testNoArgsConstructor() {
        Mandor mandor = new Mandor();

        // NoArgsConstructor with @Builder.Default still initializes id
        assertNotNull(mandor.getId());
        assertNull(mandor.getNama());
        assertNull(mandor.getEmail());
    }

    @Test
    void testAllArgsConstructor() {
        UUID id = UUID.randomUUID();
        Mandor mandor = new Mandor(id, "David", "david@example.com");

        assertEquals(id, mandor.getId());
        assertEquals("David", mandor.getNama());
        assertEquals("david@example.com", mandor.getEmail());
    }

    @Test
    void testEqualsAndHashCode() {
        UUID id = UUID.randomUUID();
        Mandor mandor1 = Mandor.builder()
                .id(id)
                .nama("Test")
                .email("test@example.com")
                .build();
        Mandor mandor2 = Mandor.builder()
                .id(id)
                .nama("Test")
                .email("test@example.com")
                .build();

        assertEquals(mandor1, mandor2);
        assertEquals(mandor1.hashCode(), mandor2.hashCode());
    }

    @Test
    void testNotEquals() {
        Mandor mandor1 = Mandor.builder()
                .nama("Test1")
                .email("test1@example.com")
                .build();
        Mandor mandor2 = Mandor.builder()
                .nama("Test2")
                .email("test2@example.com")
                .build();

        assertNotEquals(mandor1, mandor2);
    }

    @Test
    void testToString() {
        Mandor mandor = Mandor.builder()
                .nama("Test")
                .email("test@example.com")
                .build();

        String toString = mandor.toString();

        assertTrue(toString.contains("Test"));
        assertTrue(toString.contains("test@example.com"));
    }
}
