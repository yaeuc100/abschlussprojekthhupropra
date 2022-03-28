package de.hhu.propra.application.repositories;

import de.hhu.propra.domain.aggregates.student.Student;
import de.hhu.propra.domain.aggregates.student.Urlaub;
import java.util.List;

public interface StudentRepository {

  Student studentMitId(Long id);

  Student studentMitHandle(String handle);

  List<Student> alleStudenten();

  void storniereUrlaub(Long studentId, Urlaub urlaub);

  void save(Student student);


}
