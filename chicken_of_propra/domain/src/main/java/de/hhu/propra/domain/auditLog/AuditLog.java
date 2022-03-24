package de.hhu.propra.domain.auditLog;

import java.time.LocalDateTime;
import java.util.Objects;

public class AuditLog {
    private String aenderung;
    private String handle;
    private LocalDateTime zeitpunkt;

    public AuditLog(String aenderung, String handle) {
        this.aenderung = aenderung;
        this.handle = handle;
        zeitpunkt = LocalDateTime.now();
    }

    public String getAenderung() {
        return aenderung;
    }

    public String getHandle() {
        return handle;
    }

    public LocalDateTime getZeitpunkt() {
        return zeitpunkt;
    }

    public void setZeitpunkt(LocalDateTime zeitpunkt){
        this.zeitpunkt = zeitpunkt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuditLog auditLog = (AuditLog) o;
        return Objects.equals(aenderung, auditLog.aenderung) && Objects.equals(handle, auditLog.handle)
                && Objects.equals(zeitpunkt, auditLog.zeitpunkt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aenderung, handle, zeitpunkt);
    }
}
