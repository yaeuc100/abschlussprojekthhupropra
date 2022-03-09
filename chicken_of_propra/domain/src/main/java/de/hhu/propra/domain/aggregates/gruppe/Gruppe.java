package de.hhu.propra.domain.aggregates.gruppe;

import de.hhu.propra.domain.aggregates.student.Student;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Gruppe {

    private Long id;
    private LocalTime startZeit;
    private LocalTime endeZeit;
    private Set<StudentReferenz> studentListe = new HashSet<>();

    public Gruppe(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<StudentReferenz> getStudentList() {
        return studentListe;
    }

    public void addStudent(Student student) {
        studentListe.add(new StudentReferenz(student.getId()));
    }

    public LocalTime getStartZeit() {
        return startZeit;
    }

    public void setStartZeit(LocalTime startZeit) {
        this.startZeit = startZeit;
    }

    public LocalTime getEndeZeit() {
        return endeZeit;
    }

    public void setEndeZeit(LocalTime endeZeit) {
        this.endeZeit = endeZeit;
    }

    public Set<StudentReferenz> getStudentListe() {
        return studentListe;
    }

    public void setStudentListe(Set<StudentReferenz> studentListe) {
        this.studentListe = studentListe;
    }
}
