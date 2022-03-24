package de.hhu.propra.application.services;

import de.hhu.propra.application.dto.AuditDto;
import de.hhu.propra.application.repositories.AuditLogRepository;
import de.hhu.propra.application.stereotypes.ApplicationService;
import de.hhu.propra.domain.auditLog.AuditLog;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationService
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public List<AuditDto> alle(){
        return auditLogRepository.nachrichten().stream().map(AuditDto::toAuditDto).collect(Collectors.toList());
    }
}
