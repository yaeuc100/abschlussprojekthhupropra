package dao;

import entities.StudentEntity;
import org.springframework.data.repository.CrudRepository;

public interface StudentDao extends CrudRepository<StudentEntity,Long> {
}
