package de.hhu.propra.application.utils;

import de.hhu.propra.domain.aggregates.klausur.Klausur;
import de.hhu.propra.domain.aggregates.student.Urlaub;
import de.hhu.propra.domain.auditlog.AuditLog;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import de.hhu.propra.application.dto.UrlaubDto;

import static org.assertj.core.api.Assertions.assertThat;

public class AuditLogErzeugungsTests {

  @Test
  @DisplayName("Der AuditLog wird beim Erstellen eines Urlaubs von einem Student richtig erzeugt")
  void test1() {
    //arrange
    Urlaub urlaub = new Urlaub(LocalDate.of(2000, 2, 2),
        LocalTime.of(10, 0),
        LocalTime.of(11, 0));

    //act
    AuditLog auditLog = AuditLogErzeugung.urlaubHinzugefuegt("Gustav", urlaub);
    AuditLog erwartet = new AuditLog(
        "Urlaub{datum=2000-02-02, startzeit=10:00, endzeit=11:00} hinzugefuegt", "Gustav");
    erwartet.setZeitpunkt(auditLog.getZeitpunkt());
    //assert
    assertThat(auditLog).isEqualTo(erwartet);
  }

  @Test
  @DisplayName("Der AuditLog wird beim Stornieren eines Urlaubs von einem Student richtig erzeugt")
  void test2() {
    //arrange
    UrlaubDto urlaub = UrlaubDto.toUrlaubDto(new Urlaub(LocalDate.of(2000, 2, 2),
        LocalTime.of(10, 0),
        LocalTime.of(11, 0)));

    //act
    AuditLog auditLog = AuditLogErzeugung.urlaubStorniert("Gustav", urlaub);
    AuditLog erwartet = new AuditLog(
        "Urlaub{datum=2000-02-02, startzeit=10:00, endzeit=11:00} storniert", "Gustav");
    erwartet.setZeitpunkt(auditLog.getZeitpunkt());
    //assert
    assertThat(auditLog).isEqualTo(erwartet);
  }

  @Test
  @DisplayName("Der AuditLog wird beim Erstellen einer Klausur von einem Student richtig erzeugt")
  void test3() {
    //arrange
    Klausur klausur = new Klausur(123L,
        "Mathe",
        LocalDateTime.of(2022, 2, 2, 9, 0),
        90,
        12345,
        true);
    //act

    AuditLog auditLog = AuditLogErzeugung.klausurErstellen("Gustav", klausur);
    AuditLog erwartet = new AuditLog("Klausur{id=123, name=Mathe} erstellt", "Gustav");
    erwartet.setZeitpunkt(auditLog.getZeitpunkt());
    //assert
    assertThat(auditLog).isEqualTo(erwartet);
  }

  @Test
  @DisplayName("Der AuditLog wird bei der Anmeldung einer Klausur von einem Student richtig erzeugt")
  void test4() {
    //arrange
    Klausur klausur = new Klausur(123L,
        "Mathe",
        LocalDateTime.of(2022, 2, 2, 9, 0),
        90,
        12345,
        true);
    //act

    AuditLog auditLog = AuditLogErzeugung.klausurAnmelden("Gustav", klausur);
    AuditLog erwartet = new AuditLog("Klausur{id=123, name=Mathe} angemeldet", "Gustav");
    erwartet.setZeitpunkt(auditLog.getZeitpunkt());
    //assert
    assertThat(auditLog).isEqualTo(erwartet);
  }

  @Test
  @DisplayName("Der AuditLog wird bei der Stornierung einer Klausur von einem Student richtig erzeugt")
  void test5() {
    //arrange
    Klausur klausur = new Klausur(123L,
        "Mathe",
        LocalDateTime.of(2022, 2, 2, 9, 0),
        90,
        12345,
        true);
    //act

    AuditLog auditLog = AuditLogErzeugung.klausurStorniert("Gustav", klausur);
    AuditLog erwartet = new AuditLog("Klausur{id=123, name=Mathe} storniert", "Gustav");
    erwartet.setZeitpunkt(auditLog.getZeitpunkt());
    //assert
    assertThat(auditLog).isEqualTo(erwartet);
  }


}
