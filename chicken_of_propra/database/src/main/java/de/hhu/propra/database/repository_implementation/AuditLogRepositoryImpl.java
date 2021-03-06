package de.hhu.propra.database.repository_implementation;

import de.hhu.propra.application.repositories.AuditLogRepository;
import de.hhu.propra.database.dao.AuditDao;
import de.hhu.propra.database.entities.AuditEntity;
import de.hhu.propra.domain.auditlog.AuditLog;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class AuditLogRepositoryImpl implements AuditLogRepository {

  private final AuditDao auditDao;

  public AuditLogRepositoryImpl(AuditDao auditDao) {
    this.auditDao = auditDao;
  }


  @Override
  public void save(AuditLog auditLog) {
    auditDao.save(toEntity(auditLog));
  }

  @Override
  public List<AuditLog> nachrichten() {
    List<AuditLog> log = new ArrayList<>();
    for (AuditEntity entity : auditDao.findAll()) {
      log.add(toAuditLog(entity));
    }
    return log;
  }

  private AuditEntity toEntity(AuditLog auditLog) {
    return new AuditEntity(null, auditLog.getAenderung(), auditLog.getHandle(),
        auditLog.getZeitpunkt());
  }

  private AuditLog toAuditLog(AuditEntity entity) {
    AuditLog audit = new AuditLog(entity.aenderung(), entity.handle());
    audit.setZeitpunkt(entity.zeitpunkt());
    return audit;
  }

}
