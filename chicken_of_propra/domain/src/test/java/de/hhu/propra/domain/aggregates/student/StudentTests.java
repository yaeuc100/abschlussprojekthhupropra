package de.hhu.propra.domain.aggregates.student;

import de.hhu.propra.domain.aggregates.klausur.Klausur;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class StudentTests {

  @Test
  @DisplayName("Ein Urlaub wird zum Student hinzugefügt")
  void test1() {
    //arrange
    Student student = new Student(1L, "olli");

    //act
    student.addUrlaub(LocalDate.of(2022, 2, 22),
        LocalTime.of(10, 0),
        LocalTime.of(12, 0));
    List<Urlaub> urlaube = student.getUrlaube();

    //assert
    assertThat(urlaube).contains(new Urlaub(LocalDate.of(2022, 2, 22),
        LocalTime.of(10, 0),
        LocalTime.of(12, 0)));
    assertThat(urlaube).hasSize(1);

  }

  @Test
  @DisplayName("Zwei Urlaube werden zum Studenten hinzugefügt")
  void test2() {
    //arrange
    Student student = new Student(1L, "olli");

    //act
    student.addUrlaub(LocalDate.of(2022, 2, 22),
        LocalTime.of(10, 0),
        LocalTime.of(12, 0));
    student.addUrlaub(LocalDate.of(2022, 2, 22),
        LocalTime.of(9, 30),
        LocalTime.of(11, 30));
    List<Urlaub> urlaube = student.getUrlaube();

    //assert
    assertThat(urlaube).contains(new Urlaub(LocalDate.of(2022, 2, 22),
            LocalTime.of(10, 0),
            LocalTime.of(12, 0)),
        new Urlaub(LocalDate.of(2022, 2, 22),
            LocalTime.of(10, 0),
            LocalTime.of(12, 0)));
    assertThat(urlaube).hasSize(2);


  }

  @Test
  @DisplayName("Ein Urlaub wird zum Student hinzugefügt und der Resturlaub wird richtig berechnet")
  void test3() {
    //arrange
    Student student = new Student(1L, "olli");

    //act
    student.addUrlaub(LocalDate.of(2022, 2, 22),
        LocalTime.of(10, 0),
        LocalTime.of(12, 0));
    student.berechneResturlaub();
    int resturlaub = student.getResturlaub();

    //assert
    assertThat(resturlaub).isEqualTo(120);
  }

  @Test
  @DisplayName("Bei zwei Urlauben wird der Resturlaub richtig berechnet")
  void test4() {
    //arrange
    Student student = new Student(1L, "olli");
    student.addUrlaub(LocalDate.of(2022, 2, 22),
        LocalTime.of(10, 0),
        LocalTime.of(12, 0));
    student.addUrlaub(LocalDate.of(2022, 2, 23),
        LocalTime.of(9, 30),
        LocalTime.of(11, 30));

    //act
    student.berechneResturlaub();
    int resturlaub = student.getResturlaub();

    //assert

    assertThat(resturlaub).isEqualTo(0);
  }


  @Test
  @DisplayName("Ein Urlaub zum Studenten hinzugefügt und anschließend storniert")
  void test5() {
    //arrange
    Student student = new Student(1L, "olli");

    //act
    student.addUrlaub(LocalDate.of(2022, 2, 23),
        LocalTime.of(10, 0),
        LocalTime.of(12, 0));
    student.urlaubStornieren(LocalDate.of(2022, 2, 23),
        LocalTime.of(10, 0),
        LocalTime.of(12, 0));
    List<Urlaub> urlaube = student.getUrlaube();

    //assert
    assertThat(urlaube).isEmpty();
  }


  @Test
  @DisplayName("Wenn kein Urlaub storniert wird, gibt die urlaubStornieren-Methode false zurück")
  void test6() {
    //arrange
    Student student = new Student(1L, "olli");

    //act
    student.addUrlaub(LocalDate.of(2022, 2, 23),
        LocalTime.of(10, 0),
        LocalTime.of(12, 0));
    boolean ergebnis = student.urlaubStornieren(LocalDate.of(2022, 2, 24),
        LocalTime.of(10, 0),
        LocalTime.of(12, 0));

    //assert
    assertThat(ergebnis).isEqualTo(false);
  }

  @Test
  @DisplayName("Wenn ein Urlaub storniert wird, gibt die urlaubStornieren-Methode true zurück")
  void test7() {
    //arrange
    Student student = new Student(1L, "olli");

    //act
    student.addUrlaub(LocalDate.of(2022, 2, 23),
        LocalTime.of(10, 0),
        LocalTime.of(12, 0));
    boolean ergebnis = student.urlaubStornieren(LocalDate.of(2022, 2, 23),
        LocalTime.of(10, 0),
        LocalTime.of(12, 0));

    //assert
    assertThat(ergebnis).isEqualTo(true);
  }

  @Test
  @DisplayName("Bei zwei vorhandenen Urlauben wird einer richtig storniert")
  void test8() {
    //arrange
    Student student = new Student(1L, "olli");
    student.addUrlaub(LocalDate.of(2022, 2, 23),
        LocalTime.of(10, 0),
        LocalTime.of(12, 0));
    student.addUrlaub(LocalDate.of(2022, 2, 22),
        LocalTime.of(10, 0),
        LocalTime.of(12, 0));

    //act
    boolean ergebnis = student.urlaubStornieren(LocalDate.of(2022, 2, 23),
        LocalTime.of(10, 0),
        LocalTime.of(12, 0));
    List<Urlaub> urlaube = student.getUrlaube();

    //assert
    assertThat(urlaube).hasSize(1);
    assertThat(ergebnis).isTrue();
  }

  @Test
  @DisplayName("Methode urlaubExistiertSchon gibt true zurück, falls der Urlaub schon existiert")
  void test9() {
    //arrange
    Student student = new Student(1L, "olli");
    student.addUrlaub(LocalDate.of(2022, 2, 23),
        LocalTime.of(10, 0),
        LocalTime.of(12, 0));

    //act
    boolean ergebnis = student.urlaubExistiertSchon(LocalDate.of(2022, 2, 23),
        LocalTime.of(10, 0),
        LocalTime.of(12, 0));

    //assert
    assertThat(ergebnis).isTrue();
  }

  @Test
  @DisplayName("Methode urlaubExistiertSchon gibt false zurück, falls der Urlaub noch nicht existiert")
  void test10() {
    //arrange
    Student student = new Student(1L, "olli");
    student.addUrlaub(LocalDate.of(2022, 2, 23),
        LocalTime.of(10, 0),
        LocalTime.of(12, 0));

    //act
    boolean ergebnis = student.urlaubExistiertSchon(LocalDate.of(2023, 2, 23),
        LocalTime.of(10, 0),
        LocalTime.of(12, 0));

    //assert
    assertThat(ergebnis).isFalse();
  }


  @Test
  @DisplayName("Eine Klausur wird richtig stoniert")
  void test12() {
    //arrange
    Student student = new Student(1L, "olli");
    Klausur klausur = new Klausur(1L, "Mathe",
        LocalDateTime.of(2022, 3, 22, 9, 0),
        90,
        12345, true);
    student.addKlausur(klausur);

    //act
    student.klausurStornieren(klausur);
    List<Long> klausuren = student.getKlausuren();

    //assert
    assertThat(klausuren).hasSize(0);
  }

  @Test
  @DisplayName("Die genommene Urlaubszeit wird richtig berechnet")
  void test13() {
    //arrange
    Student student = new Student(1L, "olli");
    student.addUrlaub(LocalDate.of(2022, 2, 23),
        LocalTime.of(10, 0),
        LocalTime.of(12, 0));
    student.addUrlaub(LocalDate.of(2022, 2, 22),
        LocalTime.of(10, 0),
        LocalTime.of(12, 0));
    student.berechneResturlaub();

    //act
    int urlaubszeit = student.berechneUrlaubszeit();

    //assert
    assertThat(urlaubszeit).isEqualTo(240);
  }

  @Test
  @DisplayName("Zwei identische Studenten werden als gleich erkannt")
  void test14() {
    //arrange
    Student student = new Student(1L, "olli");
    Student zweiterStudent = new Student(1L, "olli");

    //act
    boolean ergebnis = student.equals(zweiterStudent);

    //assert
    assertThat(ergebnis).isTrue();
  }

  @Test
  @DisplayName("Die genommene Urlaubszeit wird richtig berechnet")
  void test15() {
    //arrange
    Student student = new Student(1L, "olli");
    Student zweiterStudent = new Student(12L, "olli");

    //act
    boolean ergebnis = student.equals(zweiterStudent);

    //assert
    assertThat(ergebnis).isFalse();
  }


}
