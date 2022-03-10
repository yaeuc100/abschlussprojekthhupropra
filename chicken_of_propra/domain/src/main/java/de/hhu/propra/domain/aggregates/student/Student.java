package de.hhu.propra.domain.aggregates.student;

import de.hhu.propra.domain.aggregates.klausur.Klausur;
import de.hhu.propra.domain.stereotypes.AggregateRoot;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@AggregateRoot
public class Student {

    private Long id;
    private String handle;
    private int resturlaub;
    private Set<Urlaub> urlaube = new HashSet<>();
    private Set<Anwesenheit> anwesenheiten = new HashSet<>();
    private Set<KlausurReferenz> klausuren = new HashSet<>();

    public Student(Long id, String handle, int resturlaub) {
        this.id = id;
        this.handle = handle;
        this.resturlaub = 240;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public void setResturlaub(int resturlaub) {
        this.resturlaub = resturlaub;
    }

    public void addUrlaub(LocalDate datum , LocalTime start , LocalTime ende) {
        urlaube.add(new Urlaub(datum,start,ende));
    }

    public void urlaubStornieren(LocalDate datum , LocalTime start , LocalTime ende){
        urlaube.removeIf(u -> u.datum().equals(datum)
                && u.startzeit().equals(start)
                && u.endzeit().equals(ende));
    }

    public Set<Anwesenheit> getAnwesenheiten() {
        return anwesenheiten;
    }

    public void addAnwesenheit(LocalDate datum , LocalTime start , LocalTime ende) {
        anwesenheiten.add(new Anwesenheit(datum,start,ende));
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
        return urlaube.stream().collect(Collectors.toList());
    }



    public void addKlausur (Klausur klausur){
        klausuren.add(new KlausurReferenz(klausur.id()));
    }

    public boolean urlaubExistiert(LocalDate datum , LocalTime start , LocalTime ende){
        Urlaub urlaub = new Urlaub(datum,start,ende);
        return urlaube.contains(urlaub);
    }
}
