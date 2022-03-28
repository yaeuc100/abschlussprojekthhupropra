package de.hhu.propra.application.dto;

import de.hhu.propra.domain.auditlog.AuditLog;
import java.time.LocalDateTime;

public record AuditDto(LocalDateTime datum, String handle, String aenderung) {

  public static AuditDto toAuditDto(AuditLog log) {
    return new AuditDto(log.getZeitpunkt(),
        log.getHandle(),
        log.getAenderung());
  }
}
