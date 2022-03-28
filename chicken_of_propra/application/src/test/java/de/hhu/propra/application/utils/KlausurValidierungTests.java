package de.hhu.propra.application.utils;

import de.hhu.propra.application.dto.KlausurDto;
import de.hhu.propra.application.dto.UrlaubDto;
import de.hhu.propra.application.fehler.KlausurFehler;
import de.hhu.propra.application.fehler.UrlaubFehler;
import de.hhu.propra.domain.aggregates.klausur.Klausur;
import de.hhu.propra.domain.aggregates.student.Urlaub;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;


public class KlausurValidierungTests {

  KlausurValidierung klausurValidierung = new KlausurValidierung();


  @Test
  @DisplayName("Der Startzeitpunkt einer Klausur muss ein Vielfaches von 15 sein")
  void test() {
    //arrange
    KlausurDto klausurDto = new KlausurDto("x",
        LocalDate.of(2023, 10, 10).toString(),
        LocalTime.of(10, 1).toString(),
        LocalTime.of(11, 0).toString(),
        123456,
        true);

    //act
    boolean ergebnis = klausurValidierung.vielfachesVon15(klausurDto);
    Set<String> fehler = klausurValidierung.getFehlgeschlagen();

    //assert
    assertThat(ergebnis).isFalse();
    assertThat(fehler).contains(KlausurFehler.VIELFACHES_VON_15);
    assertThat(fehler).hasSize(1);
  }

  @Test
  @DisplayName("Der Endzeitpunkt einer Klausur muss ein Vielfaches von 15 sein")
  void test01() {
    //arrange
    KlausurDto klausurDto = new KlausurDto("x",
        LocalDate.of(2023, 10, 10).toString(),
        LocalTime.of(10, 0).toString(),
        LocalTime.of(11, 1).toString(),
        123456,
        true);

    //act
    boolean ergebnis = klausurValidierung.vielfachesVon15(klausurDto);
    Set<String> fehler = klausurValidierung.getFehlgeschlagen();

    //assert
    assertThat(ergebnis).isFalse();
    assertThat(fehler).contains(KlausurFehler.VIELFACHES_VON_15);
    assertThat(fehler).hasSize(1);
  }

  @Test
  @DisplayName("Das Datum der Klausur liegt nicht im Praktikumszeitraum")
  void test1() {
    //arrange
    KlausurDto klausurDto = new KlausurDto("x",
        LocalDate.of(6000, 10, 10).toString(),
        LocalTime.of(10, 30).toString(),
        LocalTime.of(11, 0).toString(),
        123456,
        true);

    //act
    boolean ergebnis = klausurValidierung.datumLiegtInPraktikumszeit(klausurDto);

    //assert
    assertThat(ergebnis).isFalse();
  }


  @Test
  @DisplayName("Erstellte Klausur darf nicht am Wochenende liegen")
  void test7() {
    //arrange
    KlausurDto klausur = new KlausurDto("x",
        LocalDate.of(2022, 3, 19).toString(),
        LocalTime.of(10, 30).toString(),
        LocalTime.of(11, 0).toString(),
        222916,
        true);

    //act
    boolean ergebnis = klausurValidierung.amWochenende(klausur);
    Set<String> fehler = klausurValidierung.getFehlgeschlagen();

    //assert
    assertThat(ergebnis).isFalse();
    assertThat(fehler).contains(KlausurFehler.AM_WOCHENENDE);
    assertThat(fehler).hasSize(1);
  }

  @Test
  @DisplayName("Die Startzeit der Klausur muss vor der Endzeit der Klausur liegen")
  void test8() {
    //arrange
    KlausurDto klausur = new KlausurDto("x",
        LocalDate.of(2022, 3, 19).toString(),
        LocalTime.of(10, 30).toString(),
        LocalTime.of(8, 0).toString(),
        222916,
        true);

    //act
    boolean ergebnis = klausurValidierung.startzeitVorEndzeit(klausur);
    Set<String> fehler = klausurValidierung.getFehlgeschlagen();

    //assert
    assertThat(ergebnis).isFalse();
    assertThat(fehler).contains(KlausurFehler.STARTZEIT_VOR_ENDZEIT);
    assertThat(fehler).hasSize(1);
  }

  @Test
  @DisplayName("Die erstellte Klausur existiert bereits und wird somit nicht doppelt hinzugefügt")
  void test9() {
    //arrange
    List<Klausur> klausuren = Stream.of(KlausurDto.toKlausur(new KlausurDto("x",
        LocalDate.of(2022, 3, 19).toString(),
        LocalTime.of(8, 30).toString(),
        LocalTime.of(10, 0).toString(),
        222916,
        true))).collect(Collectors.toList());

    Klausur klausur = KlausurDto.toKlausur(new KlausurDto("x",
        LocalDate.of(2022, 3, 19).toString(),
        LocalTime.of(8, 30).toString(),
        LocalTime.of(10, 0).toString(),
        222916,
        true));

    //act
    boolean ergebnis = klausurValidierung.klausurLiegtInDb(klausuren, klausur);
    Set<String> fehler = klausurValidierung.getFehlgeschlagen();

    //assert
    assertThat(ergebnis).isTrue();
    assertThat(fehler).contains(KlausurFehler.KLAUSUR_LIEGT_In_DB);
    assertThat(fehler).hasSize(1);
  }

