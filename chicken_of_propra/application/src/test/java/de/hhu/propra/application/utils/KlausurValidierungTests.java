package de.hhu.propra.application.utils;

import de.hhu.propra.application.dto.KlausurDto;
import de.hhu.propra.application.fehler.KlausurFehler;
import de.hhu.propra.domain.aggregates.klausur.Klausur;
import de.hhu.propra.domain.aggregates.student.Urlaub;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;


public class KlausurValidierungTests {

    KlausurValidierung klausurValidierung = new KlausurValidierung();

    @Test
    @DisplayName("Das Datum der Klausur liegt nicht im Praktikumszeitraum")
    void test1(){
        //arrange
        KlausurDto klausurDto = new KlausurDto("x" ,
                LocalDate.of(6000,10,10).toString(),
                LocalTime.of(10,30).toString(),
                LocalTime.of(11,0).toString(),
                123456,
                true);

        //act
        boolean ergebnis = klausurValidierung.datumLiegtInPraktikumszeit(klausurDto);

        //assert
        assertThat(ergebnis).isFalse();
    }

    @Test
    @DisplayName("Die Lsf ID hat die falsche Länge")
    void test2() throws IOException {
        //arrange
        KlausurDto klausurDto = new KlausurDto("x" ,
                LocalDate.of(2022,3,17).toString(),
                LocalTime.of(10,30).toString(),
                LocalTime.of(11,0).toString(),
                12345,
                true);

        //act
        boolean ergebnis = klausurValidierung.lsfIDPasst(klausurDto);

        //assert
        assertThat(ergebnis).isFalse();
    }

    @Test
    @DisplayName("Die Lsf ID hat die richtige Länge, aber den falschen Name")
    void test3() throws IOException {
        //arrange
        KlausurDto klausurDto = new KlausurDto("x" ,
                LocalDate.of(2022,3,17).toString(),
                LocalTime.of(10,30).toString(),
                LocalTime.of(11,0).toString(),
                222916,
                true);

        //act
        boolean ergebnis = klausurValidierung.lsfIDPasst(klausurDto);

        //assert
        assertThat(ergebnis).isFalse();
        assertThat(klausurValidierung
                .getFehlgeschlagen())
                .contains(new String("Der angegebene Veranstaltungsname ist ungültig. Der dazu bestehende Name ist Einführung in die Computerlinguistik".getBytes(), StandardCharsets.UTF_8));
    }

    @Test
    @DisplayName("Erstellte Klausur darf nicht am Wochenende liegen")
    void test7(){
        //arrange
        KlausurDto klausur = new KlausurDto("x" ,
                LocalDate.of(2022,3,19).toString(),
                LocalTime.of(10,30).toString(),
                LocalTime.of(11,0).toString(),
                222916,
                true);

        //act
        boolean ergebnis = klausurValidierung.amWochenende(klausur);
        Set<String> fehler = klausurValidierung.getFehlgeschlagen();

        //assert
        assertThat(ergebnis).isFalse();
        assertThat(fehler).contains(KlausurFehler.AM_WOCHENENDE);
        assertThat(fehler).hasSize(1);
    }

    @Test
    @DisplayName("Die Startzeit der Klausur muss vor der Endzeit der Klausur liegen")
    void test8(){
        //arrange
        KlausurDto klausur = new KlausurDto("x" ,
                LocalDate.of(2022,3,19).toString(),
                LocalTime.of(10,30).toString(),
                LocalTime.of(8,0).toString(),
                222916,
                true);

        //act
        boolean ergebnis = klausurValidierung.startzeitVorEndzeit(klausur);
        Set<String> fehler = klausurValidierung.getFehlgeschlagen();

        //assert
        assertThat(ergebnis).isFalse();
        assertThat(fehler).contains(KlausurFehler.STARTZEIT_VOR_ENDZEIT);
        assertThat(fehler).hasSize(1);
    }

    @Test
    @DisplayName("Die erstellte Klausur existiert bereits und wird somit nicht doppelt hinzugefügt")
    void test9(){
        //arrange
        List<Klausur> klausuren = Stream.of(KlausurDto.toKlausur(new KlausurDto("x" ,
                LocalDate.of(2022,3,19).toString(),
                LocalTime.of(8,30).toString(),
                LocalTime.of(10,0).toString(),
                222916,
                true))).collect(Collectors.toList());

        Klausur klausur = KlausurDto.toKlausur(new KlausurDto("x" ,
                LocalDate.of(2022,3,19).toString(),
                LocalTime.of(8,30).toString(),
                LocalTime.of(10,0).toString(),
                222916,
                true));

        //act
        boolean ergebnis = klausurValidierung.klausurLiegtInDb(klausuren, klausur);
        Set<String> fehler = klausurValidierung.getFehlgeschlagen();

        //assert
        assertThat(ergebnis).isTrue();
        assertThat(fehler).contains(KlausurFehler.KLAUSUR_LIEGT_In_DB);
        assertThat(fehler).hasSize(1);
    }

    @Test
    @DisplayName("Eine neue Klausur überschneidet sich mit einer aus der Liste der bereits bestehenden")
    void test10(){
        //arrange
        List<Klausur> klausuren = Stream.of(KlausurDto.toKlausur(new KlausurDto("x" ,
                LocalDate.of(2022,3,19).toString(),
                LocalTime.of(10,30).toString(),
                LocalTime.of(12,0).toString(),
                222916,
                true)),
                KlausurDto.toKlausur(new KlausurDto("y" ,
                        LocalDate.of(2022,3,19).toString(),
                        LocalTime.of(12,30).toString(),
                        LocalTime.of(13,30).toString(),
                        222916,
                        true)))
                .collect(Collectors.toList());

        Klausur klausur = KlausurDto.toKlausur(new KlausurDto("z" ,
                LocalDate.of(2022,3,19).toString(),
                LocalTime.of(8,30).toString(),
                LocalTime.of(11,0).toString(),
                222916,
                true));

        //act
        boolean ergebnis = klausurValidierung.keineKlausurUeberschneidung(klausuren, klausur);
        Set<String> fehler = klausurValidierung.getFehlgeschlagen();

        //assert
        assertThat(ergebnis).isFalse();
        assertThat(fehler).contains(KlausurFehler.NEUE_KLAUSUR_SCHNEIDET_ALTE);
        assertThat(fehler).hasSize(1);
    }

    @Test
    @DisplayName("Zwei Klausuren überschneiden sich")
    void test11(){
        //arrange
        Klausur klausur = KlausurDto.toKlausur(new KlausurDto("zx" ,
                LocalDate.of(2022,3,19).toString(),
                LocalTime.of(8,30).toString(),
                LocalTime.of(11,0).toString(),
                222916,
                true));

        Klausur zweiteKlausur = KlausurDto.toKlausur(new KlausurDto("z" ,
                LocalDate.of(2022,3,19).toString(),
                LocalTime.of(10,30).toString(),
                LocalTime.of(12,0).toString(),
                222916,
                true));

        //act
        boolean ergebnis = klausurValidierung.pruefeZweiUeberschneidungen(klausur, zweiteKlausur);

        //assert
        assertThat(ergebnis).isFalse();

    }


}
