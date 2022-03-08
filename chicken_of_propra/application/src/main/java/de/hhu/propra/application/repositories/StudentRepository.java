package de.hhu.propra.application.repositories;

import de.hhu.propra.domain.aggregates.student.Anwesenheit;
import de.hhu.propra.domain.aggregates.student.Student;
import de.hhu.propra.domain.aggregates.student.Urlaub;

import java.util.List;

public interface StudentRepository {

    Student studentMitId(Long id);
   // List<Student> alleStudenten();
    List<Student> alleStudentenInGruppe(Long gruppeId);
    List<Urlaub> alleUrlaube(Long id);
    List<Anwesenheit> alleAnwesenheiten(Long id);
    void storniereUrlaub(Long studentId, Long urlaubId);
    void save(Student student);


}
