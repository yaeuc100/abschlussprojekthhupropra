package de.hhu.propra.application.dto;

import de.hhu.propra.domain.aggregates.klausur.Klausur;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record KlausurDto (String name , String datum , String startzeit, String endzeit, long lsf, boolean online){

    public static Klausur toKlausur(KlausurDto dto){
        LocalDateTime datumMitZeit = LocalDateTime.of(LocalDate.parse(dto.datum),
                LocalTime.parse(dto.startzeit));
        int dauer = (int) Duration.between(LocalTime.parse(dto.startzeit()),
                LocalTime.parse(dto.endzeit())).toMinutes();
        return new Klausur(null, dto.name, datumMitZeit, dauer ,dto.lsf , dto.online());
    }

    public static KlausurDto toKlausurDto(Klausur klausur){
        return new KlausurDto( klausur.name(),
                klausur.datum().toLocalDate().toString(),
                klausur.datum().toLocalTime().toString(),
                klausur.datum().toLocalTime().plusMinutes(klausur.dauer()).toString(),
                klausur.lsf(),
                klausur.online()
                );
    }

    @Override
    public String toString() {
        return name +
                " ( " + datum +
                " , " + startzeit +
                " , " + endzeit +
                " , " + online +
                " )";
    }
}
