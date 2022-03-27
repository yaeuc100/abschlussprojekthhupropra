package de.hhu.propra.application.repositories;

import de.hhu.propra.domain.auditLog.AuditLog;

import java.util.List;

public interface AuditLogRepository {

    void save(AuditLog auditLog);

    List<AuditLog> nachrichten();
}
