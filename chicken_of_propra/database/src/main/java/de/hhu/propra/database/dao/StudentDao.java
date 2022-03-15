package de.hhu.propra.database.dao;

import de.hhu.propra.database.entities.StudentEntity;
import org.springframework.data.repository.CrudRepository;

public interface StudentDao extends CrudRepository<StudentEntity,Long> {
}
