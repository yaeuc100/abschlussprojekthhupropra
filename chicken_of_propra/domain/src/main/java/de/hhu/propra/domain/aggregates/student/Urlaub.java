package de.hhu.propra.domain.aggregates.student;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

public record Urlaub (LocalDate datum, LocalTime startzeit, LocalTime endzeit) {


    public long berechneZeitraum(){
        return Duration.between(startzeit,endzeit).toMinutes();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Urlaub urlaub = (Urlaub) o;
        return Objects.equals(datum, urlaub.datum) && Objects.equals(startzeit, urlaub.startzeit) && Objects.equals(endzeit, urlaub.endzeit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(datum, startzeit, endzeit);
    }
}
