package id.ac.ui.cs.advprog.mysawit.pengiriman.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SupirTrukTest {

    @Test
    void testSupirTrukCreationWithBuilder() {
        SupirTruk supirTruk = SupirTruk.builder().build();

        assertNotNull(supirTruk.getId());
        assertNull(supirTruk.getNama());
        assertNull(supirTruk.getNomorTelepon());
        assertNull(supirTruk.getPlatNomorTruk());
        assertFalse(supirTruk.isSedangBertugas());
    }

    @Test
    void testSupirTrukCreationWithParams() {
        SupirTruk supirTruk = SupirTruk.builder()
                .nama("Ahmad Supir")
                .nomorTelepon("08123456789")
                .platNomorTruk("B 1234 XYZ")
                .sedangBertugas(true)
                .build();

        assertNotNull(supirTruk.getId());
        assertEquals("Ahmad Supir", supirTruk.getNama());
        assertEquals("08123456789", supirTruk.getNomorTelepon());
        assertEquals("B 1234 XYZ", supirTruk.getPlatNomorTruk());
        assertTrue(supirTruk.isSedangBertugas());
    }

    @Test
    void testSupirTrukCreationWithCustomId() {
        UUID customId = UUID.randomUUID();
        SupirTruk supirTruk = SupirTruk.builder()
                .id(customId)
                .nama("Budi Supir")
                .nomorTelepon("08987654321")
                .platNomorTruk("D 5678 ABC")
                .sedangBertugas(false)
                .build();

        assertEquals(customId, supirTruk.getId());
        assertEquals("Budi Supir", supirTruk.getNama());
        assertEquals("08987654321", supirTruk.getNomorTelepon());
        assertEquals("D 5678 ABC", supirTruk.getPlatNomorTruk());
        assertFalse(supirTruk.isSedangBertugas());
    }

    @Test
    void testSetNama() {
        SupirTruk supirTruk = SupirTruk.builder().build();
        supirTruk.setNama("Charlie Supir");

        assertEquals("Charlie Supir", supirTruk.getNama());
    }

    @Test
    void testSetNomorTelepon() {
        SupirTruk supirTruk = SupirTruk.builder().build();
        supirTruk.setNomorTelepon("08111222333");

        assertEquals("08111222333", supirTruk.getNomorTelepon());
    }

    @Test
    void testSetPlatNomorTruk() {
        SupirTruk supirTruk = SupirTruk.builder().build();
        supirTruk.setPlatNomorTruk("F 9999 ZZZ");

        assertEquals("F 9999 ZZZ", supirTruk.getPlatNomorTruk());
    }

    @Test
    void testSetSedangBertugas() {
        SupirTruk supirTruk = SupirTruk.builder().build();
        assertFalse(supirTruk.isSedangBertugas());

        supirTruk.setSedangBertugas(true);
        assertTrue(supirTruk.isSedangBertugas());

        supirTruk.setSedangBertugas(false);
        assertFalse(supirTruk.isSedangBertugas());
    }

    @Test
    void testSetId() {
        SupirTruk supirTruk = SupirTruk.builder().build();
        UUID newId = UUID.randomUUID();
        supirTruk.setId(newId);

        assertEquals(newId, supirTruk.getId());
    }

    @Test
    void testNoArgsConstructor() {
        SupirTruk supirTruk = new SupirTruk();

        // NoArgsConstructor with @Builder.Default still initializes id
        assertNotNull(supirTruk.getId());
        assertNull(supirTruk.getNama());
        assertNull(supirTruk.getNomorTelepon());
        assertNull(supirTruk.getPlatNomorTruk());
        assertFalse(supirTruk.isSedangBertugas());
    }

    @Test
    void testAllArgsConstructor() {
        UUID id = UUID.randomUUID();
        SupirTruk supirTruk = new SupirTruk(id, "David Supir", "08444555666", "G 1111 AAA", true);

        assertEquals(id, supirTruk.getId());
        assertEquals("David Supir", supirTruk.getNama());
        assertEquals("08444555666", supirTruk.getNomorTelepon());
        assertEquals("G 1111 AAA", supirTruk.getPlatNomorTruk());
        assertTrue(supirTruk.isSedangBertugas());
    }

    @Test
    void testEqualsAndHashCode() {
        UUID id = UUID.randomUUID();
        SupirTruk supirTruk1 = SupirTruk.builder()
                .id(id)
                .nama("Test Supir")
                .nomorTelepon("08123456789")
                .platNomorTruk("B 1234 XYZ")
                .sedangBertugas(true)
                .build();
        SupirTruk supirTruk2 = SupirTruk.builder()
                .id(id)
                .nama("Test Supir")
                .nomorTelepon("08123456789")
                .platNomorTruk("B 1234 XYZ")
                .sedangBertugas(true)
                .build();

        assertEquals(supirTruk1, supirTruk2);
        assertEquals(supirTruk1.hashCode(), supirTruk2.hashCode());
    }

    @Test
    void testNotEquals() {
        SupirTruk supirTruk1 = SupirTruk.builder()
                .nama("Test1")
                .nomorTelepon("08111111111")
                .platNomorTruk("B 1111 AAA")
                .build();
        SupirTruk supirTruk2 = SupirTruk.builder()
                .nama("Test2")
                .nomorTelepon("08222222222")
                .platNomorTruk("B 2222 BBB")
                .build();

        assertNotEquals(supirTruk1, supirTruk2);
    }

    @Test
    void testToString() {
        SupirTruk supirTruk = SupirTruk.builder()
                .nama("Test Supir")
                .nomorTelepon("08123456789")
                .platNomorTruk("B 1234 XYZ")
                .sedangBertugas(true)
                .build();

        String toString = supirTruk.toString();

        assertTrue(toString.contains("Test Supir"));
        assertTrue(toString.contains("08123456789"));
        assertTrue(toString.contains("B 1234 XYZ"));
        assertTrue(toString.contains("true"));
    }

    @Test
    void testDefaultSedangBertugasIsFalse() {
        SupirTruk supirTruk = SupirTruk.builder()
                .nama("Supir Baru")
                .nomorTelepon("08999888777")
                .platNomorTruk("H 7777 ZZZ")
                .build();

        assertFalse(supirTruk.isSedangBertugas());
    }
}
