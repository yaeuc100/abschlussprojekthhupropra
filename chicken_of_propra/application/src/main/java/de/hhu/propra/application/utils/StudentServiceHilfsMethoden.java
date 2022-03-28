package de.hhu.propra.application.utils;

import de.hhu.propra.domain.aggregates.klausur.Klausur;
import de.hhu.propra.domain.aggregates.student.Student;
import de.hhu.propra.domain.aggregates.student.Urlaub;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class StudentServiceHilfsMethoden {

  public List<Klausur> studentHatKlausurAnTag(List<Klausur> klausuren, LocalDate datum) {
    return klausuren.stream()
        .filter(k -> k.datum().toLocalDate().equals(datum))
        .toList();

  }

  public List<Urlaub> findeUrlaubeAmSelbenTag(Student student, LocalDate datum) {
    return student.getUrlaube().stream()
        .filter(u -> u.datum().equals(datum))
        .map(u -> new Urlaub(u.datum(), u.startzeit(), u.endzeit()))
        .toList();
  }

  public void storniereAlleUrlaubeAnTag(Student student, LocalDate datum) {
    for (Urlaub urlaub : findeUrlaubeAmSelbenTag(student, datum)) {
      student.urlaubStornieren(urlaub.datum(), urlaub.startzeit(), urlaub.endzeit());
    }
  }

  public List<Urlaub> stornierteUrlaube(List<Urlaub> alteUrlaube, List<Urlaub> neueUrlaube) {
    List<Urlaub> rueckgabe = new ArrayList<>();
    for (Urlaub urlaub : alteUrlaube) {
      if (!neueUrlaube.contains(urlaub)) {
        rueckgabe.add(urlaub);
      }
    }
    return rueckgabe;
  }

  public List<Urlaub> neueUrlaube(List<Urlaub> alteUrlaube, List<Urlaub> neueUrlaube) {
    List<Urlaub> rueckgabe = new ArrayList<>();
    for (Urlaub urlaub : neueUrlaube) {
      if (!alteUrlaube.contains(urlaub)) {
        rueckgabe.add(urlaub);
      }
    }
    return rueckgabe;
  }

}
