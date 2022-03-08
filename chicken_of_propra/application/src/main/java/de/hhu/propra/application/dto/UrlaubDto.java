package de.hhu.propra.application.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record UrlaubDto(LocalDate datum, LocalTime startzeit, LocalTime endzeit) {
}
