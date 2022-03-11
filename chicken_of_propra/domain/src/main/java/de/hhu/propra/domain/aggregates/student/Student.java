package de.hhu.propra.domain.aggregates.student;

import de.hhu.propra.domain.aggregates.klausur.Klausur;
import de.hhu.propra.domain.stereotypes.AggregateRoot;

import java.time.Duration;
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

    public Student(Long id, String handle) {
        this.id = id;
        this.handle = handle;
        this.resturlaub = 240;
    }


    public void addUrlaub(LocalDate datum , LocalTime start , LocalTime ende) {
        resturlaub -= Duration.between(start, ende).toMinutes();
        urlaube.add(new Urlaub(datum,start,ende));
    }

    public boolean urlaubStornieren(LocalDate datum , LocalTime start , LocalTime ende){
        if(urlaubExistiert(datum, start, ende)) {
            resturlaub += Duration.between(start, ende).toMinutes();
            urlaube.removeIf(u -> u.datum().equals(datum)
                    && u.startzeit().equals(start)
                    && u.endzeit().equals(ende));
            return true;
        }
        return false;
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

    public List<Long> getKlausuren(){
        return klausuren.stream()
                .map(k -> k.id())
                .collect(Collectors.toList());
    }

    public void addKlausur (Klausur klausur){
        klausuren.add(new KlausurReferenz(klausur.id()));
    }

    public boolean urlaubExistiert(LocalDate datum , LocalTime start , LocalTime ende){
        Urlaub urlaub = new Urlaub(datum,start,ende);
        return urlaube.contains(urlaub);
    }
}
