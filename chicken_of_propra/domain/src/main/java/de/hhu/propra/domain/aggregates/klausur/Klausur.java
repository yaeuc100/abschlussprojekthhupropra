package de.hhu.propra.domain.aggregates.klausur;

import java.time.LocalDateTime;

public record Klausur(Long id, String name, LocalDateTime datum, int dauer, long lsf) {

}
