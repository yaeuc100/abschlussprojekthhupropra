package de.hhu.propra.application.utils;

import de.hhu.propra.application.dto.UrlaubDto;
import de.hhu.propra.domain.aggregates.klausur.Klausur;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class UrlaubKlausurValidierungTests {

    UrlaubKlausurValidierung urlaubKlausurValidierung = new UrlaubKlausurValidierung();
    LocalDate datum = LocalDate.of(2022,2,22);



    @Test
    @DisplayName("Zeit der Onlineklausur wird zu Urlaub")
    void test1(){
        //arrange
        Klausur klausur = new Klausur(null,
                null,
                 LocalDateTime.of(3000,3,5,10,0),
                60,
                1L,
                true);
        UrlaubDto urlaubDto = new UrlaubDto(klausur.datum().toLocalDate(),
                LocalTime.of(9,30),
                LocalTime.of(11,0));

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
                LocalDateTime.of(3000,3,5,10,0),
                60,
                1L,
                false);
        UrlaubDto urlaubDto = new UrlaubDto(klausur.datum().toLocalDate(),
                LocalTime.of(8,0),
                LocalTime.of(13,0));

        //act
        UrlaubDto ergebnis = urlaubKlausurValidierung.freieZeitDurchKlausur(klausur);

        //assert
        assertThat(ergebnis).isEqualTo(urlaubDto);

    }

    @Test
    @DisplayName("Urlaub findet komplett innerhalb Klausur statt")
    void test3(){
        //arrange
        UrlaubDto urlaubDto = new UrlaubDto(datum,
                LocalTime.of(9,0),
                LocalTime.of(13,0));
        UrlaubDto freieZeitDurchKlausur = new UrlaubDto(datum,
                LocalTime.of(8,0),
                LocalTime.of(14,0));

        //act
        List<UrlaubDto> ergebnis = urlaubKlausurValidierung.reduziereUrlaubDurchKlausur(urlaubDto,freieZeitDurchKlausur);

        //assert
        assertThat(ergebnis).isEqualTo(Collections.emptyList());
    }

    @Test
    @DisplayName("Urlaub fängt zeitgleich mit Klausur an, hört aber früher auf")
    void test4(){
        //arrange
        UrlaubDto urlaubDto = new UrlaubDto(datum,
                LocalTime.of(9,0),
                LocalTime.of(13,0));
        UrlaubDto freieZeitDurchKlausur = new UrlaubDto(datum,
                LocalTime.of(9,0),
                LocalTime.of(14,0));

        //act
        List<UrlaubDto> ergebnis = urlaubKlausurValidierung.reduziereUrlaubDurchKlausur(urlaubDto,freieZeitDurchKlausur);

        //assert
        assertThat(ergebnis).isEqualTo(Collections.emptyList());
    }

    @Test
    @DisplayName("Urlaub startet nach der Klausur, endet aber zeitgleich")
    void test5(){
        //arrange
        UrlaubDto urlaubDto = new UrlaubDto(datum,
                LocalTime.of(9,0),
                LocalTime.of(13,0));
        UrlaubDto freieZeitDurchKlausur = new UrlaubDto(datum,
                LocalTime.of(8,0),
                LocalTime.of(13,0));

        //act
        List<UrlaubDto> ergebnis = urlaubKlausurValidierung.reduziereUrlaubDurchKlausur(urlaubDto,freieZeitDurchKlausur);

        //assert
        assertThat(ergebnis).isEqualTo(Collections.emptyList());
    }

    @Test
    @DisplayName("Gesamter Urlaub wird angerechnet, weil freie Zeit durch Klausur nach Urlaub liegt")
    void test6(){
        //arrange
        UrlaubDto urlaubDto = new UrlaubDto(datum,
                LocalTime.of(8,0),
                LocalTime.of(9,0));
        UrlaubDto freieZeitDurchKlausur = new UrlaubDto(datum,
                LocalTime.of(10,0),
                LocalTime.of(14,0));

        //act
        List<UrlaubDto> ergebnis = urlaubKlausurValidierung.reduziereUrlaubDurchKlausur(urlaubDto,freieZeitDurchKlausur);

        //assert
        assertThat(ergebnis).isEqualTo(List.of(urlaubDto));
    }

    @Test
    @DisplayName("Gesamter Urlaub wird angerechnet, weil freie Zeit durch Klausur vor Urlaub liegt")
    void test7(){
        //arrange
        UrlaubDto urlaubDto = new UrlaubDto(datum,
                LocalTime.of(10,0),
                LocalTime.of(11,0));
        UrlaubDto freieZeitDurchKlausur = new UrlaubDto(datum,
                LocalTime.of(7,0),
                LocalTime.of(9,0));

        //act
        List<UrlaubDto> ergebnis = urlaubKlausurValidierung.reduziereUrlaubDurchKlausur(urlaubDto,freieZeitDurchKlausur);

        //assert
        assertThat(ergebnis).isEqualTo(List.of(urlaubDto));
    }

    @Test
    @DisplayName("Das Ende des Urlaubs überschneidet sich mit der freien Zeit durch Klausur")
    void test8(){
        //arrange
        UrlaubDto urlaubDto = new UrlaubDto(datum,
                LocalTime.of(8,0),
                LocalTime.of(10,0));
        UrlaubDto freieZeitDurchKlausur = new UrlaubDto(datum,
                LocalTime.of(9,0),
                LocalTime.of(11,0));
        UrlaubDto reduzierterUrlaub = new UrlaubDto(datum,
                LocalTime.of(8,0),
                LocalTime.of(9,0));
        //act
        List<UrlaubDto> ergebnis = urlaubKlausurValidierung.reduziereUrlaubDurchKlausur(urlaubDto,freieZeitDurchKlausur);

        //assert
        assertThat(ergebnis).isEqualTo(List.of(reduzierterUrlaub));
    }

    @Test
    @DisplayName("Urlaub fängt früher an, endet aber zeitgleich")
    void test9(){
        //arrange
        UrlaubDto urlaubDto = new UrlaubDto(datum,
                LocalTime.of(8,0),
                LocalTime.of(10,0));
        UrlaubDto freieZeitDurchKlausur = new UrlaubDto(datum,
                LocalTime.of(9,0),
                LocalTime.of(10,0));
        UrlaubDto reduzierterUrlaub = new UrlaubDto(datum,
                LocalTime.of(8,0),
                LocalTime.of(9,0));
        //act
        List<UrlaubDto> ergebnis = urlaubKlausurValidierung.reduziereUrlaubDurchKlausur(urlaubDto,freieZeitDurchKlausur);

        //assert
        assertThat(ergebnis).isEqualTo(List.of(reduzierterUrlaub));
    }

    @Test
    @DisplayName("Der Anfang des Urlaubs überschneidet sich mit der freien Zeit durch Klausur")
    void test10(){
        //arrange
        UrlaubDto urlaubDto = new UrlaubDto(datum,
                LocalTime.of(11,0),
                LocalTime.of(12,0));
        UrlaubDto freieZeitDurchKlausur = new UrlaubDto(datum,
                LocalTime.of(9,0),
                LocalTime.of(11,30));
        UrlaubDto reduzierterUrlaub = new UrlaubDto(datum,
                LocalTime.of(11,30),
                LocalTime.of(12,0));
        //act
        List<UrlaubDto> ergebnis = urlaubKlausurValidierung.reduziereUrlaubDurchKlausur(urlaubDto,freieZeitDurchKlausur);

        //assert
        assertThat(ergebnis).isEqualTo(List.of(reduzierterUrlaub));
    }

    @Test
    @DisplayName("Urlaub startet zeitgleich, endet aber später")
    void test11(){
        //arrange
        UrlaubDto urlaubDto = new UrlaubDto(datum,
                LocalTime.of(11,0),
                LocalTime.of(12,0));
        UrlaubDto freieZeitDurchKlausur = new UrlaubDto(datum,
                LocalTime.of(11,0),
                LocalTime.of(11,30));
        UrlaubDto reduzierterUrlaub = new UrlaubDto(datum,
                LocalTime.of(11,30),
                LocalTime.of(12,0));
        //act
        List<UrlaubDto> ergebnis = urlaubKlausurValidierung.reduziereUrlaubDurchKlausur(urlaubDto,freieZeitDurchKlausur);

        //assert
        assertThat(ergebnis).isEqualTo(List.of(reduzierterUrlaub));
    }

    @Test
    @DisplayName("Urlaub umschließt Klausur")
    void test12(){
        //arrange
        UrlaubDto urlaubDto = new UrlaubDto(datum,
                LocalTime.of(8,0),
                LocalTime.of(13,0));
        UrlaubDto freieZeitDurchKlausur = new UrlaubDto(datum,
                LocalTime.of(9,0),
                LocalTime.of(12,30));
        UrlaubDto reduzierterUrlaubVorKlausur = new UrlaubDto(datum,
                LocalTime.of(8,0),
                LocalTime.of(9,0));
        UrlaubDto reduzierterUrlaubNachKlausur = new UrlaubDto(datum,
                LocalTime.of(12,30),
                LocalTime.of(13,0));
        //act
        List<UrlaubDto> ergebnis = urlaubKlausurValidierung.reduziereUrlaubDurchKlausur(urlaubDto,freieZeitDurchKlausur);

        //assert
        assertThat(ergebnis).isEqualTo(List.of(reduzierterUrlaubVorKlausur,reduzierterUrlaubNachKlausur));
    }

    @Test
    @DisplayName("Urlaub und Klausur sind exakt identisch")
    void test13(){
        //arrange
        UrlaubDto urlaubDto = new UrlaubDto(datum,
                LocalTime.of(9,0),
                LocalTime.of(13,0));
        UrlaubDto freieZeitDurchKlausur = new UrlaubDto(datum,
                LocalTime.of(9,0),
                LocalTime.of(13,0));

        //act
        List<UrlaubDto> ergebnis = urlaubKlausurValidierung.reduziereUrlaubDurchKlausur(urlaubDto,freieZeitDurchKlausur);

        //assert
        assertThat(ergebnis).isEqualTo(Collections.emptyList());
    }

    @Test
    @DisplayName("Urlaub endet sobald Klausur beginnt")
    void test14(){
        //arrange
        UrlaubDto urlaubDto = new UrlaubDto(datum,
                LocalTime.of(9,0),
                LocalTime.of(13,0));
        UrlaubDto freieZeitDurchKlausur = new UrlaubDto(datum,
                LocalTime.of(13,0),
                LocalTime.of(14,0));

        //act
        List<UrlaubDto> ergebnis = urlaubKlausurValidierung.reduziereUrlaubDurchKlausur(urlaubDto,freieZeitDurchKlausur);

        //assert
        assertThat(ergebnis).isEqualTo(List.of(urlaubDto));
    }

    @Test
    @DisplayName("Urlaub startet sobald Klausur endet")
    void test15(){
        //arrange
        UrlaubDto urlaubDto = new UrlaubDto(datum,
                LocalTime.of(13,0),
                LocalTime.of(14,0));
        UrlaubDto freieZeitDurchKlausur = new UrlaubDto(datum,
                LocalTime.of(9,0),
                LocalTime.of(13,0));

        //act
        List<UrlaubDto> ergebnis = urlaubKlausurValidierung.reduziereUrlaubDurchKlausur(urlaubDto,freieZeitDurchKlausur);

        //assert
        assertThat(ergebnis).isEqualTo(List.of(urlaubDto));
    }

    @Test
    @DisplayName("Freie Zeit durch Klausur wird richtig berechnet und von Urlaub an dem Tag abgezogen")
    void test16(){
        //arrange
        UrlaubDto urlaubDto = new UrlaubDto(datum,
                LocalTime.of(8,0),
                LocalTime.of(14,0));
        Klausur klausur = new Klausur(1L,
                "mündliche Prüfung Ana 2",
                LocalDateTime.of(2022,2,22,10,0),
                60,
                12345,
                true);
        UrlaubDto reduzierterUrlaub = new UrlaubDto(datum,
                LocalTime.of(8,0),
                LocalTime.of(9,30));
        UrlaubDto reduzierterUrlaub2 = new UrlaubDto(datum,
                LocalTime.of(11,0),
                LocalTime.of(14,00));

        //act
        List<UrlaubDto> ergebnis = urlaubKlausurValidierung.urlaubKlausurValidierung(urlaubDto,klausur);

        //assert
        assertThat(ergebnis).isEqualTo(List.of(reduzierterUrlaub, reduzierterUrlaub2));
    }

}
