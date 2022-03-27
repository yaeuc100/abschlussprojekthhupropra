package de.hhu.propra.application.dtos;

import de.hhu.propra.application.dto.KlausurDto;
import de.hhu.propra.domain.aggregates.klausur.Klausur;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

public class KlausurDtoTests {

    @Test
    @DisplayName("Das KlausurDto wird richtig zu einer Klausur übersetzt")
    void test1() {
        //arrange
        KlausurDto klausurDto = new KlausurDto("x",
                LocalDate.of(6000, 10, 10).toString(),
                LocalTime.of(10, 30).toString(),
                LocalTime.of(11, 0).toString(),
                123456,
                true);
        Klausur klausur = new Klausur(1L, "x",
                LocalDateTime.of(6000, 10, 10, 10, 30),
                30,
                123456,
                true);

        //act
        Klausur ergebnis = KlausurDto.toKlausur(klausurDto);

        //assert
        assertThat(ergebnis).isEqualTo(klausur);
    }

    @Test
    @DisplayName("Die Klausur wird richtig zu einem KlausurDto übersetzt")
    void test2() {
        //arrange
        Klausur klausur = new Klausur(1L, "x",
                LocalDateTime.of(6000, 10, 10, 10, 30),
                30,
                123456,
                true);
        KlausurDto klausurDto = new KlausurDto("x",
                LocalDate.of(6000, 10, 10).toString(),
                LocalTime.of(10, 30).toString(),
                LocalTime.of(11, 0).toString(),
                123456,
                true);


        //act
        KlausurDto ergebnis = KlausurDto.toKlausurDto(klausur);

        //assert
        assertThat(ergebnis).isEqualTo(klausurDto);
    }

    @Test
    @DisplayName("Das Datum eines KlausurDtos wird richtig formatiert")
    void test3() {
        //arrange
        KlausurDto klausurDto = new KlausurDto("x",
                LocalDate.of(6000, 10, 10).toString(),
                LocalTime.of(10, 30).toString(),
                LocalTime.of(11, 0).toString(),
                123456,
                true);


        //act
        String ergebnis = klausurDto.formatiereDatum();

        //assert
        assertThat(ergebnis).isEqualTo("6000-10-10, 10:30 Uhr - 11:00 Uhr");
    }

    @Test
    @DisplayName("Die Freistellung eines KlausurDtos wird richtig formatiert")
    void test4() {
        //arrange
        KlausurDto klausurDto = new KlausurDto("x",
                LocalDate.of(6000, 10, 10).toString(),
                LocalTime.of(10, 30).toString(),
                LocalTime.of(11, 0).toString(),
                123456,
                true);


        //act
        String ergebnis = klausurDto.formatiereDatum();

        //assert
        assertThat(ergebnis).isEqualTo("6000-10-10, 10:30 Uhr - 11:00 Uhr");
    }

    @Test
    @DisplayName("Die toString Methode formatiert ein KlausurDto richtig")
    void test5() {
        //arrange
        KlausurDto klausurDto = new KlausurDto("x",
                LocalDate.of(6000, 10, 10).toString(),
                LocalTime.of(10, 30).toString(),
                LocalTime.of(11, 0).toString(),
                123456,
                true);


        //act
        String ergebnis = klausurDto.toString();

        //assert
        assertThat(ergebnis).isEqualTo("x ( 6000-10-10 , 10:30 , 11:00 , true )");
    }

}
