package de.hhu.propra.database.dao;

import de.hhu.propra.database.entities.StudentEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Component
public interface StudentDao extends CrudRepository<StudentEntity,Long> {
}
