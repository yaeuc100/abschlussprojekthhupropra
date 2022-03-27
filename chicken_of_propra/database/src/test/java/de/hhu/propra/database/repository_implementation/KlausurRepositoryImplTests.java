package de.hhu.propra.database.repository_implementation;

import de.hhu.propra.application.dto.KlausurDto;
import de.hhu.propra.database.dao.KlausurDao;
import de.hhu.propra.domain.aggregates.klausur.Klausur;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJdbcTest
@ActiveProfiles("test")
public class KlausurRepositoryImplTests {

    @Autowired
    KlausurDao klausurDao;

    @Autowired
    JdbcTemplate db;

    @Test
    @DisplayName("Richtige Klausur wird raus gelesen")
    @Sql({"classpath:db/migration/V1__init.sql",
            "classpath:db/migration/loadtest.sql"})
    void test1() {
        //arrange
        KlausurRepositoryImpl klausurRepository = new KlausurRepositoryImpl(klausurDao, db);
        Klausur klausur = new Klausur(1L,
                "Rechnernetze",
                LocalDateTime.of(2020, 01, 01, 10, 00),
                60, 123456, true);

        //act
        Klausur ergebnis = klausurRepository.klausurMitId(klausur.id());

        //assert
        assertThat(klausur).isEqualTo(ergebnis);
    }


    @Test
    @DisplayName("Alle Klausuren werden geholt")
    @Sql({"classpath:db/migration/V1__init.sql",
            "classpath:db/migration/loadtest.sql"})
    void test2() {
        //arrange
        KlausurRepositoryImpl klausurRepository = new KlausurRepositoryImpl(klausurDao, db);

        //act
        List<Klausur> list = klausurRepository.alleKlausuren();

        //assert
        assertThat(list).hasSize(3);
    }


    @Test
    @DisplayName("Klausur wird gespeichert")
    @Sql({"classpath:db/migration/V1__init.sql",
            "classpath:db/migration/loadtest.sql"})
    void test3() {
        //arrange
        KlausurRepositoryImpl klausurRepository = new KlausurRepositoryImpl(klausurDao, db);
        Klausur klausur = new Klausur(null,
                "Rechnernetze",
                LocalDateTime.of(2020, 01, 01, 10, 00),
                60, 123456, true);
        Klausur erwartet = new Klausur(4L,
                "Rechnernetze",
                LocalDateTime.of(2020, 01, 01, 10, 00),
                60, 123456, true);

        //act
        klausurRepository.save(klausur);

        //assert
        assertThat(klausurRepository.alleKlausuren()).hasSize(4);
        assertThat(klausurRepository.klausurMitId(4L)).isEqualTo(erwartet);
    }

    @Test
    @DisplayName("Klausur wird mit Daten geholt")
    @Sql({"classpath:db/migration/V1__init.sql",
            "classpath:db/migration/loadtest.sql"})
    void test4() {
        //arrange
        KlausurRepositoryImpl klausurRepository = new KlausurRepositoryImpl(klausurDao, db);
        Klausur klausur = new Klausur(null, "Rechnernetze",
                LocalDateTime.of(2020, 01, 01, 10, 00),
                60, 123456, true);
        KlausurDto dtoAusKlausur = KlausurDto.toKlausurDto(klausur);
        Klausur erwartet = new Klausur(1L,
                "Rechnernetze",
                LocalDateTime.of(2020, 01, 01, 10, 00),
                60, 123456, true);

        //act
        Klausur klausurAusDb = klausurRepository.klausurMitDaten(dtoAusKlausur);

        //assert
        assertThat(klausurAusDb).isEqualTo(erwartet);
    }

}
