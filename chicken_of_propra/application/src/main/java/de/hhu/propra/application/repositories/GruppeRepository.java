package de.hhu.propra.application.repositories;

import de.hhu.propra.domain.aggregates.gruppe.Gruppe;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface GruppeRepository {

    Gruppe gruppeMitId(Long id);
    List<Gruppe> alleGruppen();
    void save(Gruppe gruppe);

}