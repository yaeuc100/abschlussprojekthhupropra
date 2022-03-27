package de.hhu.propra.application.dtos;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import de.hhu.propra.domain.aggregates.student.Urlaub;
import de.hhu.propra.application.dto.UrlaubDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

public class UrlaubDtoTests {

    @Test
    @DisplayName("Ein UrlaubDto wird richtig zu einem Urlaub übersetzt")
    void test() {
        //arrange
        UrlaubDto urlaubDto = new UrlaubDto(
                LocalDate.of(6000, 10, 10).toString(),
                LocalTime.of(10, 30).toString(),
                LocalTime.of(11, 0).toString());
        Urlaub urlaub = new Urlaub(
                LocalDate.of(6000, 10, 10),
                LocalTime.of(10, 30),
                LocalTime.of(11, 0));

        //act
        Urlaub ergebnis = UrlaubDto.toUrlaub(urlaubDto);

        //assert
        assertThat(ergebnis).isEqualTo(urlaub);
    }

    @Test
    @DisplayName("Ein Urlaub wird richtig zu einem UrlaubDto übersetzt")
    void test1() {
        //arrange
        Urlaub urlaub = new Urlaub(
                LocalDate.of(6000, 10, 10),
                LocalTime.of(10, 30),
                LocalTime.of(11, 0));
        UrlaubDto urlaubDto = new UrlaubDto(
                LocalDate.of(6000, 10, 10).toString(),
                LocalTime.of(10, 30).toString(),
                LocalTime.of(11, 0).toString());


        //act
        UrlaubDto ergebnis = UrlaubDto.toUrlaubDto(urlaub);

        //assert
        assertThat(ergebnis).isEqualTo(urlaubDto);
    }

    @Test
    @DisplayName("Ein UrlaubDto wird richtig durch toString formatiert")
    void test2() {
        //arrange
        UrlaubDto urlaubDto = new UrlaubDto(
                LocalDate.of(6000, 10, 10).toString(),
                LocalTime.of(10, 30).toString(),
                LocalTime.of(11, 0).toString());


        //act
        String ergebnis = urlaubDto.toString();

        //assert
        assertThat(ergebnis).isEqualTo("Urlaub{datum=6000-10-10, startzeit=10:30, endzeit=11:00}");
    }

    @Test
    @DisplayName("Zwei gleiche UrlaubDtos werden als gleich erkannt")
    void test3() {
        //arrange
        UrlaubDto urlaubDto = new UrlaubDto(
                LocalDate.of(6000, 10, 10).toString(),
                LocalTime.of(10, 30).toString(),
                LocalTime.of(11, 0).toString());
        UrlaubDto urlaubDto2 = new UrlaubDto(
                LocalDate.of(6000, 10, 10).toString(),
                LocalTime.of(10, 30).toString(),
                LocalTime.of(11, 0).toString());


        //act
        boolean ergebnis = urlaubDto.equals(urlaubDto2);

        //assert
        assertThat(ergebnis).isTrue();
    }

    @Test
    @DisplayName("Zwei unterschiedliche UrlaubDtos werden als verschieden erkannt")
    void test4() {
        //arrange
        UrlaubDto urlaubDto = new UrlaubDto(
                LocalDate.of(6000, 10, 10).toString(),
                LocalTime.of(10, 30).toString(),
                LocalTime.of(11, 0).toString());
        UrlaubDto urlaubDto2 = new UrlaubDto(
                LocalDate.of(6000, 10, 10).toString(),
                LocalTime.of(10, 0).toString(),
                LocalTime.of(11, 0).toString());


        //act
        boolean ergebnis = urlaubDto.equals(urlaubDto2);

        //assert
        assertThat(ergebnis).isFalse();
    }


}
