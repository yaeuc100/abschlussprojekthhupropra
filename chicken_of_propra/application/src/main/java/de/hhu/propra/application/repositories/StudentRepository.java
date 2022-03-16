package de.hhu.propra.application.repositories;

import de.hhu.propra.domain.aggregates.klausur.Klausur;
import de.hhu.propra.domain.aggregates.student.Student;
import de.hhu.propra.domain.aggregates.student.Urlaub;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface StudentRepository {

    // TODO: viellicht studentMitId löschen
    Student studentMitId(Long id);
    Student studentMitHandle(String handle);
    List<Student> alleStudenten();
    void storniereUrlaub(Long studentId, Urlaub urlaub);
    void save(Student student);


}
