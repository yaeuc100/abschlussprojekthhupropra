package de.hhu.propra.database.repository_implementation;

import de.hhu.propra.database.dao.StudentDao;
import de.hhu.propra.domain.aggregates.klausur.Klausur;
import de.hhu.propra.domain.aggregates.student.KlausurReferenz;
import de.hhu.propra.domain.aggregates.student.Student;
import de.hhu.propra.domain.aggregates.student.Urlaub;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJdbcTest
@ActiveProfiles("test")
public class StudentRepositoryImplTests {

  @Autowired
  StudentDao studentDao;

  @Autowired
  JdbcTemplate db;

  @Test
  @DisplayName("Richtiger Student wird mit ID aus DB gelesen")
  @Sql({"classpath:db/migration/V1__init.sql",
      "classpath:db/migration/loadtest.sql"})
  void test1() {
    //arrange
    StudentRepositoryImpl studentRepository = new StudentRepositoryImpl(studentDao, db);
    Student olli = new Student(1L, "olli");
    olli.setResturlaub(0);

    //act
    Student olliAusDb = studentRepository.studentMitId(1L);

    //assert
    assertThat(olliAusDb).isEqualTo(olli);
  }

  @Test
  @DisplayName("Richtiger Student wird mit ID aus DB gelesen mit Urlauben")
  @Sql({"classpath:db/migration/V1__init.sql",
      "classpath:db/migration/loadtest.sql"})
  void test2() {
    //arrange
    StudentRepositoryImpl studentRepository = new StudentRepositoryImpl(studentDao, db);
    Urlaub urlaub1 = new Urlaub(LocalDate.of(2021, 1, 1),
        LocalTime.of(8, 30),
        LocalTime.of(10, 30));
    Urlaub urlaub2 = new Urlaub(LocalDate.of(2021, 1, 1),
        LocalTime.of(10, 30),
        LocalTime.of(12, 30));

    Student olli = new Student(1L, "olli");
    olli.setResturlaub(0);
    olli.addUrlaub(urlaub1.datum(), urlaub1.startzeit(), urlaub1.endzeit());
    olli.addUrlaub(urlaub2.datum(), urlaub2.startzeit(), urlaub2.endzeit());

    //act
    Student olliAusDb = studentRepository.studentMitId(1L);

    //assert
    assertThat(olliAusDb).isEqualTo(olli);
    assertThat(olliAusDb.getUrlaube()).contains(urlaub1, urlaub2);
  }

  @Test
  @DisplayName("Richtiger Student mit ID wird aus DB gelesen und ein Urlaub storniert")
  @Sql({"classpath:db/migration/V1__init.sql",
      "classpath:db/migration/loadtest.sql"})
  void test3() {
    //arrange
    StudentRepositoryImpl studentRepository = new StudentRepositoryImpl(studentDao, db);
    Urlaub urlaub1 = new Urlaub(LocalDate.of(2021, 1, 1),
        LocalTime.of(8, 30),
        LocalTime.of(10, 30));
    Urlaub urlaub2 = new Urlaub(LocalDate.of(2021, 1, 1),
        LocalTime.of(10, 30),
        LocalTime.of(12, 30));

    Student olli = new Student(1L, "olli");
    // olli.setResturlaub(0);
    olli.addUrlaub(urlaub1.datum(), urlaub1.startzeit(), urlaub1.endzeit());

    //act
    studentRepository.storniereUrlaub(1L, urlaub2);
    Student olliAusDb = studentRepository.studentMitId(1L);

    //assert
    assertThat(olliAusDb).isEqualTo(olli);
    assertThat(olliAusDb.getUrlaube()).containsExactly(urlaub1);
  }


  @Test
  @DisplayName("Alle Studenten werden aus DB geholt")
  @Sql({"classpath:db/migration/V1__init.sql",
      "classpath:db/migration/loadtest.sql"})
  void test4() {
    //arrange
    StudentRepositoryImpl studentRepository = new StudentRepositoryImpl(studentDao, db);

    //act
    List<Student> studenten = studentRepository.alleStudenten();

    //asset
    assertThat(studenten).hasSize(5);
  }


