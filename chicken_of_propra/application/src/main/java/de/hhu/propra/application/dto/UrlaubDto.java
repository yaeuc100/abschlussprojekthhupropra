package de.hhu.propra.application.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

public record UrlaubDto(LocalDate datum, LocalTime startzeit, LocalTime endzeit) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UrlaubDto urlaubDto = (UrlaubDto) o;
        return datum.equals(urlaubDto.datum) && startzeit.equals(urlaubDto.startzeit) && endzeit.equals(urlaubDto.endzeit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(datum, startzeit, endzeit);
    }
}
