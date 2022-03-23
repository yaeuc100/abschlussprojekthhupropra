package de.hhu.propra.application.utils;

import de.hhu.propra.application.dto.KlausurDto;
import de.hhu.propra.domain.aggregates.klausur.Klausur;
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

}
