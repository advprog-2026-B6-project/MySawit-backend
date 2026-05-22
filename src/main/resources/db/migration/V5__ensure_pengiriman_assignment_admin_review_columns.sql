ALTER TABLE pengiriman_assignments
    ADD COLUMN IF NOT EXISTS admin_final_approval VARCHAR(255);

ALTER TABLE pengiriman_assignments
    ADD COLUMN IF NOT EXISTS admin_final_note TEXT;

ALTER TABLE pengiriman_assignments
    ADD COLUMN IF NOT EXISTS kilogram_diakui DOUBLE PRECISION;

ALTER TABLE pengiriman_assignments
    ADD COLUMN IF NOT EXISTS admin_final_reviewed_at TIMESTAMP(6);

ALTER TABLE pengiriman_assignments
    DROP CONSTRAINT IF EXISTS pengiriman_assignments_admin_final_approval_check;

ALTER TABLE pengiriman_assignments
    ADD CONSTRAINT pengiriman_assignments_admin_final_approval_check
    CHECK (
        admin_final_approval IS NULL
        OR admin_final_approval IN ('APPROVED', 'REJECTED', 'PARTIALLY_REJECTED')
    );
