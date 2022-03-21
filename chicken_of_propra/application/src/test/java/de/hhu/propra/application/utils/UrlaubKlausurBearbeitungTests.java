package de.hhu.propra.application.utils;

import de.hhu.propra.domain.aggregates.klausur.Klausur;
import de.hhu.propra.domain.aggregates.student.Urlaub;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class UrlaubKlausurBearbeitungTests {

    UrlaubKlausurBearbeitung urlaubKlausurBearbeitung = new UrlaubKlausurBearbeitung();
    UrlaubValidierung urlaubValidierung = new UrlaubValidierung();
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
        Urlaub urlaub = new Urlaub(klausur.datum().toLocalDate(),
                LocalTime.of(9,30),
                LocalTime.of(11,0));

        //act
        Urlaub ergebnis = urlaubKlausurBearbeitung.freieZeitDurchKlausur(klausur);

        //assert
        assertThat(ergebnis).isEqualTo(urlaub);

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
        Urlaub urlaub = new Urlaub(klausur.datum().toLocalDate(),
                LocalTime.of(8,0),
                LocalTime.of(13,0));

        //act
        Urlaub ergebnis = urlaubKlausurBearbeitung.freieZeitDurchKlausur(klausur);

        //assert
        assertThat(ergebnis).isEqualTo(urlaub);

    }

    @Test
    @DisplayName("Urlaub findet komplett innerhalb Klausur statt")
    void test3(){
        //arrange
        Urlaub urlaub = new Urlaub(datum,
                LocalTime.of(9,0),
                LocalTime.of(13,0));
        Urlaub freieZeitDurchKlausur = new Urlaub(datum,
                LocalTime.of(8,0),
                LocalTime.of(14,0));

        //act
        List<Urlaub> ergebnis = urlaubKlausurBearbeitung.reduziereUrlaubDurchKlausur(urlaub,freieZeitDurchKlausur);

        //assert
        assertThat(ergebnis).isEqualTo(Collections.emptyList());
    }

    @Test
    @DisplayName("Urlaub fängt zeitgleich mit Klausur an, hört aber früher auf")
    void test4(){
        //arrange
        Urlaub urlaub = new Urlaub(datum,
                LocalTime.of(9,0),
                LocalTime.of(13,0));
        Urlaub freieZeitDurchKlausur = new Urlaub(datum,
                LocalTime.of(9,0),
                LocalTime.of(14,0));

        //act
        List<Urlaub> ergebnis = urlaubKlausurBearbeitung.reduziereUrlaubDurchKlausur(urlaub,freieZeitDurchKlausur);

        //assert
        assertThat(ergebnis).isEqualTo(Collections.emptyList());
    }

    @Test
    @DisplayName("Urlaub startet nach der Klausur, endet aber zeitgleich")
    void test5(){
        //arrange
        Urlaub urlaub = new Urlaub(datum,
                LocalTime.of(9,0),
                LocalTime.of(13,0));
        Urlaub freieZeitDurchKlausur = new Urlaub(datum,
                LocalTime.of(8,0),
                LocalTime.of(13,0));

        //act
        List<Urlaub> ergebnis = urlaubKlausurBearbeitung.reduziereUrlaubDurchKlausur(urlaub,freieZeitDurchKlausur);

        //assert
        assertThat(ergebnis).isEqualTo(Collections.emptyList());
    }

    @Test
    @DisplayName("Gesamter Urlaub wird angerechnet, weil freie Zeit durch Klausur nach Urlaub liegt")
    void test6(){
        //arrange
        Urlaub urlaub = new Urlaub(datum,
                LocalTime.of(8,0),
                LocalTime.of(9,0));
        Urlaub freieZeitDurchKlausur = new Urlaub(datum,
                LocalTime.of(10,0),
                LocalTime.of(14,0));

        //act
        List<Urlaub> ergebnis = urlaubKlausurBearbeitung.reduziereUrlaubDurchKlausur(urlaub,freieZeitDurchKlausur);

        //assert
        assertThat(ergebnis).isEqualTo(List.of(urlaub));
    }

    @Test
    @DisplayName("Gesamter Urlaub wird angerechnet, weil freie Zeit durch Klausur vor Urlaub liegt")
    void test7(){
        //arrange
        Urlaub urlaub = new Urlaub(datum,
                LocalTime.of(10,0),
                LocalTime.of(11,0));
        Urlaub freieZeitDurchKlausur = new Urlaub(datum,
                LocalTime.of(7,0),
                LocalTime.of(9,0));

        //act
        List<Urlaub> ergebnis = urlaubKlausurBearbeitung.reduziereUrlaubDurchKlausur(urlaub,freieZeitDurchKlausur);

        //assert
        assertThat(ergebnis).isEqualTo(List.of(urlaub));
    }

    @Test
    @DisplayName("Das Ende des Urlaubs überschneidet sich mit der freien Zeit durch Klausur")
    void test8(){
        //arrange
        Urlaub urlaub = new Urlaub(datum,
                LocalTime.of(8,0),
                LocalTime.of(10,0));
        Urlaub freieZeitDurchKlausur = new Urlaub(datum,
                LocalTime.of(9,0),
                LocalTime.of(11,0));
        Urlaub reduzierterUrlaub = new Urlaub(datum,
                LocalTime.of(8,0),
                LocalTime.of(9,0));
        //act
        List<Urlaub> ergebnis = urlaubKlausurBearbeitung.reduziereUrlaubDurchKlausur(urlaub,freieZeitDurchKlausur);

        //assert
        assertThat(ergebnis).isEqualTo(List.of(reduzierterUrlaub));
    }

    @Test
    @DisplayName("Urlaub fängt früher an, endet aber zeitgleich")
    void test9(){
        //arrange
        Urlaub urlaub = new Urlaub(datum,
                LocalTime.of(8,0),
                LocalTime.of(10,0));
        Urlaub freieZeitDurchKlausur = new Urlaub(datum,
                LocalTime.of(9,0),
                LocalTime.of(10,0));
        Urlaub reduzierterUrlaub = new Urlaub(datum,
                LocalTime.of(8,0),
                LocalTime.of(9,0));
        //act
        List<Urlaub> ergebnis = urlaubKlausurBearbeitung.reduziereUrlaubDurchKlausur(urlaub,freieZeitDurchKlausur);

        //assert
        assertThat(ergebnis).isEqualTo(List.of(reduzierterUrlaub));
    }

    @Test
    @DisplayName("Der Anfang des Urlaubs überschneidet sich mit der freien Zeit durch Klausur")
    void test10(){
        //arrange
        Urlaub urlaub = new Urlaub(datum,
                LocalTime.of(11,0),
                LocalTime.of(12,0));
        Urlaub freieZeitDurchKlausur = new Urlaub(datum,
                LocalTime.of(9,0),
                LocalTime.of(11,30));
        Urlaub reduzierterUrlaub = new Urlaub(datum,
                LocalTime.of(11,30),
                LocalTime.of(12,0));
        //act
        List<Urlaub> ergebnis = urlaubKlausurBearbeitung.reduziereUrlaubDurchKlausur(urlaub,freieZeitDurchKlausur);

        //assert
        assertThat(ergebnis).isEqualTo(List.of(reduzierterUrlaub));
    }

    @Test
    @DisplayName("Urlaub startet zeitgleich, endet aber später")
    void test11(){
        //arrange
        Urlaub urlaub = new Urlaub(datum,
                LocalTime.of(11,0),
                LocalTime.of(12,0));
        Urlaub freieZeitDurchKlausur = new Urlaub(datum,
                LocalTime.of(11,0),
                LocalTime.of(11,30));
        Urlaub reduzierterUrlaub = new Urlaub(datum,
                LocalTime.of(11,30),
                LocalTime.of(12,0));
        //act
        List<Urlaub> ergebnis = urlaubKlausurBearbeitung.reduziereUrlaubDurchKlausur(urlaub,freieZeitDurchKlausur);

        //assert
        assertThat(ergebnis).isEqualTo(List.of(reduzierterUrlaub));
    }

    @Test
    @DisplayName("Urlaub umschließt Klausur")
    void test12(){
        //arrange
        Urlaub urlaub = new Urlaub(datum,
                LocalTime.of(8,0),
                LocalTime.of(13,0));
        Urlaub freieZeitDurchKlausur = new Urlaub(datum,
                LocalTime.of(9,0),
                LocalTime.of(12,30));
        Urlaub reduzierterUrlaubVorKlausur = new Urlaub(datum,
                LocalTime.of(8,0),
                LocalTime.of(9,0));
        Urlaub reduzierterUrlaubNachKlausur = new Urlaub(datum,
                LocalTime.of(12,30),
                LocalTime.of(13,0));
        //act
        List<Urlaub> ergebnis = urlaubKlausurBearbeitung.reduziereUrlaubDurchKlausur(urlaub,freieZeitDurchKlausur);

        //assert
        assertThat(ergebnis).isEqualTo(List.of(reduzierterUrlaubVorKlausur,reduzierterUrlaubNachKlausur));
    }

    @Test
    @DisplayName("Urlaub und Klausur sind exakt identisch")
    void test13(){
        //arrange
        Urlaub urlaub = new Urlaub(datum,
                LocalTime.of(9,0),
                LocalTime.of(13,0));
        Urlaub freieZeitDurchKlausur = new Urlaub(datum,
                LocalTime.of(9,0),
                LocalTime.of(13,0));

        //act
        List<Urlaub> ergebnis = urlaubKlausurBearbeitung.reduziereUrlaubDurchKlausur(urlaub,freieZeitDurchKlausur);

        //assert
        assertThat(ergebnis).isEqualTo(Collections.emptyList());
    }

    @Test
    @DisplayName("Urlaub endet sobald Klausur beginnt")
    void test14(){
        //arrange
        Urlaub urlaub = new Urlaub(datum,
                LocalTime.of(9,0),
                LocalTime.of(13,0));
        Urlaub freieZeitDurchKlausur = new Urlaub(datum,
                LocalTime.of(13,0),
                LocalTime.of(14,0));

        //act
        List<Urlaub> ergebnis = urlaubKlausurBearbeitung.reduziereUrlaubDurchKlausur(urlaub,freieZeitDurchKlausur);

        //assert
        assertThat(ergebnis).isEqualTo(List.of(urlaub));
    }

    @Test
    @DisplayName("Urlaub startet sobald Klausur endet")
    void test15(){
        //arrange
        Urlaub urlaub = new Urlaub(datum,
                LocalTime.of(13,0),
                LocalTime.of(14,0));
        Urlaub freieZeitDurchKlausur = new Urlaub(datum,
                LocalTime.of(9,0),
                LocalTime.of(13,0));

        //act
        List<Urlaub> ergebnis = urlaubKlausurBearbeitung.reduziereUrlaubDurchKlausur(urlaub,freieZeitDurchKlausur);

        //assert
        assertThat(ergebnis).isEqualTo(List.of(urlaub));
    }

    @Test
    @DisplayName("Freie Zeit durch Klausur wird richtig berechnet und von Urlaub an dem Tag abgezogen")
    void test16(){
        //arrange
        Urlaub urlaub = new Urlaub(datum,
                LocalTime.of(8,0),
                LocalTime.of(14,0));
        Klausur klausur = new Klausur(1L,
                "mündliche Prüfung Ana 2",
                LocalDateTime.of(2022,2,22,10,0),
                60,
                12345,
                true);
        Urlaub reduzierterUrlaub = new Urlaub(datum,
                LocalTime.of(8,0),
                LocalTime.of(9,30));
        Urlaub reduzierterUrlaub2 = new Urlaub(datum,
                LocalTime.of(11,0),
                LocalTime.of(14,00));

        //act
        List<Urlaub> ergebnis = urlaubKlausurBearbeitung.urlaubKlausurValidierung(urlaub,List.of(klausur));

        //assert
        assertThat(ergebnis).isEqualTo(List.of(reduzierterUrlaub, reduzierterUrlaub2));
    }

    @Test
    @DisplayName("An einem Tag gibt es zwei Klausuren")
    void test17(){
        //arrange
        Urlaub beantragterUrlaub = new Urlaub(datum,
                LocalTime.of(8,0),
                LocalTime.of(14,30));
        Klausur klausur = new Klausur(1L,
                "mündliche Prüfung Ana 2",
                LocalDateTime.of(2022,2,22,10,0),
                30,
                12345,
                true);   //liefert 9:30 - 10:30
        Klausur zweiteKlausur = new Klausur(1L,
                "mündliche Prüfung Ana 3",
                LocalDateTime.of(2022,2,22,11,30),
                60,
                123234,
                true); //liefert 11:00 - 12.30
        Urlaub reduzierterUrlaub = new Urlaub(datum,
                LocalTime.of(8,0),
                LocalTime.of(9,30));
        Urlaub reduzierterUrlaub2 = new Urlaub(datum,
                LocalTime.of(10,30),
                LocalTime.of(11,00));
        Urlaub reduzierterUrlaub3 = new Urlaub(datum,
                LocalTime.of(12,30),
                LocalTime.of(14,30));


        //act
        List<Urlaub> ergebnis = urlaubKlausurBearbeitung.urlaubKlausurValidierung(beantragterUrlaub,List.of(klausur, zweiteKlausur));

        //assert
        assertThat(ergebnis).isEqualTo(List.of(reduzierterUrlaub, reduzierterUrlaub2, reduzierterUrlaub3));
    }
    @Test
    @DisplayName("Urlaubsüberschneidung wird festgestellt")
    void test18(){
        //arrange
        Urlaub urlaub = new Urlaub(datum,
                LocalTime.of(8,0),
                LocalTime.of(12,30));
        Urlaub urlaub2 = new Urlaub(datum,
                LocalTime.of(9,0),
                LocalTime.of(14,00));



        //act
        boolean ergebnis = urlaubValidierung.pruefeUrlaubUeberschneidung(urlaub, urlaub2);

        //assert
        assertThat(ergebnis).isTrue();
    }

    @Test
    @DisplayName("Urlaubszeit wird richtig zusammengefasst")
    void test19(){
        //arrange
        Urlaub urlaub = new Urlaub(datum,
                LocalTime.of(8,0),
                LocalTime.of(12,30));
        Urlaub urlaub2 = new Urlaub(datum,
                LocalTime.of(9,0),
                LocalTime.of(14,00));
        Urlaub zusammengefassterUrlaub = new Urlaub(datum,
                LocalTime.of(8,0),
                LocalTime.of(14,00));


        //act
        Urlaub ergebnis = urlaubValidierung.fasseZeitZusammen(urlaub, urlaub2);

        //assert
        assertThat(ergebnis).isEqualTo(zusammengefassterUrlaub);
    }
    //TODO andere Fälle
    @Test
    @DisplayName("5 Urlaubsblöcke werden richtig zu 3 Urlaubsblöcken zusammengefasst")
    void test20(){
        //arrange
        List<Urlaub> urlaube = new ArrayList<>();
        urlaube.add( new Urlaub(datum,
                LocalTime.of(8,0),
                LocalTime.of(9,30)));
        urlaube.add(new Urlaub(datum,
                LocalTime.of(9,0),
                LocalTime.of(9,45)));
        urlaube.add( new Urlaub(datum,
                LocalTime.of(8,30),
                LocalTime.of(9,15)));
        urlaube.add( new Urlaub(datum,
                LocalTime.of(10,0),
                LocalTime.of(11,30)));
        urlaube.add( new Urlaub(datum,
                LocalTime.of(12,0),
                LocalTime.of(13,30)));


        List<Urlaub> zusammengefassterUrlaub = new ArrayList<>();
        zusammengefassterUrlaub.add(new Urlaub(datum,
                LocalTime.of(8,0),
                LocalTime.of(9,45)));
        zusammengefassterUrlaub.add(new Urlaub(datum,
                LocalTime.of(10,0),
                LocalTime.of(11,30)));
        zusammengefassterUrlaub.add( new Urlaub(datum,
                LocalTime.of(12,0),
                LocalTime.of(13,30)));


        //act
        List<Urlaub> ergebnis = urlaubValidierung.urlaubeZusammenfuegen(urlaube);

        //assert
        assertThat(ergebnis).isEqualTo(zusammengefassterUrlaub);
    }
    @Test
    @DisplayName("4 Urlaubsblöcke werden richtig zu 2 zusammengefasst")
    void test21() {
        //arrange
        List<Urlaub> urlaube = new ArrayList<>();
        urlaube.add(new Urlaub(datum,
                LocalTime.of(8, 0),
                LocalTime.of(9, 30)));
        urlaube.add(new Urlaub(datum,
                LocalTime.of(9, 0),
                LocalTime.of(9, 45)));
        urlaube.add(new Urlaub(datum,
                LocalTime.of(10, 30),
                LocalTime.of(12, 15)));
        urlaube.add(new Urlaub(datum,
                LocalTime.of(12, 0),
                LocalTime.of(13, 30)));


        List<Urlaub> zusammengefassterUrlaub = new ArrayList<>();
        zusammengefassterUrlaub.add(new Urlaub(datum,
                LocalTime.of(8, 0),
                LocalTime.of(9, 45)));
        zusammengefassterUrlaub.add(new Urlaub(datum,
                LocalTime.of(10, 30),
                LocalTime.of(13, 30)));


        //act
        List<Urlaub> ergebnis = urlaubValidierung.urlaubeZusammenfuegen(urlaube);

        //assert
        assertThat(ergebnis).isEqualTo(zusammengefassterUrlaub);

    }
    @Test
    @DisplayName("2 Urlaubsblöcke liegen genau übereinander- werden richtig zusammengefasst")
    void test22() {
        //arrange
        List<Urlaub> urlaube = new ArrayList<>();
        urlaube.add(new Urlaub(datum,
                LocalTime.of(8, 0),
                LocalTime.of(9, 30)));
        urlaube.add(new Urlaub(datum,
                LocalTime.of(8, 0),
                LocalTime.of(9, 30)));

        List<Urlaub> zusammengefassterUrlaub = new ArrayList<>();
        zusammengefassterUrlaub.add(new Urlaub(datum,
                LocalTime.of(8, 0),
                LocalTime.of(9, 30)));

        //act
        List<Urlaub> ergebnis = urlaubValidierung.urlaubeZusammenfuegen(urlaube);

        //assert
        assertThat(ergebnis).isEqualTo(zusammengefassterUrlaub);
    }

    @Test
    @DisplayName("Am ganzen Tag Urlaub eingetragen mit 3 Klausuren")
    void test23(){
        //arrange
        Urlaub beantragterUrlaub = new Urlaub(datum,
                LocalTime.of(8,0),
                LocalTime.of(14,30));
        Klausur klausur = new Klausur(1L,
                "mündliche Prüfung Ana 2",
                LocalDateTime.of(2022,2,22,10,0),
                30,
                12345,
                true);   //liefert 9:30 - 10:30
        Klausur zweiteKlausur = new Klausur(1L,
                "mündliche Prüfung Ana 3",
                LocalDateTime.of(2022,2,22,11,30),
                30,
                123234,
                true); //liefert 11:00 - 12.00
        Klausur dritteKlausur = new Klausur(1L,
                "mündliche Prüfung Ana 3",
                LocalDateTime.of(2022,2,22,13,30),
                30,
                123234,
                true); //liefert 13:00 - 14.00
        Urlaub reduzierterUrlaub = new Urlaub(datum,
                LocalTime.of(8,0),
                LocalTime.of(9,30));
        Urlaub reduzierterUrlaub2 = new Urlaub(datum,
                LocalTime.of(10,30),
                LocalTime.of(11,0));
        Urlaub reduzierterUrlaub3 = new Urlaub(datum,
                LocalTime.of(12,0),
                LocalTime.of(13,0));
        Urlaub reduzierterUrlaub4 = new Urlaub(datum,
                LocalTime.of(14,0),
                LocalTime.of(14,30));

        //act
        List<Urlaub> ergebnis = urlaubKlausurBearbeitung.urlaubKlausurValidierung(beantragterUrlaub,List.of(klausur, zweiteKlausur,dritteKlausur));

        //assert
        assertThat(ergebnis).isEqualTo(List.of(reduzierterUrlaub, reduzierterUrlaub2 ,reduzierterUrlaub3,reduzierterUrlaub4));
    }

    @Test
    @DisplayName("Man versucht Urlaub am ganzen Tag anzumelden, am Tag gibt es drei Klausuren," +
            " davon ist eine am Ende des Tages")
    void test24(){
        //arrange
        Urlaub beantragterUrlaub = new Urlaub(datum,
                LocalTime.of(8,0),
                LocalTime.of(14,30));
        Klausur klausur = new Klausur(1L,
                "mündliche Prüfung Ana 2",
                LocalDateTime.of(2022,2,22,10,0),
                30,
                12345,
                true);   //liefert 9:30 - 10:30
        Klausur zweiteKlausur = new Klausur(1L,
                "mündliche Prüfung Ana 3",
                LocalDateTime.of(2022,2,22,11,30),
                30,
                123234,
                true); //liefert 11:00 - 12.00
        Klausur dritteKlausur = new Klausur(1L,
                "mündliche Prüfung Ana 3",
                LocalDateTime.of(2022,2,22,13,30),
                60,
                123234,
                true); //liefert 13:00 - 14.00
        Urlaub reduzierterUrlaub = new Urlaub(datum,
                LocalTime.of(8,0),
                LocalTime.of(9,30));
        Urlaub reduzierterUrlaub2 = new Urlaub(datum,
                LocalTime.of(10,30),
                LocalTime.of(11,0));
        Urlaub reduzierterUrlaub3 = new Urlaub(datum,
                LocalTime.of(12,0),
                LocalTime.of(13,0));

        //act
        List<Urlaub> ergebnis = urlaubKlausurBearbeitung.urlaubKlausurValidierung(beantragterUrlaub,List.of(klausur, zweiteKlausur,dritteKlausur));

        //assert
        assertThat(ergebnis).isEqualTo(List.of(reduzierterUrlaub, reduzierterUrlaub2 ,reduzierterUrlaub3));
    }

    @Test
    @DisplayName("Man versucht Urlaub für den gesamten Tag anzumelden, am Tag gibt es zwei Klausuren, " +
            "dabei ist eine am Anfang und eine am Ende des Tages")
    void test25(){
        //arrange
        Urlaub beantragterUrlaub = new Urlaub(datum,
                LocalTime.of(8,0),
                LocalTime.of(14,30));
        Klausur klausur = new Klausur(1L,
                "mündliche Prüfung Ana 2",
                LocalDateTime.of(2022,2,22,8,30),
                90,
                12345,
                true);   //liefert 8:00 - 10:00
        Klausur zweiteKlausur = new Klausur(1L,
                "mündliche Prüfung Ana 3",
                LocalDateTime.of(2022,2,22,11,30),
                30,
                123234,
                true); //liefert 11:00 - 12.00
        Klausur dritteKlausur = new Klausur(1L,
                "mündliche Prüfung Ana 10",
                LocalDateTime.of(2022,2,22,13,30),
                60,
                123234,
                true); //liefert 13:00 - 14.00
        Urlaub reduzierterUrlaub = new Urlaub(datum,
                LocalTime.of(10,0),
                LocalTime.of(11,00));
        Urlaub reduzierterUrlaub2 = new Urlaub(datum,
                LocalTime.of(12,0),
                LocalTime.of(13,0));

        //act
        List<Urlaub> ergebnis = urlaubKlausurBearbeitung.urlaubKlausurValidierung(beantragterUrlaub,List.of(klausur, zweiteKlausur,dritteKlausur));

        //assert
        assertThat(ergebnis).isEqualTo(List.of(reduzierterUrlaub, reduzierterUrlaub2));
    }

    @Test
    @DisplayName("Am ganzen Tag Urlaub eingetragen mit 3 Klausuren")
    void test26(){
        //arrange
        Urlaub beantragterUrlaub = new Urlaub(datum,
                LocalTime.of(8,0),
                LocalTime.of(9,0));
        Urlaub beantragterUrlaub1 = new Urlaub(datum,
                LocalTime.of(10,0),
                LocalTime.of(13,30));
        Urlaub beantragterUrlaub2 = new Urlaub(datum,
                LocalTime.of(14,15),
                LocalTime.of(14,30));

        Klausur klausur = new Klausur(1L,
                "mündliche Prüfung Ana 2",
                LocalDateTime.of(2022,2,22,10,0),
                30,
                12345,
                true);   //liefert 9:30 - 10:30
        Klausur zweiteKlausur = new Klausur(1L,
                "mündliche Prüfung Ana 3",
                LocalDateTime.of(2022,2,22,11,30),
                30,
                123234,
                true); //liefert 11:00 - 12.00
        Urlaub reduzierterUrlaub = new Urlaub(datum,
                LocalTime.of(8,0),
                LocalTime.of(9,0));
        Urlaub reduzierterUrlaub2 = new Urlaub(datum,
                LocalTime.of(10,30),
                LocalTime.of(11,0));
        Urlaub reduzierterUrlaub3 = new Urlaub(datum,
                LocalTime.of(12,0),
                LocalTime.of(13,30));
        Urlaub reduzierterUrlaub4 = new Urlaub(datum,
                LocalTime.of(14,15),
                LocalTime.of(14,30));


        //act
        List<Urlaub> ergebnis = urlaubKlausurBearbeitung.
                reduziereUrlaubDurchMehrereKlausuren(List.of(beantragterUrlaub,beantragterUrlaub1, beantragterUrlaub2),
                List.of(klausur, zweiteKlausur));

        //assert
        assertThat(ergebnis).isEqualTo(List.of(reduzierterUrlaub, reduzierterUrlaub2 ,reduzierterUrlaub3, reduzierterUrlaub4));
    }

    @Test
    @DisplayName("Man möchte drei Urlaube anmelden, diese schließen passend mit zwei Klausuren ab")
    void test27(){
        //arrange
        Urlaub beantragterUrlaub = new Urlaub(datum,
                LocalTime.of(8,0),
                LocalTime.of(9,0));
        Urlaub beantragterUrlaub1 = new Urlaub(datum,
                LocalTime.of(10,0),
                LocalTime.of(13,00));
        Urlaub beantragterUrlaub2 = new Urlaub(datum,
                LocalTime.of(14,15),
                LocalTime.of(14,30));

        Klausur klausur = new Klausur(1L,
                "mündliche Prüfung Ana 2",
                LocalDateTime.of(2022,2,22,9,30),
                30,
                12345,
                true);   //liefert 9:00 - 10:00
        Klausur zweiteKlausur = new Klausur(1L,
                "mündliche Prüfung Ana 3",
                LocalDateTime.of(2022,2,22,13,30),
                45,
                123234,
                true); //liefert 11:00 - 12.00
        Urlaub reduzierterUrlaub = new Urlaub(datum,
                LocalTime.of(8,0),
                LocalTime.of(9,0));
        Urlaub reduzierterUrlaub1 = new Urlaub(datum,
                LocalTime.of(10,0),
                LocalTime.of(13,0));
        Urlaub reduzierterUrlaub2 = new Urlaub(datum,
                LocalTime.of(14,15),
                LocalTime.of(14,30));


        //act
        List<Urlaub> ergebnis = urlaubKlausurBearbeitung.
                reduziereUrlaubDurchMehrereKlausuren(List.of(beantragterUrlaub,beantragterUrlaub1, beantragterUrlaub2),
                        List.of(klausur, zweiteKlausur));

        //assert
        assertThat(ergebnis).isEqualTo(List.of(reduzierterUrlaub, reduzierterUrlaub1 ,reduzierterUrlaub2));
    }

    //TODO::ANDERE FÄLLE


}
