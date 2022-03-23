package de.hhu.propra.application.utils;

import de.hhu.propra.application.dto.KlausurDto;
import org.assertj.core.internal.Bytes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class LsfIdValidierungTests {

    //TODO Test getINhalt, startIndex

    @Test
    @DisplayName("Der Name der Klausur passt nicht zur LSF-ID")
    void test() throws IOException {
        //arrange
        KlausurDto klausurDto = new KlausurDto("aaksfjgakqio",
                LocalDate.of(2022,3,17).toString(),
                LocalTime.of(10,30).toString(),
                LocalTime.of(11,0).toString(),
                222916,
                true);

        //act
        boolean ergebnis = LsfIdValidierung.namePasstZuId(klausurDto);


        //assert
        assertThat(ergebnis).isFalse();
    }

    @Test
    @DisplayName("Der Name der Klausur passt zur LSF-ID")
    void test2() throws IOException {
        //arrange
        KlausurDto klausurDto = new KlausurDto("Einführung in die Computerlinguistik",
                LocalDate.of(2022,3,17).toString(),
                LocalTime.of(10,30).toString(),
                LocalTime.of(11,0).toString(),
                222916,
                true);

        //act
        boolean ergebnis = LsfIdValidierung.namePasstZuId(klausurDto);

        //assert
        assertThat(ergebnis).isTrue();
    }
    @Test
    @DisplayName("Der Name der Veranstaltung wird in der HTML Seite gefunden")
    void test3() throws IOException {
        //arrange
        KlausurDto klausurDto = new KlausurDto("Einführung in die Computerlinguistik",
                LocalDate.of(2022,3,17).toString(),
                LocalTime.of(10,30).toString(),
                LocalTime.of(11,0).toString(),
                222916,
                true);

        //act
        String ergebnis = LsfIdValidierung.getName(klausurDto);

        //assert
        assertThat(ergebnis).isEqualTo(new String("Einführung in die Computerlinguistik".getBytes(),StandardCharsets.UTF_8));
    }

    @Test
    @DisplayName("Der richtige Link zu einer gültigen LSF-ID wird zurückgegeben")
    void test4() throws IOException {
        //arrange
        long lsfID = 22291;

        //act
        String ergebnis = LsfIdValidierung.erstelleUrl(22291);

        //assert
        assertThat(ergebnis).isEqualTo("https://lsf.hhu.de/qisserver/rds?state=verpublish&status=init&vmfile=no&pub" +
                "lishid=22291&moduleCall=webInfo&publishConfFile=webInfo&publishSubDir=veranstaltung");
    }

}
