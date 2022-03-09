package de.hhu.propra.application.repositories;

import de.hhu.propra.domain.aggregates.klausur.Klausur;

import java.util.List;

public interface KlausurRepository {

    Klausur klausurMitId(Long id);
    List<Klausur> alleKlausuren();
    void save(Klausur klausur);
    Klausur klausurMitLsf(long lsf);


}
