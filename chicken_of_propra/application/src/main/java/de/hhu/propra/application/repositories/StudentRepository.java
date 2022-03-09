package de.hhu.propra.application.repositories;

import de.hhu.propra.domain.aggregates.klausur.Klausur;
import de.hhu.propra.domain.aggregates.student.Student;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface StudentRepository {

    Student studentMitId(Long id);
    List<Student> alleStudenten();
    List<Student> alleStudentenInGruppe(Long gruppeId);
    void storniereUrlaub(Long studentId, Long urlaubId);
    void save(Student student);


}
