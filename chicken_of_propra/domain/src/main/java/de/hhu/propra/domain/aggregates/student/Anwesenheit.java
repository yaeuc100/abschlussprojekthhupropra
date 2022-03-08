package de.hhu.propra.domain.aggregates.student;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

public record Anwesenheit(Long id, LocalDate datum, LocalTime startzeit, LocalTime endzeit) {

}
