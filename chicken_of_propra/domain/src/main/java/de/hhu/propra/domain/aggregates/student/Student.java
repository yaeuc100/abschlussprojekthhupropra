package de.hhu.propra.domain.aggregates.student;

import de.hhu.propra.domain.aggregates.klausur.Klausur;
import de.hhu.propra.domain.stereotypes.AggregateRoot;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@AggregateRoot
public class Student {

    private Long id;
    private String handle;
    private int resturlaub;
    private Set<Urlaub> urlaubsListe = new HashSet<>();
    private Set<Anwesenheit> anwesenheitsListe = new HashSet<>();
    private Set<KlausurReferenz> klausurenListe = new HashSet<>();

    public Student(Long id, String handle, int resturlaub) {
        this.id = id;
        this.handle = handle;
        this.resturlaub = resturlaub;
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
        urlaubsListe.add(new Urlaub(datum,start,ende));
    }

    public void urlaubStornieren(LocalDate datum , LocalTime start , LocalTime ende){
        urlaubsListe.removeIf( u -> u.datum().equals(datum)
                && u.startzeit().equals(start)
                && u.endzeit().equals(ende));
    }

    public Set<Anwesenheit> getAnwesenheitsListe() {
        return anwesenheitsListe;
    }

    public void addAnwesenheit(LocalDate datum , LocalTime start , LocalTime ende) {
        anwesenheitsListe.add(new Anwesenheit(datum,start,ende));
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

    public Set<Urlaub> getUrlaubsListe() {
        return urlaubsListe;
    }

    public void addKlausur (Klausur klausur){
        klausurenListe.add(new KlausurReferenz(klausur.id()));
    }

    public boolean urlaubExistiert(LocalDate datum , LocalTime start , LocalTime ende){
        Urlaub urlaub = new Urlaub(datum,start,ende);
        return urlaubsListe.contains(urlaub);
    }
}
