package de.hhu.propra.domain.aggregates.student;

import de.hhu.propra.domain.aggregates.klausur.Klausur;
import de.hhu.propra.domain.stereotypes.AggregateRoot;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@AggregateRoot
public class Student {

    private Long id;
    private String handle;
    private int resturlaub;
    private Set<Urlaub> urlaube = new HashSet<>();
    private Set<Anwesenheit> anwesenheiten = new HashSet<>();
    private Set<KlausurReferenz> klausuren = new HashSet<>();

    public Student(Long id, String handle) {
        this.id = id;
        this.handle = handle;
        this.resturlaub = 240;
    }


    public void addUrlaub(LocalDate datum, LocalTime start, LocalTime ende) {
        urlaube.add(new Urlaub(datum, start, ende));
    }

    public void berechneResturlaub(){
        resturlaub = 240 - urlaube.stream()
                .mapToInt(u -> (int) Duration.between(u.startzeit(),u.endzeit()).toMinutes())
                .sum();
    }

    public boolean urlaubStornieren(LocalDate datum, LocalTime start, LocalTime ende) {
        if (urlaubExistiertSchon(datum, start, ende)) {
            urlaube.removeIf(u -> u.datum().equals(datum)
                    && u.startzeit().equals(start)
                    && u.endzeit().equals(ende));
            return true;
        }
        return false;
    }


    public Long getId() {
        return id;
    }

    public String getHandle() {
        return handle;
    }

    public int getResturlaub() {
        return resturlaub;
    }

    public List<Urlaub> getUrlaube() {
        return new ArrayList<>(urlaube);
    }

    public List<Long> getKlausuren() {
        return klausuren.stream()
                .map(KlausurReferenz::id)
                .collect(Collectors.toList());
    }

    public void addKlausur(Klausur klausur) {
        klausuren.add(new KlausurReferenz(klausur.id()));
    }

    public void addKlausurRef(KlausurReferenz referenz) {
        klausuren.add(referenz);
    }

    public boolean urlaubExistiertSchon(LocalDate datum, LocalTime start, LocalTime ende) {
        Urlaub urlaub = new Urlaub(datum, start, ende);
        return urlaube.contains(urlaub);
    }

    public void klausurStornieren(Klausur klausur) {
        klausuren.removeIf(k -> k.id() == klausur.id());
    }

    public void setResturlaub(int resturlaub) {
        this.resturlaub = resturlaub;
    }

    public int berechneUrlaubszeit(){
        return 240 - resturlaub;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return id.equals(student.id) && handle.equals(student.handle);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, handle);
    }
}
