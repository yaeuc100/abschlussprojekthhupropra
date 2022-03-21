package de.hhu.propra.application.dto;

import de.hhu.propra.domain.aggregates.student.Urlaub;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

public record UrlaubDto(String datum, String startzeit, String endzeit) {

    public static Urlaub toUrlaub(UrlaubDto dto){
        System.out.println(dto.datum);
        System.out.println(dto.startzeit);
        return new Urlaub(LocalDate.parse(dto.datum),
                LocalTime.parse(dto.startzeit()),
                LocalTime.parse(dto.endzeit()));
    }
    @Override
    public String toString() {
        return "UrlaubDto{" +
                "datum=" + datum +
                ", startzeit=" + startzeit +
                ", endzeit=" + endzeit +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UrlaubDto urlaub = (UrlaubDto) o;
        return datum.equals(urlaub.datum()) && startzeit.equals(urlaub.startzeit) && endzeit.equals(urlaub.endzeit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(datum, startzeit, endzeit);
    }
}
