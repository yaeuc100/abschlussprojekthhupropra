package de.hhu.propra.application.utils;

import de.hhu.propra.application.dto.KlausurDto;
import de.hhu.propra.domain.aggregates.klausur.Klausur;
import de.hhu.propra.domain.aggregates.student.Student;
import de.hhu.propra.domain.aggregates.student.Urlaub;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class StudentServiceHilfsmethodenTests {

    StudentServiceHilfsMethoden studentServiceHilfsMethoden = new StudentServiceHilfsMethoden();
    LocalDate datum = LocalDate.of(2022,3,20);
    private Urlaub urlaub1 = new Urlaub(LocalDate.of(2022, 10, 10), LocalTime.of(9, 30), LocalTime.of(10, 30));
    private Urlaub urlaub2 = new Urlaub(LocalDate.of(2022, 10, 10), LocalTime.of(12, 30), LocalTime.of(13, 0));
    private Urlaub urlaub3 = new Urlaub(LocalDate.of(2022, 10, 11), LocalTime.of(9, 30), LocalTime.of(10, 30));
    private Urlaub urlaub4 = new Urlaub(LocalDate.of(2022, 9, 10), LocalTime.of(10, 30), LocalTime.of(11, 30));


    @Test
    @DisplayName("Zwei Klausuren an einem bestimmten Tag werden zurückgegeben")
    void test1(){
        //arrange
        List<Klausur> klausuren =  new ArrayList<>();
        klausuren.add(KlausurDto.toKlausur(new KlausurDto("x" ,
                LocalDate.of(2022,3,19).toString(),
                LocalTime.of(10,30).toString(),
                LocalTime.of(12,0).toString(),
                222916,
                true)));
        Klausur klausur = KlausurDto.toKlausur(new KlausurDto("y" ,
                LocalDate.of(2022,3,20).toString(),
                LocalTime.of(8,30).toString(),
                LocalTime.of(9,30).toString(),
                222916,
                true));
        Klausur klausur2 = KlausurDto.toKlausur(new KlausurDto("z" ,
                LocalDate.of(2022,3,20).toString(),
                LocalTime.of(12,30).toString(),
                LocalTime.of(13,30).toString(),
                222916,
                true));
                klausuren.add(klausur);
                klausuren.add(klausur2);

        //act
        List<Klausur> klausurenAnTag = studentServiceHilfsMethoden.studentHatKlausurAnTag(klausuren, datum);

        //assert
        assertThat(klausurenAnTag).contains(klausur,klausur2);
        assertThat(klausurenAnTag).hasSize(2);

    }

    @Test
    @DisplayName("Urlaube an einem Tag werden korrekt zurückgeliefert")
    void test2(){
        //arrange
        Student student = new Student(1L, "X");
        student.addUrlaub(urlaub1.datum(), urlaub1.startzeit(), urlaub1.endzeit());
        student.addUrlaub(urlaub2.datum(), urlaub2.startzeit(), urlaub2.endzeit());
        student.addUrlaub(urlaub3.datum(), urlaub3.startzeit(), urlaub3.endzeit());
        student.addUrlaub(urlaub4.datum(), urlaub4.startzeit(), urlaub4.endzeit());

        //act
        List<Urlaub> ergebnis = studentServiceHilfsMethoden.findeUrlaubeAmSelbenTag(student, urlaub1.datum());

        //assert
        assertThat(ergebnis).hasSize(2);
        assertThat(ergebnis).contains(urlaub1, urlaub2);
    }

    @Test
    @DisplayName("Urlaube an einem Tag werden korrekt storniert")
    void test3(){
        //arrange
        Student student = new Student(1L, "X");
        student.addUrlaub(urlaub1.datum(), urlaub1.startzeit(), urlaub1.endzeit());
        student.addUrlaub(urlaub2.datum(), urlaub2.startzeit(), urlaub2.endzeit());
        student.addUrlaub(urlaub3.datum(), urlaub3.startzeit(), urlaub3.endzeit());
        student.addUrlaub(urlaub4.datum(), urlaub4.startzeit(), urlaub4.endzeit());

        //act
        studentServiceHilfsMethoden.storniereAlleUrlaubeAnTag(student, urlaub1.datum());
        List<Urlaub> urlaube = student.getUrlaube();

        //assert
        assertThat(urlaube).hasSize(2);
        assertThat(urlaube).contains(urlaub3, urlaub4);
    }

    @Test
    @DisplayName("Stornierte Urlaube werden richtig zurückgegeben")
    void test4(){
        //arrange
        List<Urlaub> alte = List.of(urlaub1, urlaub2, urlaub3, urlaub4).stream().collect(Collectors.toList());
        List<Urlaub> neue = List.of(urlaub3, urlaub4).stream().collect(Collectors.toList());

        //act
        List<Urlaub> ergebnis = studentServiceHilfsMethoden.stornierteUrlaube(alte, neue);

        //assert
        assertThat(ergebnis).hasSize(2);
        assertThat(ergebnis).contains(urlaub1, urlaub2);
    }

    @Test
    @DisplayName("Neue Urlaube werden richtig zurückgegeben")
    void test5(){
        //arrange
        List<Urlaub> alte = List.of(urlaub3, urlaub4).stream().collect(Collectors.toList());
        List<Urlaub> neue = List.of(urlaub1, urlaub2, urlaub3, urlaub4).stream().collect(Collectors.toList());

        //act
        List<Urlaub> ergebnis = studentServiceHilfsMethoden.neueUrlaube(alte, neue);

        //assert
        assertThat(ergebnis).hasSize(2);
        assertThat(ergebnis).contains(urlaub1, urlaub2);
    }


}
