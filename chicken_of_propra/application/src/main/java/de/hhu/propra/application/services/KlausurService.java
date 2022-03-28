package de.hhu.propra.application.services;

import de.hhu.propra.application.repositories.KlausurRepository;
import de.hhu.propra.application.stereotypes.ApplicationService;
import de.hhu.propra.domain.aggregates.klausur.Klausur;
import java.util.List;

@ApplicationService
public class KlausurService {

  private final KlausurRepository klausurRepository;

  public KlausurService(KlausurRepository klausurRepository) {
    this.klausurRepository = klausurRepository;
  }

  public List<Klausur> alleKlausuren() {
    return klausurRepository.alleKlausuren();
  }

  public Klausur klausurMitId(Long id) {
    return klausurRepository.klausurMitId(id);
  }

}
