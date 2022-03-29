package de.hhu.propra.application.services;

import de.hhu.propra.application.dto.KlausurDto;
import de.hhu.propra.application.dto.UrlaubDto;
import de.hhu.propra.application.fehler.KlausurFehler;
import de.hhu.propra.application.fehler.UrlaubFehler;
import de.hhu.propra.application.repositories.AuditLogRepository;
import de.hhu.propra.application.repositories.KlausurRepository;
import de.hhu.propra.application.repositories.StudentRepository;
import de.hhu.propra.domain.aggregates.klausur.Klausur;
import de.hhu.propra.domain.aggregates.student.Student;
import de.hhu.propra.domain.aggregates.student.Urlaub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class StudentServiceTests {


  private KlausurRepository klausurRepository;
  private StudentRepository studentRepository;
  private AuditLogRepository auditLogRepository;
  private StudentService studentService;

  @BeforeEach
  void vorbereiten() {
    this.klausurRepository = mock(KlausurRepository.class);
    this.studentRepository = mock(StudentRepository.class);
    this.auditLogRepository = mock(AuditLogRepository.class);
    this.studentService = new StudentService(studentRepository, klausurRepository,
        auditLogRepository);

  }

  @Test
  @DisplayName("Urlaub wird erfolgreich zu Student hinzugefügt")
  void test1() {
    //arrange
    UrlaubDto urlaub = new UrlaubDto(LocalDate.of(3000, 1, 1).toString(),
        LocalTime.of(10, 30).toString(),
        LocalTime.of(11, 30).toString());
    Student student = new Student(1L, "x");
    when(studentRepository.studentMitHandle("x")).thenReturn(student);

    //act
    Set<String> ergebnis = studentService.urlaubAnlegen(student.getHandle(), urlaub);

    //assert
    assertThat(student.getUrlaube()).hasSize(1);
    assertThat(ergebnis).isEmpty();
  }

  @Test
  @DisplayName("Ein Urlaub kann nicht zu Student hinzugefügt werden," +
      "weil er genau der selbe wie ein bereits existierender ist")
  void test2() {
    //arrange
    UrlaubDto urlaub = new UrlaubDto(LocalDate.of(3000, 1, 1).toString(),
        LocalTime.of(10, 30).toString(),
        LocalTime.of(11, 30).toString());
    Student student = new Student(1L, "x");
    when(studentRepository.studentMitHandle("x")).thenReturn(student);

    //act
    studentService.urlaubAnlegen(student.getHandle(), urlaub);
    Set<String> ergebnis = studentService.urlaubAnlegen(student.getHandle(), urlaub);

    //assert
    assertThat(student.getUrlaube()).hasSize(1);
    assertThat(ergebnis).contains(UrlaubFehler.ZWEI_URLAUBE_AN_TAG);
  }

  @Test
  @DisplayName("Save Methode wird beim hinzufuegen eines Urlaubes aufgerufen")
  void test3() {
    //arrange
    UrlaubDto urlaub = new UrlaubDto(LocalDate.of(3000, 1, 1).toString(),
        LocalTime.of(10, 30).toString(),
        LocalTime.of(11, 30).toString());
    Student student = new Student(1L, "x");
    when(studentRepository.studentMitHandle("x")).thenReturn(student);

    //act
    studentService.urlaubAnlegen(student.getHandle(), urlaub);

    //assert
    verify(studentRepository, Mockito.times(1)).save(student);

  }

  @Test
  @DisplayName("Beim invalidem Urlaub wird die Save Methode nicht aufgerufen")
  void test4() {
    //arrange
    UrlaubDto urlaub = new UrlaubDto(LocalDate.of(2000, 1, 1).toString(),
        LocalTime.of(10, 30).toString(),
        LocalTime.of(11, 30).toString());
    Student student = new Student(1L, "x");
    when(studentRepository.studentMitHandle("x")).thenReturn(student);

    //act
    studentService.urlaubAnlegen(student.getHandle(), urlaub);

    //assert
    verify(studentRepository, Mockito.times(0)).save(student);
  }

  @Test
  @DisplayName("Bei Hinzufuegen eines Urlaubs wird der Resturlaub angepasst")
  void test5() {
    //arrange
    UrlaubDto urlaub = new UrlaubDto(LocalDate.of(3000, 1, 1).toString(),
        LocalTime.of(10, 30).toString(),
        LocalTime.of(14, 30).toString());
    Student student = new Student(1L, "x");
    when(studentRepository.studentMitHandle("x")).thenReturn(student);

    //act
    studentService.urlaubAnlegen(student.getHandle(), urlaub);

    //assert
    assertThat(student.getResturlaub()).isEqualTo(0);
  }

  @Test
  @DisplayName("Beim ungenuegendem Resturlaub wird die Save Methode nicht aufgerufen")
  void test6() {
    //arrange
    UrlaubDto urlaub1 = new UrlaubDto(LocalDate.of(3000, 1, 1).toString(),
        LocalTime.of(10, 30).toString(),
        LocalTime.of(13, 30).toString());
    UrlaubDto urlaub2 = new UrlaubDto(LocalDate.of(3000, 1, 2).toString(),
        LocalTime.of(10, 30).toString(),
        LocalTime.of(12, 0).toString());
    Student student = new Student(1L, "x");
    when(studentRepository.studentMitHandle("x")).thenReturn(student);

    //act
    studentService.urlaubAnlegen(student.getHandle(), urlaub1);
    studentService.urlaubAnlegen(student.getHandle(), urlaub2);

    //assert
    verify(studentRepository, Mockito.times(1)).save(student);
  }


  @Test
  @DisplayName("Es werden zwei gueltige Urlaube an einem Tag hinzugefuegt")
  void test7() {
    //arrange
    UrlaubDto urlaub1 = new UrlaubDto(LocalDate.of(3000, 1, 1).toString(),
        LocalTime.of(8, 30).toString(),
        LocalTime.of(10, 30).toString());
    UrlaubDto urlaub2 = new UrlaubDto(LocalDate.of(3000, 1, 1).toString(),
        LocalTime.of(12, 0).toString(),
        LocalTime.of(12, 30).toString());
    Student student = new Student(1L, "x");
    when(studentRepository.studentMitHandle("x")).thenReturn(student);

    //act
    studentService.urlaubAnlegen(student.getHandle(), urlaub1);
    Set<String> ergebnis = studentService.urlaubAnlegen(student.getHandle(), urlaub2);

    //assert
    assertThat(student.getUrlaube()).hasSize(2);
    assertThat(ergebnis).isEmpty();
  }

  @Test
  @DisplayName("Bei zwei Urlauben an verschiedenen Tagen wird Resturlaub angepasst")
  void test8() {
    //arrange
    UrlaubDto urlaub1 = new UrlaubDto(LocalDate.of(3000, 1, 1).toString(),
        LocalTime.of(8, 30).toString(),
        LocalTime.of(10, 30).toString());
    UrlaubDto urlaub2 = new UrlaubDto(LocalDate.of(3000, 1, 2).toString(),
        LocalTime.of(11, 0).toString(),
        LocalTime.of(12, 30).toString());
    Student student = new Student(1L, "x");
    when(studentRepository.studentMitHandle("x")).thenReturn(student);

    //act
    studentService.urlaubAnlegen(student.getHandle(), urlaub1);
    studentService.urlaubAnlegen(student.getHandle(), urlaub2);

    //assert
    assertThat(student.getResturlaub()).isEqualTo(30);
  }

  @Test
  @DisplayName("Student hat nicht genug Resturlaub, somit wird Urlaub nicht hinzugefügt ")
  void test28() {
    //act
    Urlaub urlaub1 = new Urlaub(LocalDate.of(2020, 1, 1),
        LocalTime.of(8, 30),
        LocalTime.of(11, 30));
    Urlaub urlaub2 = new Urlaub(LocalDate.of(2020, 1, 2),
        LocalTime.of(8, 30),
        LocalTime.of(9, 45));
    Student student = new Student(1L, "x");
    student.addUrlaub(urlaub1.datum(), urlaub1.startzeit(), urlaub1.endzeit());
    student.berechneResturlaub();

    //arrange
    boolean hinzugefuegt = studentService.fuegeUrlaubHinzu(student, urlaub2);

    //assert
    assertThat(hinzugefuegt).isFalse();

  }

  @Test
  @DisplayName("Es werden zwei gueltige Urlaube an verschiedenen Tagen hinzugefuegt")
  void test9() {
    //arrange
    UrlaubDto urlaub1 = new UrlaubDto(LocalDate.of(3000, 1, 1).toString(),
        LocalTime.of(8, 30).toString(),
        LocalTime.of(10, 30).toString());
    UrlaubDto urlaub2 = new UrlaubDto(LocalDate.of(3000, 1, 2).toString(),
        LocalTime.of(11, 0).toString(),
        LocalTime.of(12, 30).toString());
    Student student = new Student(1L, "x");
    when(studentRepository.studentMitHandle("x")).thenReturn(student);

    //act
    studentService.urlaubAnlegen(student.getHandle(), urlaub1);
    Set<String> ergebnis = studentService.urlaubAnlegen(student.getHandle(), urlaub2);

    //assert
    assertThat(student.getUrlaube()).hasSize(2);
    assertThat(ergebnis).isEmpty();
  }

  @Test
  @DisplayName("Es werden zwei gueltige Urlaube an einem Tag und ein weiterer an einem anderen Tag hinzugefuegt")
  void test10() {
    //arrange
    UrlaubDto urlaub1 = new UrlaubDto(LocalDate.of(3000, 1, 1).toString(),
        LocalTime.of(8, 30).toString(),
        LocalTime.of(10, 0).toString());
    UrlaubDto urlaub2 = new UrlaubDto(LocalDate.of(3000, 1, 2).toString(),
        LocalTime.of(10, 0).toString(),
        LocalTime.of(12, 0).toString());
    UrlaubDto urlaub3 = new UrlaubDto(LocalDate.of(3000, 1, 1).toString(),
        LocalTime.of(12, 0).toString(),
        LocalTime.of(12, 30).toString());
    Student student = new Student(1L, "x");
    when(studentRepository.studentMitHandle("x")).thenReturn(student);

    //act
    studentService.urlaubAnlegen(student.getHandle(), urlaub1);
    studentService.urlaubAnlegen(student.getHandle(), urlaub2);
    Set<String> ergebnis = studentService.urlaubAnlegen(student.getHandle(), urlaub3);

    //assert
    assertThat(student.getUrlaube()).hasSize(3);
    assertThat(ergebnis).isEmpty();
  }

  @Test
  @DisplayName("Es werden maximal 2 Urlaube an einem Tag hinzugefuegt")
  void test11() {
    //arrange
    UrlaubDto urlaub1 = new UrlaubDto(LocalDate.of(3000, 1, 1).toString(),
        LocalTime.of(8, 30).toString(),
        LocalTime.of(8, 45).toString());
    UrlaubDto urlaub2 = new UrlaubDto(LocalDate.of(3000, 1, 1).toString(),
        LocalTime.of(12, 0).toString(),
        LocalTime.of(12, 30).toString());
    UrlaubDto urlaub3 = new UrlaubDto(LocalDate.of(3000, 1, 1).toString(),
        LocalTime.of(10, 15).toString(),
        LocalTime.of(10, 30).toString());
    Student student = new Student(1L, "x");
    when(studentRepository.studentMitHandle("x")).thenReturn(student);

    //act
    studentService.urlaubAnlegen(student.getHandle(), urlaub1);
    studentService.urlaubAnlegen(student.getHandle(), urlaub2);
    Set<String> ergebnis = studentService.urlaubAnlegen(student.getHandle(), urlaub3);

    //assert

    assertThat(student.getUrlaube()).hasSize(2);
    assertThat(ergebnis).contains(UrlaubFehler.MAX_ZWEI_URLAUBE);
  }

  @Test
  @DisplayName("Nach einer Stornierung wird der Resturlaub angepasst")
  void test12() {
    //arrange
    UrlaubDto urlaub1 = new UrlaubDto(LocalDate.of(3000, 1, 1).toString(),
        LocalTime.of(8, 30).toString(),
        LocalTime.of(10, 30).toString());
    UrlaubDto urlaub2 = new UrlaubDto(LocalDate.of(3000, 1, 1).toString(),
        LocalTime.of(12, 0).toString(),
        LocalTime.of(12, 30).toString());
    Student student = new Student(1L, "x");
    when(studentRepository.studentMitHandle("x")).thenReturn(student);

    //act
    studentService.urlaubAnlegen(student.getHandle(), urlaub1);
    studentService.urlaubAnlegen(student.getHandle(), urlaub2);
    studentService.urlaubStornieren(student.getHandle(), urlaub2);

    //assert
    assertThat(student.getResturlaub()).isEqualTo(120);
  }


  @Test
  @DisplayName("Es werden zwei gueltige Urlaube an einem Tag hinzugefuegt und einer davon storniert")
  void test13() {
    //arrange
    UrlaubDto urlaub1 = new UrlaubDto(LocalDate.of(3000, 1, 1).toString(),
        LocalTime.of(8, 30).toString(),
        LocalTime.of(10, 30).toString());
    UrlaubDto urlaub2 = new UrlaubDto(LocalDate.of(3000, 1, 1).toString(),
        LocalTime.of(12, 0).toString(),
        LocalTime.of(12, 30).toString());
    Student student = new Student(1L, "x");
    when(studentRepository.studentMitHandle("x")).thenReturn(student);

    //act
    studentService.urlaubAnlegen(student.getHandle(), urlaub1);
    studentService.urlaubAnlegen(student.getHandle(), urlaub2);
    Set<String> ergebnis = studentService.urlaubStornieren(student.getHandle(), urlaub2);

    //assert
    assertThat(student.getUrlaube()).hasSize(1);
    assertThat(ergebnis).isEmpty();
  }


  @Test
  @DisplayName("Urlaub wird bei Stornierungsantrag am selben Tag nicht storniert")
  void test14() {
    //arrange
    UrlaubDto urlaub1 = new UrlaubDto(LocalDate.now().toString(),
        LocalTime.of(8, 30).toString(),
        LocalTime.of(10, 30).toString());
    Student student = new Student(1L, "x");
    when(studentRepository.studentMitHandle("x")).thenReturn(student);
    Urlaub urlaub = UrlaubDto.toUrlaub(urlaub1);
    //act
    student.addUrlaub(urlaub.datum(), urlaub.startzeit(), urlaub.endzeit());
    Set<String> ergebnis = studentService.urlaubStornieren(student.getHandle(), urlaub1);

    //assert
    assertThat(student.getUrlaube()).hasSize(1);
    assertThat(ergebnis).isNotEmpty();
  }

  @Test
  @DisplayName("Wenn ein Urlaub nicht existiert, wird false beim Stornieren zurück geliefert")
  void test15() {
    UrlaubDto urlaub1 = new UrlaubDto(LocalDate.of(3000, 1, 1).toString(),
        LocalTime.of(8, 30).toString(),
        LocalTime.of(10, 30).toString());
    Student student = new Student(1L, "x");
    when(studentRepository.studentMitHandle("x")).thenReturn(student);

    //act
    Set<String> ergebnis = studentService.urlaubStornieren(student.getHandle(), urlaub1);

    //assert
    assertThat(ergebnis).isNotEmpty();
  }

  @Test
  @DisplayName("Nach einer Stornierung wird die save Methode aufgerufen")
  void test16() {
    //arrange
    Urlaub urlaub1 = new Urlaub(LocalDate.of(3000, 1, 1),
        LocalTime.of(8, 30),
        LocalTime.of(10, 30));
    UrlaubDto urlaub2 = new UrlaubDto(LocalDate.of(3000, 1, 1).toString(),
        LocalTime.of(12, 0).toString(),
        LocalTime.of(12, 30).toString());
    Student student = new Student(1L, "x");
    when(studentRepository.studentMitHandle("x")).thenReturn(student);

    //act
    student.addUrlaub(urlaub1.datum(), urlaub1.startzeit(), urlaub1.endzeit());
    student.addUrlaub(UrlaubDto.toUrlaub(urlaub2).datum(), UrlaubDto.toUrlaub(urlaub2).startzeit(),
        UrlaubDto.toUrlaub(urlaub2).endzeit());
    studentService.urlaubStornieren(student.getHandle(), urlaub2);

    //assert
    verify(studentRepository, Mockito.times(1)).save(student);
  }

  @Test
  @DisplayName("Urlaub wird bei Stornierungantrag am selben Tag nicht storniert")
  void test17() {
    //arrange
    Urlaub urlaub1 = new Urlaub(LocalDate.now(),
        LocalTime.of(8, 30),
        LocalTime.of(10, 30));
    Student student = new Student(1L, "x");
    when(studentRepository.studentMitHandle("x")).thenReturn(student);

    //act
    student.addUrlaub(urlaub1.datum(), urlaub1.startzeit(), urlaub1.endzeit());
    studentService.urlaubStornieren(student.getHandle(), new UrlaubDto(urlaub1.datum().toString(),
        urlaub1.startzeit().toString(),
        urlaub1.endzeit().toString()));

    //assert
    verify(studentRepository, Mockito.times(0)).save(student);
  }

  //Tests zu Klausuren

  @Test
  @DisplayName("Neue Klausur wird erstellt")
  void test18() throws IOException {
    //arrange
    KlausurDto klausurDto = new KlausurDto("Betriebssysteme und Systemprogrammierung",
        LocalDate.of(3000, 4, 22).toString(),
        LocalTime.of(9, 0).toString(),
        LocalTime.of(11, 0).plusMinutes(90).toString(),
        217480,
        true);
    Klausur klausur = new Klausur(1L,
        "Betriebssysteme und Systemprogrammierung",
        LocalDateTime.of(3000, 4, 22, 9, 0),
        120,
        217480,
        true);
    when(klausurRepository.alleKlausuren()).thenReturn(Collections.emptyList());
    when(klausurRepository.klausurMitDaten(klausurDto)).thenReturn(klausur);

    //act
    Set<String> ergebnis = studentService.klausurErstellen(null, klausurDto);

    //assert
    assertThat(ergebnis).isEmpty();
  }

  @Test
  @DisplayName("Student meldet sich für eine Klausur erfolgreich an")
  void test24() {
    //arrange
    Klausur klausur = new Klausur(1L,
        "Betriebssysteme und Systemprogrammierung",
        LocalDateTime.of(2020, 1, 1, 9, 30),
        60,
        217480,
        true); // bis 10:30

    Student student = new Student(1L, "x");

    when(studentRepository.studentMitHandle("x")).thenReturn(student);
    when(klausurRepository.klausurMitId(1L)).thenReturn(klausur);

    //act
    studentService.klausurAnmelden("x", 1L);

    //assert
    assertThat(student.getKlausuren()).contains(1L);
  }

  @Test
  @DisplayName("Keine Klausur Duplikate bei der Erstellung")
  void test19() throws IOException {
    //arrange
    KlausurDto klausurDto = new KlausurDto("Betriebssysteme und Systemprogrammierung",
        LocalDate.now().toString(),
        LocalTime.now().toString(),
        LocalTime.now().plusMinutes(90).toString(),
        217480,
        true);
    List<Klausur> klausurListe = new ArrayList<>();
    klausurListe.add(KlausurDto.toKlausur(klausurDto));
    when(klausurRepository.alleKlausuren()).thenReturn(klausurListe);

    //act
    Set<String> ergebnis = studentService.klausurErstellen(null, klausurDto);

    //assert
    assertThat(ergebnis).contains("Die Klausur ist schon vorhanden");
  }

  @Test
  @DisplayName("Bei zwei Klausuren an einem Tag wird eine richtig stoniert")
  void test27() {
    //arrange
    Klausur klausur1 = new Klausur(1L, "Betriebssysteme und Systemprogrammierung",
        LocalDateTime.now(),
        90,
        217480,
        true);
    Klausur klausur2 = new Klausur(2L, "Einführung in die Computerlinguistik",
        LocalDateTime.now(),
        90,
        222916,
        false);
    Student student = new Student(1L, "x");
    student.addKlausur(klausur1);
    student.addKlausur(klausur2);
    when(studentRepository.studentMitHandle("x")).thenReturn(student);

    //act
    studentService.klausurStornieren("x", klausur2);

    //assert
    assertThat(student.getKlausuren()).contains(1L);

  }

  @Test
  @DisplayName("Eine Klausur an einem Tag kann nicht erstellt werden, " +
      "wenn sie sich mit einer vorher erstellten Klausur überschneidet")
  void test20() throws IOException {
    //arrange
    KlausurDto klausurDto1 = new KlausurDto("Einführung in die Computerlinguistik",
        LocalDateTime.of(2022, 3, 22, 9, 30).toLocalDate().toString(),
        LocalDateTime.of(2022, 3, 22, 9, 30).toLocalTime().toString(),
        LocalDateTime.of(2022, 3, 22, 9, 30).toLocalTime().plusMinutes(90).toString(),
        222916,
        true);     //9:30 - 11:00
    KlausurDto klausurDto2 = new KlausurDto("Betriebssysteme und Systemprogrammierung",
        LocalDateTime.of(2022, 3, 22, 10, 0).toLocalDate().toString(),
        LocalDateTime.of(2022, 3, 22, 10, 0).toLocalTime().toString(),
        LocalDateTime.of(2022, 3, 22, 11, 30).toLocalTime().plusMinutes(90).toString(),
        217480,
        false);    //10:00 - 11:30
    Klausur klausur = new Klausur(1L, "Betriebssysteme und Systemprogrammierung",
        LocalDateTime.of(2022, 3, 22, 10, 0),
        90,
        217480,
        false);
    List<Klausur> klausurListe = new ArrayList<>();
    klausurListe.add(KlausurDto.toKlausur(klausurDto1));
    when(klausurRepository.alleKlausuren()).thenReturn(klausurListe);
    when(klausurRepository.klausurMitDaten(klausurDto2)).thenReturn(klausur);
    //act
    Set<String> ergebnis = studentService.klausurErstellen(null, klausurDto2);

    //assert
    assertThat(ergebnis).isEmpty();
  }

  @Test
  @DisplayName("Eine Klausur an einem Tag kann nicht zu einem Student hinzugefügt werden, " +
      "wenn sie sich mit einer vorher hinzugefügten Klausur überschneidet")
  void test29() {
    //arrange
    Klausur klausur1 = new Klausur(1L, "Betriebssysteme und Systemprogrammierung",
        LocalDateTime.of(2022, 3, 22, 9, 30),
        90,
        217480,
        true); // 09:00 - 11:00
    Klausur klausur2 = new Klausur(2L, "Einführung in die Computerlinguistik",
        LocalDateTime.of(2022, 3, 22, 8, 30),
        90,
        222916,
        true); // 08:00 - 10:00
    Student student = new Student(1L, "x");
    student.addKlausur(klausur1);

    when(studentRepository.studentMitHandle("x")).thenReturn(student);
    when(klausurRepository.alleKlausuren()).thenReturn(List.of(klausur1, klausur2));
    when(klausurRepository.klausurMitId(1L)).thenReturn(klausur1);
    when(klausurRepository.klausurMitId(2L)).thenReturn(klausur2);

    //act
    Set<String> ergebnis = studentService.klausurAnmelden("x", klausur2.id());

    //assert
    assertThat(ergebnis).contains(KlausurFehler.NEUE_KLAUSUR_SCHNEIDET_ALTE);

  }

  @Test
  @DisplayName("Eine Klausur an einem Tag kann nicht zu einem Student hinzugefügt werden, " +
      "wenn sie sich mit einer vorher hinzugefügten Klausur genau deckt")
  void test30() {
    //arrange
    Klausur klausur1 = new Klausur(1L, "Betriebssysteme und Systemprogrammierung",
        LocalDateTime.of(3000, 3, 22, 9, 30),
        90,
        217480,
        true); // 09:00 - 11:00
    Klausur klausur2 = new Klausur(2L, "Einführung in die Computerlinguistik",
        LocalDateTime.of(3000, 3, 22, 9, 30),
        90,
        222916,
        true); // 09:00 - 11:00
    Student student = new Student(1L, "x");
    student.addKlausur(klausur1);

    when(studentRepository.studentMitHandle("x")).thenReturn(student);
    when(klausurRepository.alleKlausuren()).thenReturn(List.of(klausur1, klausur2));
    when(klausurRepository.klausurMitId(1L)).thenReturn(klausur1);
    when(klausurRepository.klausurMitId(2L)).thenReturn(klausur2);

    //act
    Set<String> ergebnis = studentService.klausurAnmelden("x", klausur2.id());

    //assert
    assertThat(ergebnis).contains(KlausurFehler.NEUE_KLAUSUR_SCHNEIDET_ALTE);

  }

  //Tests zu Urlauben und Klausuren kombiniert

  @Test
  @DisplayName("Student beantragt Urlaub am ganzen Tag an dem er Klausur hat")
  void test21() {
    //arrange
    UrlaubDto dto = new UrlaubDto(LocalDate.of(2024, 1, 1).toString(),
        LocalTime.of(8, 30).toString(),
        LocalTime.of(12, 30).toString());
    Klausur klausur = new Klausur(1L,
        "Betriebssysteme und Systemprogrammierung",
        LocalDateTime.of(2024, 1, 1, 10, 0),
        60,
        217480,
        true);
    Student student = new Student(1L, "x");
    student.addKlausur(klausur);
    when(studentRepository.studentMitHandle("x")).thenReturn(student);
    when(klausurRepository.klausurMitId(1L)).thenReturn(klausur);
    Urlaub ergebnis1 = new Urlaub(LocalDate.of(2024, 1, 1),
        LocalTime.of(8, 30),
        LocalTime.of(9, 30));

    Urlaub ergebnis2 = new Urlaub(LocalDate.of(2024, 1, 1),
        LocalTime.of(11, 0),
        LocalTime.of(12, 30));

    //act
    studentService.urlaubAnlegen("x", dto);

    //assert
    assertThat(student.getUrlaube()).contains(ergebnis1, ergebnis2);
    assertThat(student.getResturlaub()).isEqualTo(90);

  }

  @Test
  @DisplayName("Student beantragt Urlaub am ganzen Tag an dem er 2 Klausuren hat")
  void test22() {
    //arrange
    UrlaubDto dto = new UrlaubDto(LocalDate.of(2024, 1, 1).toString(),
        LocalTime.of(8, 30).toString(),
        LocalTime.of(12, 30).toString());
    Klausur klausur = new Klausur(1L,
        "Betriebssysteme und Systemprogrammierung",
        LocalDateTime.of(2024, 1, 1, 9, 30),
        60,
        217480,
        true); // bis 10:30
    Klausur klausur1 = new Klausur(2L,
        "Grundlagen der Computernetzwerke (vormals: Rechnernetze)",
        LocalDateTime.of(2024, 1, 1, 11, 30),
        30,
        219478,
        true);
    Student student = new Student(1L, "x");
    student.addKlausur(klausur);
    student.addKlausur(klausur1);
    when(studentRepository.studentMitHandle("x")).thenReturn(student);
    when(klausurRepository.klausurMitId(1L)).thenReturn(klausur);
    when(klausurRepository.klausurMitId(2L)).thenReturn(klausur1);
    Urlaub ergebnis1 = new Urlaub(LocalDate.of(2024, 1, 1),
        LocalTime.of(8, 30),
        LocalTime.of(9, 0));
    Urlaub ergebnis2 = new Urlaub(LocalDate.of(2024, 1, 1),
        LocalTime.of(10, 30),
        LocalTime.of(11, 0));
    Urlaub ergebnis3 = new Urlaub(LocalDate.of(2024, 1, 1),
        LocalTime.of(12, 0),
        LocalTime.of(12, 30));

    //act
    studentService.urlaubAnlegen("x", dto);

    //assert
    assertThat(student.getUrlaube()).contains(ergebnis1, ergebnis2, ergebnis3);
    assertThat(student.getResturlaub()).isEqualTo(150);

  }

  @Test
  @DisplayName("Student beantragt Urlaub und hat am Tag bereits Urlaub und eine Klausur")
  void test23() {
    //arrange
    UrlaubDto dto = new UrlaubDto(LocalDate.of(2024, 1, 1).toString(),
        LocalTime.of(10, 0).toString(),
        LocalTime.of(12, 30).toString());
    UrlaubDto dto2 = new UrlaubDto(LocalDate.of(2024, 1, 1).toString(),
        LocalTime.of(8, 30).toString(),
        LocalTime.of(9, 0).toString());

    Klausur klausur = new Klausur(1L,
        "Betriebssysteme und Systemprogrammierung",
        LocalDateTime.of(2024, 1, 1, 9, 30),
        60,
        217480,
        true); // bis 10:30

    Student student = new Student(1L, "x");
    student.addKlausur(klausur);

    when(studentRepository.studentMitHandle("x")).thenReturn(student);
    when(klausurRepository.klausurMitId(1L)).thenReturn(klausur);
    Urlaub ergebnis1 = new Urlaub(LocalDate.of(2024, 1, 1),
        LocalTime.of(8, 30),
        LocalTime.of(9, 0));
    Urlaub ergebnis2 = new Urlaub(LocalDate.of(2024, 1, 1),
        LocalTime.of(10, 30),
        LocalTime.of(12, 30));

    //act
    studentService.urlaubAnlegen("x", dto2);
    studentService.urlaubAnlegen("x", dto);

    //assert
    assertThat(student.getUrlaube()).contains(ergebnis1, ergebnis2);
    assertThat(student.getResturlaub()).isEqualTo(90);
  }


  @Test
  @DisplayName("Der Student hat an einem Tag Urlaub und will genau zu dieser Zeit eine Klausur anmelden")
  void test25() {
    //arrange
    Urlaub dto = new Urlaub(LocalDate.of(2020, 1, 1),
        LocalTime.of(8, 30),
        LocalTime.of(12, 30));

    Klausur klausur = new Klausur(1L,
        "Betriebssysteme und Systemprogrammierung",
        LocalDateTime.of(2020, 1, 1, 9, 30),
        60,
        217480,
        true); // bis 10:30

    Student student = new Student(1L, "x");
    student.addUrlaub(dto.datum(), dto.startzeit(), dto.endzeit());
    when(studentRepository.studentMitHandle("x")).thenReturn(student);
    when(klausurRepository.klausurMitId(1L)).thenReturn(klausur);
    Urlaub ergebnis1 = new Urlaub(LocalDate.of(2020, 1, 1),
        LocalTime.of(8, 30),
        LocalTime.of(9, 0));
    Urlaub ergebnis2 = new Urlaub(LocalDate.of(2020, 1, 1),
        LocalTime.of(10, 30),
        LocalTime.of(12, 30));

    //act
    studentService.klausurAnmelden("x", 1L);

    //assert
    assertThat(student.getUrlaube()).contains(ergebnis1, ergebnis2);
    assertThat(student.getResturlaub()).isEqualTo(90);
  }

  @Test
  @DisplayName("Der Student hat an einem Tag Urlaub und will genau zu dieser Zeit zwei Klausuren hintereinander anmelden")
  void test26() {
    //arrange
    Urlaub dto = new Urlaub(LocalDate.of(2020, 1, 1),
        LocalTime.of(8, 30),
        LocalTime.of(12, 30));

    Klausur klausur = new Klausur(1L,
        "Betriebssysteme und Systemprogrammierung",
        LocalDateTime.of(2020, 1, 1, 9, 30),
        60,
        217480,
        true); //9:00 bis 10:30
    Klausur klausur1 = new Klausur(2L,
        "Grundlagen der Computernetzwerke (vormals: Rechnernetze)",
        LocalDateTime.of(2020, 1, 1, 11, 30),
        30,
        219478,
        true); //11:00 bis 12:00

    Student student = new Student(1L, "x");
    student.addUrlaub(dto.datum(), dto.startzeit(), dto.endzeit());
    when(studentRepository.studentMitHandle("x")).thenReturn(student);
    when(klausurRepository.klausurMitId(1L)).thenReturn(klausur);
    when(klausurRepository.klausurMitId(2L)).thenReturn(klausur1);
    Urlaub ergebnis1 = new Urlaub(LocalDate.of(2020, 1, 1),
        LocalTime.of(8, 30),
        LocalTime.of(9, 0));
    Urlaub ergebnis2 = new Urlaub(LocalDate.of(2020, 1, 1),
        LocalTime.of(10, 30),
        LocalTime.of(11, 0));
    Urlaub ergebnis3 = new Urlaub(LocalDate.of(2020, 1, 1),
        LocalTime.of(12, 0),
        LocalTime.of(12, 30));

    //act
    studentService.klausurAnmelden("x", 1L);
    studentService.klausurAnmelden("x", 2L);

    //assert
    assertThat(student.getUrlaube()).contains(ergebnis1, ergebnis2, ergebnis3);
    assertThat(student.getResturlaub()).isEqualTo(150);
  }


  @Test
  @DisplayName("KlausurDto Verknüpfung mit KlausurId funktioniert")
  public void test31() {
    //arrange
    Klausur klausur = new Klausur(1L,
        "Betriebssysteme und Systemprogrammierung",
        LocalDateTime.of(2020, 1, 1, 9, 30),
        60,
        217480,
        true); // bis 10:30
    Klausur klausur1 = new Klausur(2L,
        "Betriebssysteme und Systemprogrammierung",
        LocalDateTime.of(2020, 1, 1, 9, 30),
        60,
        217480,
        true); // bis 10:30
    Student student = new Student(1L, "x");
    student.addKlausur(klausur);
    student.addKlausur(klausur1);
    when(klausurRepository.klausurMitId(1L)).thenReturn(klausur);
    when(klausurRepository.klausurMitId(2L)).thenReturn(klausur1);

    //act
    HashMap<Long, KlausurDto> map = studentService.holealleklausurdtosmitid(student);

    //assert
    assertThat(map.keySet()).containsExactly(1L, 2L);
    assertThat(map.values())
        .containsExactly(KlausurDto.toKlausurDto(klausur), KlausurDto.toKlausurDto(klausur1));
  }


}
