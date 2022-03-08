package de.hhu.propra.application.repositories;

import de.hhu.propra.domain.aggregates.student.Anwesenheit;

public interface AnwesenheitRepository {

    Anwesenheit anwesenheitMitId(Long id);
    void save(AnwesenheitRepository anwesenheit);
}
