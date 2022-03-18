package de.hhu.propra.application.utils;

import de.hhu.propra.application.dto.KlausurDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;


public class KlausurValidierungTests {


    @Test
    @DisplayName("Datum liegt nicht im Praktikumszeit")
    void test1(){
        //arrange
        KlausurValidierung klausurValidierung = new KlausurValidierung();
        KlausurDto klausurDto = new KlausurDto("x" ,
                LocalDateTime.of(2022,3,17,10,0).plusYears(5000),
                30,
                123456,
                true);

        //act
        boolean ergebnis = klausurValidierung.datumLiegtInPraktikumszeit(klausurDto);

        //assert
        assertThat(ergebnis).isFalse();
    }

    @Test
    @DisplayName("lsf id hat falsche Länge")
    void test2() throws IOException {
        //arrange
        KlausurValidierung klausurValidierung = new KlausurValidierung();
        KlausurDto klausurDto = new KlausurDto("x" ,
                LocalDateTime.of(2022,3,17,10,0),                30,
                12345,
                true);

        //act
        boolean ergebnis = klausurValidierung.lsfIDPasst(klausurDto);

        //assert
        assertThat(ergebnis).isFalse();
    }

    @Test
    @DisplayName("lsf id hat richtige Länge aber falsche Name")
    void test3() throws IOException {
        //arrange
        KlausurValidierung klausurValidierung = new KlausurValidierung();
        KlausurDto klausurDto = new KlausurDto("x" ,
                LocalDateTime.of(2022,3,17,10,0),                30,
                222916,
                true);

        //act
        boolean ergebnis = klausurValidierung.lsfIDPasst(klausurDto);

        //assert
        assertThat(ergebnis).isFalse();
        assertThat(klausurValidierung
                .getFehlgeschlagen())
                .contains(new String("Die angegebene Veranstaltungsname ist ungültig. Der dazu bestehende Name ist Einführung in die Computerlinguistik".getBytes(), StandardCharsets.UTF_8));
    }

}
