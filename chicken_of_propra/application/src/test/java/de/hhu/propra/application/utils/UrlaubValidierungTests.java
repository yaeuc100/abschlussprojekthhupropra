package de.hhu.propra.application.utils;

import de.hhu.propra.application.fehler.UrlaubFehler;
import de.hhu.propra.domain.aggregates.student.Student;
import de.hhu.propra.domain.aggregates.student.Urlaub;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class UrlaubValidierungTests {


    private UrlaubValidierung urlaubValidierung = new UrlaubValidierung();


    @Test
    @DisplayName("Die Minuten der Start- und Endzeit sind kein Vielfaches von 15")
    void test1(){
        //arrange
        Urlaub urlaub = new Urlaub(LocalDate.of(2001,9,11),
                LocalTime.of(10,11),
                LocalTime.of(15,15));

        //act
        boolean ergebnis = urlaubValidierung.vielfachesVon15(urlaub);
        Set<String> fehler = urlaubValidierung.getFehlgeschlagen();

        //assert
        assertThat(ergebnis).isEqualTo(false);
        assertThat(fehler).contains(UrlaubFehler.VIELFACHES_VON_15);
        assertThat(fehler).hasSize(1);

    }

    @Test
    @DisplayName("Urlaubsdauer beträgt keine 240 Minuten oder 150 Minuten maximal")
    void test2(){
        //arrange
        Urlaub urlaub = new Urlaub(LocalDate.of(2001,9,11),
                LocalTime.of(10,0),
                LocalTime.of(12,40));

        //act
        boolean ergebnis = urlaubValidierung.dauerIstValide(urlaub);
        Set<String> fehler = urlaubValidierung.getFehlgeschlagen();

        //assert
        assertThat(ergebnis).isEqualTo(false);
        assertThat(fehler).contains(UrlaubFehler.DAUER_IST_VALIDE);
        assertThat(fehler).hasSize(1);
    }

    @Test
    @DisplayName("Zwei Urlaube müssen 90 Minuten dazwischen haben")
    void test3(){
        //arrange

        Urlaub ersterUrlaub = new Urlaub(LocalDate.of(2001,9,11),
                LocalTime.of(8,30),
                LocalTime.of(9,0));
        Urlaub zweiterUrlaub = new Urlaub(LocalDate.of(2001,9,11),
                LocalTime.of(9,30),
                LocalTime.of(12,30));

        //act
        boolean ergebnis = urlaubValidierung.zweiUrlaubeAnEinemTag(ersterUrlaub,zweiterUrlaub);
        Set<String> fehler = urlaubValidierung.getFehlgeschlagen();

        //assert
        assertThat(ergebnis).isEqualTo(false);
        assertThat(fehler).contains(UrlaubFehler.ZWEI_URLAUBE_AN_TAG);
        assertThat(fehler).hasSize(1);
    }

    @Test
    @DisplayName("Erster Urlaub nicht am Praktikumsanfang")
    void test4(){
        //arrange
        Urlaub ersterUrlaub = new Urlaub(LocalDate.of(2001,9,11),
                LocalTime.of(9,30),
                LocalTime.of(10,0));
        Urlaub zweiterUrlaub = new Urlaub(LocalDate.now(),
                LocalTime.of(11,30),
                LocalTime.of(12,30));

        //act
        boolean ergebnis = urlaubValidierung.zweiUrlaubeAnEinemTag(ersterUrlaub,zweiterUrlaub);
        Set<String> fehler = urlaubValidierung.getFehlgeschlagen();




        //assert
        assertThat(ergebnis).isEqualTo(false);
        assertThat(fehler).contains(UrlaubFehler.ZWEI_URLAUBE_AN_TAG);
        assertThat(fehler).hasSize(1);
    }

    @Test
    @DisplayName("Zweiter Urlaub nicht am Praktikumsende")
    void test5(){
        //arrange
        Urlaub ersterUrlaub = new Urlaub(LocalDate.of(2001,9,11),
                LocalTime.of(8,30),
                LocalTime.of(10,0));
        Urlaub zweiterUrlaub = new Urlaub(LocalDate.of(2001,9,11),
                LocalTime.of(11,30),
                LocalTime.of(13,30));

        //act
        boolean ergebnis = urlaubValidierung.zweiUrlaubeAnEinemTag(ersterUrlaub,zweiterUrlaub);
        Set<String> fehler = urlaubValidierung.getFehlgeschlagen();


        //assert
        assertThat(ergebnis).isEqualTo(false);
        assertThat(fehler).contains(UrlaubFehler.ZWEI_URLAUBE_AN_TAG);
        assertThat(fehler).hasSize(1);
    }

    @Test
    @DisplayName("Ein Urlaub kann nur bis einen Tag vorher eintragbar")
    void test6(){
        //arrange
        Urlaub urlaub = new Urlaub(LocalDate.of(2001,9,11),
                LocalTime.of(8,30),
                LocalTime.of(10,0));

        //act
        boolean ergebnis = urlaubValidierung.urlaubNurVorDemTagDesUrlaubs(urlaub);
        Set<String> fehler = urlaubValidierung.getFehlgeschlagen();

        //assert
        assertThat(ergebnis).isEqualTo(false);
        assertThat(fehler).contains(UrlaubFehler.ANTRAG_RECHTZEITIG);
        assertThat(fehler).hasSize(1);
    }

    @Test
    @DisplayName("Urlaub darf nicht am Wochenende liegen")
    void test7(){
        //arrange
        Urlaub urlaub = new Urlaub(LocalDate.of(2022,3,19),
                LocalTime.of(8,30),
                LocalTime.of(10,0));

        //act
        boolean ergebnis = urlaubValidierung.amWochenende(urlaub);
        Set<String> fehler = urlaubValidierung.getFehlgeschlagen();

        //assert
        assertThat(ergebnis).isEqualTo(false);
        assertThat(fehler).contains(UrlaubFehler.AM_WOCHENENDE);
        assertThat(fehler).hasSize(1);
    }

    @Test
    @DisplayName("Die Startzeit des Urlaubs liegt nicht vor der Endzeit des Urlaubs")
    void test8(){
        //arrange
        Urlaub urlaub = new Urlaub(LocalDate.of(2001,9,11),
                LocalTime.of(10,30),
                LocalTime.of(9,0));

        //act
        boolean ergebnis = urlaubValidierung.startzeitVorEndzeit(urlaub);
        Set<String> fehler = urlaubValidierung.getFehlgeschlagen();

        //assert
        assertThat(ergebnis).isEqualTo(false);
        assertThat(fehler).contains(UrlaubFehler.STARTZEIT_VOR_ENDZEIT);
        assertThat(fehler).hasSize(1);
    }

    @Test
    @DisplayName("Urlaub liegt nicht im Praktikumszeitraum")
    void test9(){
        //arrange
        Urlaub urlaub = new Urlaub(LocalDate.of(1,9,11),
                LocalTime.of(8,30),
                LocalTime.of(10,0));

        //act
        boolean ergebnis = urlaubValidierung.datumLiegtInPraktikumszeit(urlaub);
        Set<String> fehler = urlaubValidierung.getFehlgeschlagen();

        //assert
        assertThat(ergebnis).isEqualTo(false);
        assertThat(fehler).contains(UrlaubFehler.URLAUB_IN_ZEITRAUM);
        assertThat(fehler).hasSize(1);
    }

    @Test
    @DisplayName("Die Anzahl der Urlaube ist 2, somit größer als max. 1 ")
    void test10(){
        //arrange
        List<Urlaub> urlaube = List.of(new Urlaub(LocalDate.of(2001,9,11),
                                        LocalTime.of(8,30),
                                        LocalTime.of(10,0)),
                                       new Urlaub(LocalDate.of(2001,9,11),
                                        LocalTime.of(8,30),
                                        LocalTime.of(10,0)));

        //act
        boolean ergebnis = urlaubValidierung.bisherMaxEinUrlaub(urlaube);
        Set<String> fehler = urlaubValidierung.getFehlgeschlagen();

        //assert
        assertThat(ergebnis).isEqualTo(false);
    }

    @Test
    @DisplayName("Der Student hat nicht genug Resturlaub")
    void test11(){
        //arrange
        Urlaub urlaub = new Urlaub(LocalDate.of(2001,9,11),
                        LocalTime.of(8,30),
                        LocalTime.of(14,0));
        Student student = new Student(1L, "x");

        //act
        boolean ergebnis = urlaubValidierung.genugUrlaub(student, urlaub);
        Set<String> fehler = urlaubValidierung.getFehlgeschlagen();

        //assert
        assertThat(ergebnis).isEqualTo(false);
        assertThat(fehler).contains(UrlaubFehler.NICHT_GENUG_URLAUB_VORHANDEN);
        assertThat(fehler).hasSize(1);
    }

    @Test
    @DisplayName("Die Überschneidung von zwei Urlauben wird gefunden")
    void test12(){
        //arrange
        Urlaub ersterUrlaub = new Urlaub(LocalDate.of(2001,9,11)
                , LocalTime.of(8,30)
                , LocalTime.of(10,0));
        Urlaub zweiterUrlaub = new Urlaub(LocalDate.of(2001,9,11)
                , LocalTime.of(9,30)
                , LocalTime.of(13,30));

        //act
        boolean ergebnis = urlaubValidierung.pruefeUrlaubUeberschneidung(ersterUrlaub,zweiterUrlaub);

        //assert
        assertThat(ergebnis).isEqualTo(true);
    }

    @Test
    @DisplayName("Ein Urlaub ist valide")
    void test13(){
        //arrange
        Urlaub urlaub = new Urlaub(LocalDate.of(2022, 3, 23),
                LocalTime.of(8,30),
                LocalTime.of(10,0));


        //act
        boolean ergebnis = urlaubValidierung.urlaubIstValide(urlaub);

        //assert
        assertThat(ergebnis).isEqualTo(true);
    }

    @Test
    @DisplayName("Drei Urlaube an einem Tag werden richtig zu zwei zusammengefügt")
    void test14(){
        //arrange
        List<Urlaub> urlaube = Stream.of(new Urlaub(LocalDate.of(2001,9,11),
                        LocalTime.of(8,30),
                        LocalTime.of(10,0)),
                new Urlaub(LocalDate.of(2001,9,11),
                        LocalTime.of(9,30),
                        LocalTime.of(11,0)),
                new Urlaub(LocalDate.of(2001,9,11),
                        LocalTime.of(11,30),
                        LocalTime.of(12,0))).collect(Collectors.toList());
        List<Urlaub> ergebnis = List.of(new Urlaub(LocalDate.of(2001,9,11),
                        LocalTime.of(8,30),
                        LocalTime.of(11,0)),
                new Urlaub(LocalDate.of(2001,9,11),
                        LocalTime.of(11,30),
                        LocalTime.of(12,0)));

        //act
       List<Urlaub> neueUrlaube = urlaubValidierung.urlaubeZusammenfuegen(urlaube);


        //assert
        assertThat(neueUrlaube).isEqualTo(ergebnis);
    }

    @Test
    @DisplayName("Zwei identische Urlaube werden zu einem zusammengefügt")
    void test15(){
        //arrange
        List<Urlaub> urlaube = Stream.of(new Urlaub(LocalDate.of(2001,9,11),
                        LocalTime.of(8,30),
                        LocalTime.of(10,0)),
                new Urlaub(LocalDate.of(2001,9,11),
                        LocalTime.of(8,30),
                        LocalTime.of(10,0))).collect(Collectors.toList());
        List<Urlaub> ergebnis = List.of(new Urlaub(LocalDate.of(2001,9,11),
                        LocalTime.of(8,30),
                        LocalTime.of(10,0)));

        //act
        List<Urlaub> neueUrlaube = urlaubValidierung.urlaubeZusammenfuegen(urlaube);


        //assert
        assertThat(neueUrlaube).isEqualTo(ergebnis);
    }

    @Test
    @DisplayName("Drei Urlaube, die sich überschneiden werden genau zu einem zusammengefügt")
    void test16(){
        //arrange
        List<Urlaub> urlaube = Stream.of(new Urlaub(LocalDate.of(2001,9,11),
                        LocalTime.of(8,30),
                        LocalTime.of(10,30)),
                new Urlaub(LocalDate.of(2001,9,11),
                        LocalTime.of(10,0),
                        LocalTime.of(11,30)),
                new Urlaub(LocalDate.of(2001,9,11),
                        LocalTime.of(11,0),
                        LocalTime.of(12,0))).collect(Collectors.toList());
        List<Urlaub> ergebnis = List.of(new Urlaub(LocalDate.of(2001,9,11),
                        LocalTime.of(8,30),
                        LocalTime.of(12,0)));

        //act
        List<Urlaub> neueUrlaube = urlaubValidierung.urlaubeZusammenfuegen(urlaube);


        //assert
        assertThat(neueUrlaube).isEqualTo(ergebnis);
    }

    @Test
    @DisplayName("Sechs Urlaube werden richtig zu zwei zusammengefügt")
    void test17(){
        //arrange
        List<Urlaub> urlaube = Stream.of(new Urlaub(LocalDate.of(2001,9,11),
                        LocalTime.of(8,30),
                        LocalTime.of(10,30)),
                new Urlaub(LocalDate.of(2001,9,11),
                        LocalTime.of(10,0),
                        LocalTime.of(12,0)),
                new Urlaub(LocalDate.of(2001,9,11),
                        LocalTime.of(9,0),
                        LocalTime.of(11,30)),
                new Urlaub(LocalDate.of(2001,9,11),
                        LocalTime.of(12,0),
                        LocalTime.of(13,30)),
                new Urlaub(LocalDate.of(2001,9,11),
                        LocalTime.of(12,15),
                        LocalTime.of(12,30)),
                new Urlaub(LocalDate.of(2001,9,11),
                        LocalTime.of(12,15),
                        LocalTime.of(13,30))).collect(Collectors.toList());
        List<Urlaub> ergebnis = List.of(new Urlaub(LocalDate.of(2001,9,11),
                        LocalTime.of(8,30),
                        LocalTime.of(12,0)),
                new Urlaub(LocalDate.of(2001,9,11),
                        LocalTime.of(12,0),
                        LocalTime.of(13,30)));

        //act
        List<Urlaub> neueUrlaube = urlaubValidierung.urlaubeZusammenfuegen(urlaube);


        //assert
        assertThat(neueUrlaube).isEqualTo(ergebnis);
    }
}