  @Test
  @DisplayName("Student wird richtig in DB gespeichert")
  @Sql({"classpath:db/migration/V1__init.sql",
      "classpath:db/migration/loadtest.sql"})
  void test5() {
    //arrange
    StudentRepositoryImpl studentRepository = new StudentRepositoryImpl(studentDao, db);
    Urlaub urlaub1 = new Urlaub(LocalDate.of(2021, 1, 1),
        LocalTime.of(5, 30),
        LocalTime.of(6, 15));
    Urlaub urlaub2 = new Urlaub(LocalDate.of(2021, 1, 1),
        LocalTime.of(3, 30),
        LocalTime.of(4, 30));
    Student student = new Student(null, "jens");
    student.setResturlaub(0);
    student.addUrlaub(urlaub1.datum(), urlaub1.startzeit(), urlaub1.endzeit());
    student.addUrlaub(urlaub2.datum(), urlaub2.startzeit(), urlaub2.endzeit());
    Student erwartet = new Student(6L, "jens");
    erwartet.setResturlaub(0);
    erwartet.addUrlaub(urlaub1.datum(), urlaub1.startzeit(), urlaub1.endzeit());
    erwartet.addUrlaub(urlaub2.datum(), urlaub2.startzeit(), urlaub2.endzeit());

    //act
    studentRepository.save(student);

    //assert
    assertThat(studentRepository.studentMitId(6L)).isEqualTo(erwartet);
    assertThat(studentRepository.studentMitId(6L).getUrlaube()).contains(urlaub1, urlaub2);
  }

  @Test
  @DisplayName("Richtiger Student wird mit ID aus DB gelesen und hat Klausur")
  @Sql({"classpath:db/migration/V1__init.sql",
      "classpath:db/migration/loadtest.sql"})
  void test6() {
    //arrange
    StudentRepositoryImpl studentRepository = new StudentRepositoryImpl(studentDao, db);

    Klausur klausur = new Klausur(1L,
        "Rechnernetze",
        LocalDateTime.of(2020, 01, 01, 10, 00),
        60, 123456, true);

    Student sabrina = new Student(5L, "Sabrina");
    sabrina.setResturlaub(240);
    sabrina.addKlausur(klausur);

    //act
    Student sabrinaAusDb = studentRepository.studentMitId(5L);

    //assert
    assertThat(sabrinaAusDb).isEqualTo(sabrina);
    assertThat(sabrinaAusDb.getKlausuren()).contains(1L);
  }

  @Test
  @DisplayName("Richtiger Student wird mit ID aus DB gelesen mit Klausuren und Urlauben ")
  @Sql({"classpath:db/migration/V1__init.sql",
      "classpath:db/migration/loadtest.sql"})
  void test7() {
    //arrange
    StudentRepositoryImpl studentRepository = new StudentRepositoryImpl(studentDao, db);
    Urlaub urlaub1 = new Urlaub(LocalDate.of(2021, 1, 1),
        LocalTime.of(8, 30),
        LocalTime.of(10, 30));
    Urlaub urlaub2 = new Urlaub(LocalDate.of(2021, 1, 1),
        LocalTime.of(10, 30),
        LocalTime.of(12, 30));

    Student olli = new Student(1L, "olli");
    olli.setResturlaub(0);
    olli.addKlausurRef(new KlausurReferenz(2L));
    olli.addKlausurRef(new KlausurReferenz(3L));
    olli.addUrlaub(urlaub1.datum(), urlaub1.startzeit(), urlaub1.endzeit());
    olli.addUrlaub(urlaub2.datum(), urlaub2.startzeit(), urlaub2.endzeit());

    //act
    Student olliAusDb = studentRepository.studentMitId(1L);

    //assert
    assertThat(olliAusDb).isEqualTo(olli);
    assertThat(olliAusDb.getKlausuren()).contains(2L, 3L);
    assertThat(olliAusDb.getUrlaube()).contains(urlaub1, urlaub2);
  }

  @Test
  @DisplayName("Richtiger Student wird mit Handle aus DB gelesen und hat Klausuren und Urlaube ")
  @Sql({"classpath:db/migration/V1__init.sql",
      "classpath:db/migration/loadtest.sql"})
  void test8() {
    //arrange
    StudentRepositoryImpl studentRepository = new StudentRepositoryImpl(studentDao, db);
    Urlaub urlaub1 = new Urlaub(LocalDate.of(2021, 1, 1),
        LocalTime.of(8, 30),
        LocalTime.of(10, 30));
    Urlaub urlaub2 = new Urlaub(LocalDate.of(2021, 1, 1),
        LocalTime.of(10, 30),
        LocalTime.of(12, 30));

    Student olli = new Student(1L, "olli");
    olli.setResturlaub(0);
    olli.addKlausurRef(new KlausurReferenz(2L));
    olli.addKlausurRef(new KlausurReferenz(3L));
    olli.addUrlaub(urlaub1.datum(), urlaub1.startzeit(), urlaub1.endzeit());
    olli.addUrlaub(urlaub2.datum(), urlaub2.startzeit(), urlaub2.endzeit());

    //act
    Student olliAusDb = studentRepository.studentMitHandle("olli");

    //assert
    assertThat(olliAusDb).isEqualTo(olli);
    assertThat(olliAusDb.getKlausuren()).contains(2L, 3L);
    assertThat(olliAusDb.getUrlaube()).contains(urlaub1, urlaub2);
  }

}
