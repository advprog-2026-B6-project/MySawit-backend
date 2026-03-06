package id.ac.ui.cs.advprog.mysawit.pengiriman.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StatusPengirimanTest {

    @Test
    void testStatusPengirimanDisplayNames() {
        assertEquals("Menunggu", StatusPengiriman.MENUNGGU.getDisplayName());
        assertEquals("Memuat", StatusPengiriman.MEMUAT.getDisplayName());
        assertEquals("Mengirim", StatusPengiriman.MENGIRIM.getDisplayName());
        assertEquals("Tiba", StatusPengiriman.TIBA.getDisplayName());
    }

    @Test
    void testStatusPengirimanValues() {
        StatusPengiriman[] values = StatusPengiriman.values();
        assertEquals(4, values.length);
    }

    @Test
    void testValueOfMenunggu() {
        StatusPengiriman status = StatusPengiriman.valueOf("MENUNGGU");
        assertEquals(StatusPengiriman.MENUNGGU, status);
    }

    @Test
    void testValueOfMemuat() {
        StatusPengiriman status = StatusPengiriman.valueOf("MEMUAT");
        assertEquals(StatusPengiriman.MEMUAT, status);
    }

    @Test
    void testValueOfMengirim() {
        StatusPengiriman status = StatusPengiriman.valueOf("MENGIRIM");
        assertEquals(StatusPengiriman.MENGIRIM, status);
    }

    @Test
    void testValueOfTiba() {
        StatusPengiriman status = StatusPengiriman.valueOf("TIBA");
        assertEquals(StatusPengiriman.TIBA, status);
    }

    @Test
    void testValueOfInvalid() {
        assertThrows(IllegalArgumentException.class, () -> 
                StatusPengiriman.valueOf("INVALID"));
    }

    @Test
    void testEnumOrdinal() {
        assertEquals(0, StatusPengiriman.MENUNGGU.ordinal());
        assertEquals(1, StatusPengiriman.MEMUAT.ordinal());
        assertEquals(2, StatusPengiriman.MENGIRIM.ordinal());
        assertEquals(3, StatusPengiriman.TIBA.ordinal());
    }

    @Test
    void testEnumName() {
        assertEquals("MENUNGGU", StatusPengiriman.MENUNGGU.name());
        assertEquals("MEMUAT", StatusPengiriman.MEMUAT.name());
        assertEquals("MENGIRIM", StatusPengiriman.MENGIRIM.name());
        assertEquals("TIBA", StatusPengiriman.TIBA.name());
    }

    @Test
    void testEnumNotNull() {
        for (StatusPengiriman status : StatusPengiriman.values()) {
            assertNotNull(status.getDisplayName());
        }
    }
}
