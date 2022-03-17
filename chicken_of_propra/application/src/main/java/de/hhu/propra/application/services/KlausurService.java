package de.hhu.propra.application.services;

import de.hhu.propra.application.dto.KlausurDto;
import de.hhu.propra.application.repositories.KlausurRepository;

import java.util.List;

public class KlausurService {

    private final KlausurRepository klausurRepository;

    public KlausurService(KlausurRepository klausurRepository) {
        this.klausurRepository = klausurRepository;
    }

    public List<KlausurDto> alleKlausuren(){
        return klausurRepository.alleKlausuren()
                .stream()
                .map(k -> new KlausurDto(k.name(),
                        k.datum(),
                        k.dauer(),
                        k.lsf(),
                        k.online())).
                toList();
    }

}
