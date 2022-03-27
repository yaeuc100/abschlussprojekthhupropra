package de.hhu.propra.domain.aggregates.klausur;

import java.time.LocalDateTime;
import java.util.Objects;

public record Klausur(Long id, String name, LocalDateTime datum, int dauer, long lsf, boolean online) {


    public String formatiert() {
        return name + " ( " + datum.toLocalDate().toString() + ", " +
                datum.toLocalTime().toString() + " Uhr - " +
                datum.toLocalTime().plusMinutes(dauer).toString() + " Uhr )";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Klausur klausur = (Klausur) o;
        return dauer == klausur.dauer && lsf == klausur.lsf
                && online == klausur.online && name.equals(klausur.name)
                && datum.equals(klausur.datum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, datum, dauer, lsf, online);
    }
}
