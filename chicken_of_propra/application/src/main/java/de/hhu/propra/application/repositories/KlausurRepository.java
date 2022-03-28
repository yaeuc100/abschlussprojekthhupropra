package de.hhu.propra.application.repositories;

import de.hhu.propra.application.dto.KlausurDto;
import de.hhu.propra.domain.aggregates.klausur.Klausur;
import java.util.List;

public interface KlausurRepository {

  Klausur klausurMitId(Long id);

  List<Klausur> alleKlausuren();

  Klausur klausurMitDaten(KlausurDto klausurDto);

  void save(Klausur klausur);
}
