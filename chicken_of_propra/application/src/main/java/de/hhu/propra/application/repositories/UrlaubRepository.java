package de.hhu.propra.application.repositories;

import de.hhu.propra.domain.aggregates.student.Urlaub;

public interface UrlaubRepository {

    Urlaub urlaubMitId(Long id);
    void save(Urlaub urlaub);
}
