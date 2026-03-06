package id.ac.ui.cs.advprog.mysawit.pengiriman.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
