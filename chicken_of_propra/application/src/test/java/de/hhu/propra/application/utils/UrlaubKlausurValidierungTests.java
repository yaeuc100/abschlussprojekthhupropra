package de.hhu.propra.application.utils;

import de.hhu.propra.application.dto.UrlaubDto;
import de.hhu.propra.domain.aggregates.klausur.Klausur;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

public class UrlaubKlausurValidierungTests {

    UrlaubKlausurValidierung urlaubKlausurValidierung = new UrlaubKlausurValidierung();



    @Test
    @DisplayName("Zeit der Klausur wird zu Urlaub(Online)")
    void test1(){
        //arrange
        Klausur klausur = new Klausur(null,
                null,
                 LocalDateTime.of(3000,3,5,10,00),
                60,
                1L,
                true);
        UrlaubDto urlaubDto = new UrlaubDto(klausur.datum().toLocalDate(),
                LocalTime.of(9,30),
                LocalTime.of(11,00));

        //act
        UrlaubDto ergebnis = urlaubKlausurValidierung.freieZeitDurchKlausur(klausur);

        //assert
        assertThat(ergebnis).isEqualTo(urlaubDto);

    }
    @Test
    @DisplayName("Zeit der Klausur wird zu Urlaub(Offline)")
    void test2(){
        //arrange
        Klausur klausur = new Klausur(null,
                null,
                LocalDateTime.of(3000,3,5,10,00),
                60,
                1L,
                false);
        UrlaubDto urlaubDto = new UrlaubDto(klausur.datum().toLocalDate(),
                LocalTime.of(8,00),
                LocalTime.of(13,00));

        //act
        UrlaubDto ergebnis = urlaubKlausurValidierung.freieZeitDurchKlausur(klausur);

        //assert
        assertThat(ergebnis).isEqualTo(urlaubDto);

    }

}
