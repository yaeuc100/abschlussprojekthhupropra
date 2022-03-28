package de.hhu.propra.domain.aggregates.student;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;


public class UrlaubTests {

  @Test
  @DisplayName("Zwei gleiche Urlaube werden als gleich erkannt")
  void test1() {
    //arrange
    Urlaub urlaub = new Urlaub(LocalDate.of(2001, 9, 11),
        LocalTime.of(10, 11),
        LocalTime.of(15, 15));
    Urlaub urlaub2 = new Urlaub(LocalDate.of(2001, 9, 11),
        LocalTime.of(10, 11),
        LocalTime.of(15, 15));

    //act
    boolean ergebnis = urlaub.equals(urlaub2);

    //assert
    assertThat(ergebnis).isTrue();
  }

  @Test
  @DisplayName("Zwei verschiedene Urlaube werden als unterschiedlich erkannt")
  void test2() {
    //arrange
    Urlaub urlaub = new Urlaub(LocalDate.of(2001, 9, 11),
        LocalTime.of(10, 11),
        LocalTime.of(15, 15));
    Urlaub urlaub2 = new Urlaub(LocalDate.of(2001, 9, 11),
        LocalTime.of(10, 11),
        LocalTime.of(15, 30));

    //act
    boolean ergebnis = urlaub.equals(urlaub2);

    //assert
    assertThat(ergebnis).isFalse();
  }

  @Test
  @DisplayName("Der Zeitraum des Urlaub wird richtig berechnet")
  void test3() {
    //arrange
    Urlaub urlaub = new Urlaub(LocalDate.of(2001, 9, 11),
        LocalTime.of(10, 11),
        LocalTime.of(15, 15));

    //act
    long ergebnis = urlaub.berechneZeitraum();

    //assert
    assertThat(ergebnis).isEqualTo(304L);
  }

  @Test
  @DisplayName("Der Urlaub wird richtig als String dargestellt")
  void test4() {
    //arrange
    Urlaub urlaub = new Urlaub(LocalDate.of(2001, 9, 11),
        LocalTime.of(10, 11),
        LocalTime.of(15, 15));

    //act
    String ergebnis = urlaub.toString();

    //assert
    assertThat(ergebnis).isEqualTo("Urlaub{datum=2001-09-11, startzeit=10:11, endzeit=15:15}");
  }


}
