package de.hhu.propra.application.dto;

import de.hhu.propra.application.utils.UrlaubKlausurBearbeitung;
import de.hhu.propra.application.utils.UrlaubValidierung;
import de.hhu.propra.domain.aggregates.klausur.Klausur;
import de.hhu.propra.domain.aggregates.student.Urlaub;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record KlausurDto(@NotNull String name,
                         @NotBlank @DateTimeFormat(pattern = "dd.mm.yyyy") String datum,
                         @NotBlank @DateTimeFormat(pattern = "hh.mm") String startzeit,
                         @NotBlank @DateTimeFormat(pattern = "hh.mm") String endzeit,
                         long lsf,
                         boolean online) {

    public static Klausur toKlausur(KlausurDto dto) {
        LocalDateTime datumMitZeit = LocalDateTime.of(LocalDate.parse(dto.datum),
                LocalTime.parse(dto.startzeit));
        int dauer = (int) Duration.between(LocalTime.parse(dto.startzeit()),
                LocalTime.parse(dto.endzeit())).toMinutes();
        return new Klausur(null, dto.name, datumMitZeit, dauer, dto.lsf, dto.online());
    }

    public static KlausurDto toKlausurDto(Klausur klausur) {
        return new KlausurDto(klausur.name(),
                klausur.datum().toLocalDate().toString(),
                klausur.datum().toLocalTime().toString(),
                klausur.datum().toLocalTime().plusMinutes(klausur.dauer()).toString(),
                klausur.lsf(),
                klausur.online()
        );
    }

    public String formatiereDatum() {
        return datum + ", " + startzeit + " Uhr - " + endzeit + " Uhr";
    }

    public String formatiereFreistellung() {
        UrlaubKlausurBearbeitung urlaubKlausurBearbeitung = new UrlaubKlausurBearbeitung();
        KlausurDto klausurDto = new KlausurDto(name, datum, startzeit, endzeit, lsf, online);
        Klausur klausur = KlausurDto.toKlausur(klausurDto);
        Urlaub urlaub = urlaubKlausurBearbeitung.freieZeitDurchKlausur(klausur);

        return urlaub.startzeit() + " Uhr - " + urlaub.endzeit() + " Uhr";
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
