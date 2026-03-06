package id.ac.ui.cs.advprog.mysawit.pengiriman.model;

public enum StatusPengiriman {
    MENUNGGU("Menunggu"),
    MEMUAT("Memuat"),
    MENGIRIM("Mengirim"),
    TIBA("Tiba");

    private final String displayName;

    StatusPengiriman(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
