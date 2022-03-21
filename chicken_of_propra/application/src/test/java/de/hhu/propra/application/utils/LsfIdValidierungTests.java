package de.hhu.propra.application.utils;

import de.hhu.propra.application.dto.KlausurDto;
import org.assertj.core.internal.Bytes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class LsfIdValidierungTests {

    //TODO Set mit fehlermeldung testen

    @Test
    @DisplayName("name passt nicht zu lsf")
    void test() throws IOException {
        //arrange
        KlausurDto klausurDto = new KlausurDto("aaksfjgakqio",
                LocalDateTime.now(),
                120,
                222916,
                true);

        //act
        boolean ergebnis = LsfIdValidierung.namePasstZuId(klausurDto);


        //assert
        assertThat(ergebnis).isFalse();
    }

    @Test
    @DisplayName("name passt nicht zu lsf")
    void test2() throws IOException {
        //arrange
        KlausurDto klausurDto = new KlausurDto("Einführung in die Computerlinguistik",
                LocalDateTime.now(),
                120,
                222916,
                true);

        //act
        boolean ergebnis = LsfIdValidierung.namePasstZuId(klausurDto);

        //assert
        assertThat(ergebnis).isTrue();
    }
    @Test
    @DisplayName("Name der Veranstaltung im HTML Seite gefunden")
    void test3() throws IOException {
        //arrange
        KlausurDto klausurDto = new KlausurDto("Einführung in die Computerlinguistik",
                LocalDateTime.now(),
                120,
                222916,
                true);

        //act
        String ergebnis = LsfIdValidierung.getName(klausurDto);

        //assert
        assertThat(ergebnis).isEqualTo(new String("Einführung in die Computerlinguistik".getBytes(),StandardCharsets.UTF_8));
    }
}
