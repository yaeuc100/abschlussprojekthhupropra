package de.hhu.propra.domain.aggregates.student;

import java.time.LocalDate;
import java.time.LocalTime;

public record Urlaub(Long id, LocalDate datum, LocalTime startzeit, LocalTime endzeit) {

}
