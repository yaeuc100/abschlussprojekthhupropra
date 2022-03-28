package de.hhu.propra.domain.aggregates.klausur;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class KlausurTests {


  @Test
  @DisplayName("Die Klausur wird richtig formatiert")
  void test1() {
    //arrange
    Klausur klausur = new Klausur(1L,
        "Rechnernetze",
        LocalDateTime.of(2020, 1, 1, 10, 0),
        60, 123456, true);

    //act
    String formatiert = klausur.formatiert();

    //assert
    assertThat(formatiert).isEqualTo("Rechnernetze ( Datum 2020-01-01, 10:00 Uhr - 11:00 Uhr )");
  }

  @Test
  @DisplayName("Zwei gleiche Klausuren, werden als gleich erkannt")
  void test2() {
    //arrange
    Klausur klausur = new Klausur(1L,
        "Rechnernetze",
        LocalDateTime.of(2020, 1, 1, 10, 0),
        60, 123456, true);
    Klausur klausur2 = new Klausur(1L,
        "Rechnernetze",
        LocalDateTime.of(2020, 1, 1, 10, 0),
        60, 123456, true);

    //act
    boolean ergebnis = klausur.equals(klausur2);

    //assert
    assertThat(ergebnis).isTrue();

  }

  @Test
  @DisplayName("Zwei verschiedene Klausuren, werden als unterschiedlich erkannt")
  void test3() {
    //arrange
    Klausur klausur = new Klausur(1L,
        "Rechnernetze",
        LocalDateTime.of(2020, 1, 1, 10, 0),
        60, 123756, true);
    Klausur klausur2 = new Klausur(1L,
        "Rechnernetze",
        LocalDateTime.of(2020, 1, 1, 10, 0),
        60, 123456, true);

    //act
    boolean ergebnis = klausur.equals(klausur2);

    //assert
    assertThat(ergebnis).isFalse();

  }
}
