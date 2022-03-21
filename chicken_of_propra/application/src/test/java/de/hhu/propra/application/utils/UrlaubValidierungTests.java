package de.hhu.propra.application.utils;

import de.hhu.propra.domain.aggregates.student.Urlaub;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

public class UrlaubValidierungTests {


    private UrlaubValidierung urlaubsMethoden = new UrlaubValidierung();

    @Test
    @DisplayName("Die Minuten der start/end-Zeit sind Vielfaches von 15")
    void test1(){
        Urlaub urlaub = new Urlaub(LocalDate.now()
                , LocalTime.of(10,11)
                , LocalTime.of(15,15));
        boolean ergebnis = urlaubsMethoden.vielfachesVon15(urlaub);
        assertThat(ergebnis).isEqualTo(false);
    }

    @Test
    @DisplayName("Urlaubsdauer beträgt 240 Minuten oder 150 Minuten maximal")
    void test2(){
        Urlaub urlaub = new Urlaub(LocalDate.now()
                , LocalTime.of(10,0)
                , LocalTime.of(12,40));
        boolean ergebnis = urlaubsMethoden.dauerIstValide(urlaub);
        assertThat(ergebnis).isEqualTo(false);
    }

    @Test
    @DisplayName("Zwei Urlaube haben keine 90 Minuten dazwischen")
    void test3(){

        Urlaub ersterUrlaub = new Urlaub(LocalDate.now()
                , LocalTime.of(8,30)
                , LocalTime.of(9,0));
        Urlaub zweiterUrlaub = new Urlaub(LocalDate.now()
                , LocalTime.of(9,30)
                , LocalTime.of(12,30));
        boolean ergebnis = urlaubsMethoden.zweiUrlaubeAnEinemTag(ersterUrlaub,zweiterUrlaub);
        assertThat(ergebnis).isEqualTo(false);
    }

    @Test
    @DisplayName("Erster Urlaub nicht am Praktikumsanfang")
    void test4(){
        Urlaub ersterUrlaub = new Urlaub(LocalDate.now()
                , LocalTime.of(9,30)
                , LocalTime.of(10,0));
        Urlaub zweiterUrlaub = new Urlaub(LocalDate.now()
                , LocalTime.of(11,30)
                , LocalTime.of(12,30));
        boolean ergebnis = urlaubsMethoden.zweiUrlaubeAnEinemTag(ersterUrlaub,zweiterUrlaub);
        assertThat(ergebnis).isEqualTo(false);
    }

    @Test
    @DisplayName("Zweiter Urlaub nicht am Praktikumsende")
    void test5(){
        Urlaub ersterUrlaub = new Urlaub(LocalDate.now()
                , LocalTime.of(8,30)
                , LocalTime.of(10,0));
        Urlaub zweiterUrlaub = new Urlaub(LocalDate.now()
                , LocalTime.of(11,30)
                , LocalTime.of(13,30));
        boolean ergebnis = urlaubsMethoden.zweiUrlaubeAnEinemTag(ersterUrlaub,zweiterUrlaub);
        assertThat(ergebnis).isEqualTo(false);
    }

    @Test
    @DisplayName("Urlaub nur bis einene Tag vorher eintragbar")
    void test6(){
        Urlaub urlaub = new Urlaub(LocalDate.of(2001,9,11)
                , LocalTime.of(8,30)
                , LocalTime.of(10,0));
        boolean ergebnis = urlaubsMethoden.urlaubNurVorDemTagDesUrlaubs(urlaub);
        assertThat(ergebnis).isEqualTo(false);
    }

}
