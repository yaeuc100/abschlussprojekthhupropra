package de.hhu.propra.application.utils;

import de.hhu.propra.application.repositories.KlausurRepository;
import de.hhu.propra.domain.aggregates.klausur.Klausur;
import de.hhu.propra.domain.aggregates.student.Student;
import de.hhu.propra.domain.aggregates.student.Urlaub;

import java.time.LocalDate;
import java.util.List;

public class StudentServiceHilfsMethoden {


    private KlausurRepository klausurRepository;

    public StudentServiceHilfsMethoden(KlausurRepository klausurRepository){
        this.klausurRepository = klausurRepository;
    }

    public List<Klausur> studentHatKlausurAnTag(List<Klausur> klausuren, LocalDate datum) {
        return klausuren.stream()
                .filter(k -> k.datum().toLocalDate().equals(datum))
                .toList();

    }



    public List<Urlaub> findeUrlaubeAmSelbenTag(Student student, LocalDate datum){
        return student.getUrlaube().stream()
                .filter(u -> u.datum().equals(datum))
                .map(u -> new Urlaub(u.datum(), u.startzeit(), u.endzeit()))
                .toList();
    }

    public void storniereAlleUrlaubeAnTag(Student student, LocalDate datum){
        for(Urlaub urlaub : findeUrlaubeAmSelbenTag(student, datum)){
            student.urlaubStornieren(urlaub.datum(), urlaub.startzeit(), urlaub.endzeit());
        }
    }
}
