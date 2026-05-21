package id.ac.ui.cs.advprog.mysawit.pengiriman.service;

import id.ac.ui.cs.advprog.mysawit.pengiriman.model.ApprovalAssignment;
import id.ac.ui.cs.advprog.mysawit.pengiriman.model.PengirimanAssignment;
import id.ac.ui.cs.advprog.mysawit.pengiriman.model.StatusPengiriman;

import java.time.LocalDate;

public final class PengirimanValidationRules {
    private PengirimanValidationRules() {
    }

    public static void validateDateRange(LocalDate tanggalMulai, LocalDate tanggalSelesai) {
        if (tanggalMulai != null && tanggalSelesai != null && tanggalMulai.isAfter(tanggalSelesai)) {
            throw new IllegalArgumentException("Tanggal mulai tidak boleh setelah tanggal selesai");
        }
    }

    public static void validateMuatanPengiriman(double muatanKg, double maxMuatanKg) {
        if (muatanKg > maxMuatanKg) {
            throw new IllegalArgumentException(
                    "Muatan melebihi batas maksimal. Maksimal muatan adalah " + maxMuatanKg + " kg");
        }
        if (muatanKg <= 0) {
            throw new IllegalArgumentException("Muatan harus lebih dari 0 kg");
        }
    }

    public static void validateMuatanAssignment(double muatanKg) {
        if (muatanKg <= 0 || muatanKg > 400) {
            throw new IllegalArgumentException("Muatan harus antara 0 - 400 kg");
        }
    }

    public static void validateStatusTransition(StatusPengiriman statusSaatIni, StatusPengiriman statusBaru) {
        boolean valid = switch (statusSaatIni) {
            case MENUNGGU -> statusBaru == StatusPengiriman.MEMUAT;
            case MEMUAT -> statusBaru == StatusPengiriman.MENGIRIM;
            case MENGIRIM -> statusBaru == StatusPengiriman.TIBA;
            case TIBA, DISETUJUI, DITOLAK -> false;
        };

        if (!valid) {
            throw new IllegalArgumentException(
                    "Transisi status tidak valid dari " + statusSaatIni.getDisplayName()
                            + " ke " + statusBaru.getDisplayName());
        }
    }

    public static String normalizeRequiredReason(String alasanPenolakan) {
        if (alasanPenolakan == null || alasanPenolakan.trim().isEmpty()) {
            throw new IllegalArgumentException("Alasan penolakan wajib diisi");
        }
        return alasanPenolakan.trim();
    }

    public static void validateMuatanDiakui(double muatanKgDiakui, double muatanKg) {
        if (muatanKgDiakui <= 0) {
            throw new IllegalArgumentException("Kilogram diakui harus lebih dari 0 kg");
        }
        if (muatanKgDiakui >= muatanKg) {
            throw new IllegalArgumentException("Kilogram diakui harus lebih kecil dari muatan");
        }
    }

    public static void validateAssignmentApprovedByMandor(PengirimanAssignment assignment) {
        if (assignment.getApproval() != ApprovalAssignment.APPROVED) {
            throw new IllegalArgumentException("Penugasan belum disetujui oleh mandor");
        }
    }
}