  @Test
  @DisplayName("Eine neue Klausur überschneidet sich mit einer aus der Liste der bereits bestehenden")
  void test10() {
    //arrange
    List<Klausur> klausuren = Stream.of(KlausurDto.toKlausur(new KlausurDto("x",
            LocalDate.of(2022, 3, 19).toString(),
            LocalTime.of(10, 30).toString(),
            LocalTime.of(12, 0).toString(),
            222916,
            true)),
        KlausurDto.toKlausur(new KlausurDto("y",
            LocalDate.of(2022, 3, 19).toString(),
            LocalTime.of(12, 30).toString(),
            LocalTime.of(13, 30).toString(),
            222916,
            true)))
        .collect(Collectors.toList());

    Klausur klausur = KlausurDto.toKlausur(new KlausurDto("z",
        LocalDate.of(2022, 3, 19).toString(),
        LocalTime.of(8, 30).toString(),
        LocalTime.of(11, 0).toString(),
        222916,
        true));

    //act
    boolean ergebnis = klausurValidierung.keineKlausurUeberschneidung(klausuren, klausur);
    Set<String> fehler = klausurValidierung.getFehlgeschlagen();

    //assert
    assertThat(ergebnis).isFalse();
    assertThat(fehler).contains(KlausurFehler.NEUE_KLAUSUR_SCHNEIDET_ALTE);
    assertThat(fehler).hasSize(1);
  }

  @Test
  @DisplayName("Zwei Klausuren überschneiden sich")
  void test11() {
    //arrange
    Klausur klausur = KlausurDto.toKlausur(new KlausurDto("zx",
        LocalDate.of(2022, 3, 19).toString(),
        LocalTime.of(8, 30).toString(),
        LocalTime.of(11, 0).toString(),
        222916,
        true));

    Klausur zweiteKlausur = KlausurDto.toKlausur(new KlausurDto("z",
        LocalDate.of(2022, 3, 19).toString(),
        LocalTime.of(10, 30).toString(),
        LocalTime.of(12, 0).toString(),
        222916,
        true));

    //act
    boolean ergebnis = klausurValidierung.pruefeUeberschneidung(klausur, zweiteKlausur);

    //assert
    assertThat(ergebnis).isTrue();
  }

  @Test
  @DisplayName("Zwei Klausuren überschneiden sich nicht, weil an verschiedenen Tagen")
  void test12() {
    //arrange
    Klausur klausur = KlausurDto.toKlausur(new KlausurDto("zx",
        LocalDate.of(2022, 3, 20).toString(),
        LocalTime.of(8, 30).toString(),
        LocalTime.of(11, 0).toString(),
        222916,
        true));

    Klausur zweiteKlausur = KlausurDto.toKlausur(new KlausurDto("z",
        LocalDate.of(2022, 3, 19).toString(),
        LocalTime.of(10, 30).toString(),
        LocalTime.of(12, 0).toString(),
        222916,
        true));

    //act
    boolean ergebnis = klausurValidierung.pruefeUeberschneidung(klausur, zweiteKlausur);
    Set<String> fehler = klausurValidierung.getFehlgeschlagen();

    //assert
    assertThat(ergebnis).isFalse();
    assertThat(fehler).isEmpty();
  }

  @Test
  @DisplayName("Eine neue Klausur beginnt genau zum Ende einer alten Klausur")
  void test17() {
    //arrange
    List<Klausur> klausuren = Stream.of(KlausurDto.toKlausur(new KlausurDto("x",
        LocalDate.of(2022, 3, 19).toString(),
        LocalTime.of(10, 30).toString(),
        LocalTime.of(12, 0).toString(),
        222916,
        true)))
        .collect(Collectors.toList());

    Klausur klausur = KlausurDto.toKlausur(new KlausurDto("z",
        LocalDate.of(2022, 3, 19).toString(),
        LocalTime.of(12, 0).toString(),
        LocalTime.of(14, 0).toString(),
        222916,
        true));

    //act
    boolean ergebnis = klausurValidierung.keineKlausurUeberschneidung(klausuren, klausur);
    Set<String> fehler = klausurValidierung.getFehlgeschlagen();

    //assert
    assertThat(ergebnis).isTrue();
    assertThat(fehler).isEmpty();
  }

