package de.hhu.propra.application.services;

import de.hhu.propra.application.repositories.AuditLogRepository;
import de.hhu.propra.application.stereotypes.ApplicationService;
import de.hhu.propra.domain.auditLog.AuditLog;

import java.util.List;

@ApplicationService
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public List<AuditLog> alle(){
        return auditLogRepository.nachrichten();
    }
}
