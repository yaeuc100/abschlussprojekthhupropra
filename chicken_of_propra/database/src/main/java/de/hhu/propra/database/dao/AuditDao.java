package de.hhu.propra.database.dao;

import de.hhu.propra.database.entities.AuditEntity;
import org.springframework.data.repository.CrudRepository;

public interface AuditDao extends CrudRepository<AuditEntity, Long> {

}