  @Test
  @DisplayName("Zwei Klausuren liegen genau zur selben Zeit")
  void test13() {
    //arrange
    List<Klausur> klausuren = Stream.of(KlausurDto.toKlausur(new KlausurDto("x",
            LocalDate.of(2022, 3, 19).toString(),
            LocalTime.of(10, 30).toString(),
            LocalTime.of(12, 0).toString(),
            222916,
            true)),
        KlausurDto.toKlausur(new KlausurDto("y",
            LocalDate.of(2022, 3, 19).toString(),
            LocalTime.of(12, 30).toString(),
            LocalTime.of(13, 30).toString(),
            222916,
            true)))
        .collect(Collectors.toList());

    Klausur zweiteKlausur = KlausurDto.toKlausur(new KlausurDto("z",
        LocalDate.of(2022, 3, 19).toString(),
        LocalTime.of(10, 30).toString(),
        LocalTime.of(12, 0).toString(),
        222916,
        true));

    //act
    boolean ergebnis = klausurValidierung.keineKlausurUeberschneidung(klausuren, zweiteKlausur);
    Set<String> fehler = klausurValidierung.getFehlgeschlagen();

    //assert
    assertThat(ergebnis).isFalse();
    assertThat(fehler).contains(KlausurFehler.NEUE_KLAUSUR_SCHNEIDET_ALTE);
    assertThat(fehler).hasSize(1);
  }

  @Test
  @DisplayName("Eine Klausur ohne Name kann nicht angemeldet werden")
  void test14() throws IOException {
    //arrange
    KlausurDto klausur = new KlausurDto("",
        LocalDate.of(2022, 3, 19).toString(),
        LocalTime.of(10, 30).toString(),
        LocalTime.of(12, 0).toString(),
        222916,
        true);

    //act
    boolean ergebnis = klausurValidierung.lsfIdPasst(klausur);
    Set<String> fehler = klausurValidierung.getFehlgeschlagen();

    //assert
    assertThat(ergebnis).isFalse();
    assertThat(fehler).contains(KlausurFehler.NAME_NICHT_LEER);
    assertThat(fehler).hasSize(1);
  }

  @Test
  @DisplayName("Ein Urlaub kann nicht in der Vergangenheit storniert werden")
  void test67() {
    //arrange
    KlausurDto klausur = new KlausurDto("x",
        LocalDate.of(2002, 3, 19).toString(),
        LocalTime.of(10, 30).toString(),
        LocalTime.of(11, 0).toString(),
        222916,
        true);

    //act
    boolean ergebnis = klausurValidierung.klausurNurVorDemTagDerKlausurStornieren(klausur);
    Set<String> fehler = klausurValidierung.getFehlgeschlagen();

    //assert
    assertThat(ergebnis).isEqualTo(false);
    assertThat(fehler).contains(KlausurFehler.STORNIERUNG_RECHTZEITIG);
    assertThat(fehler).hasSize(1);
  }


  @Test
  @DisplayName("Klausur mit unvollständigem/falschem Name wird erkannt und der passende" +
      "Name zur LSF-ID wird vorgeschlagen")
  void test15() throws IOException {
    //arrange
    KlausurDto klausur = new KlausurDto("Betriebssysteme",
        LocalDate.of(2022, 3, 19).toString(),
        LocalTime.of(10, 30).toString(),
        LocalTime.of(12, 0).toString(),
        217480,
        true);

    //act
    boolean ergebnis = klausurValidierung.lsfIdPasst(klausur);
    Set<String> fehler = klausurValidierung.getFehlgeschlagen();

    //assert
    assertThat(ergebnis).isFalse();
    assertThat(fehler).contains("Der angegebene Veranstaltungsname ist ungültig." +
        " Der dazu bestehende Name ist Betriebssysteme und Systemprogrammierung");
    assertThat(fehler).hasSize(1);
  }

  @Test
  @DisplayName("Die Lsf ID ist ungültig")
  void test2() throws IOException {
    //arrange
    KlausurDto klausurDto = new KlausurDto("Betriebssysteme und Systemprogrammierung",
        LocalDate.of(2022, 3, 17).toString(),
        LocalTime.of(10, 30).toString(),
        LocalTime.of(11, 0).toString(),
        12,
        true);

    //act
    boolean ergebnis = klausurValidierung.lsfIdPasst(klausurDto);
    Set<String> fehler = klausurValidierung.getFehlgeschlagen();

    //assert
    assertThat(ergebnis).isFalse();
    assertThat(fehler).contains(KlausurFehler.UNGUELTIGE_LSFID);
    assertThat(fehler).hasSize(1);
  }

  @Test
  @DisplayName("Die Klausur ist valide")
  void test16() throws IOException {
    //arrange
    KlausurDto klausurDto = new KlausurDto("Betriebssysteme und Systemprogrammierung",
        LocalDate.of(2022, 3, 17).toString(),
        LocalTime.of(10, 30).toString(),
        LocalTime.of(11, 0).toString(),
        217480,
        true);

    //act
    boolean ergebnis = klausurValidierung.klausurIstValide(klausurDto);
    Set<String> fehler = klausurValidierung.getFehlgeschlagen();
    fehler.forEach(System.out::println);
    //assert
    assertThat(ergebnis).isTrue();
    assertThat(fehler).isEmpty();
  }




}
