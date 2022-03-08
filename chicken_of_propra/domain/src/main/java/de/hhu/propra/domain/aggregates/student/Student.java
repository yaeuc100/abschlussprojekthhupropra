package de.hhu.propra.domain.aggregates.student;

import de.hhu.propra.domain.stereotypes.AggregateRoot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@AggregateRoot
public class Student {

    private Long id;
    private String handle;
    private int resturlaub;
    private Set<UrlaubReferenz> urlaubsListe = new HashSet<>();
    private Set<AnwesenheitReferenz> anwesenheitsListe = new HashSet<>();

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

    public void addUrlaub(Urlaub urlaub) {
        urlaubsListe.add(new UrlaubReferenz(urlaub.id()));
    }

    public Set<AnwesenheitReferenz> getAnwesenheitsListe() {
        return anwesenheitsListe;
    }

    public void addAnwesenheit(Anwesenheit anwesenheit) {
        anwesenheitsListe.add(new AnwesenheitReferenz(anwesenheit.id()));
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

    public Set<UrlaubReferenz> getUrlaubsListe() {
        return urlaubsListe;
    }
}
