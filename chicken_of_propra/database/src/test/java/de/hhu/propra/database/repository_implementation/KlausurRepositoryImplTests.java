package de.hhu.propra.database.repository_implementation;

import de.hhu.propra.application.stereotypes.ApplicationService;
import de.hhu.propra.database.dao.KlausurDao;
import de.hhu.propra.database.entities.KlausurEntity;
import de.hhu.propra.domain.aggregates.klausur.Klausur;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.context.annotation.FilterType.ANNOTATION;

@JdbcTest(includeFilters = @ComponentScan.Filter(type = ANNOTATION, classes = {Repository.class}))
@ActiveProfiles("test")
@EnableAutoConfiguration
@ContextConfiguration(classes = {KlausurDao.class, KlausurRepositoryImpl.class})
public class KlausurRepositoryImplTests {

  @Autowired
  KlausurDao klausurDao;

  KlausurRepositoryImpl klausurRepository = new KlausurRepositoryImpl(klausurDao);

/*
  @Test
  @DisplayName("Richtige Klausur wird gespeichert")
  @Sql({"classpath:db/migration/V1__init.sql",
          "classpath:db/migration/loadtest.sql"})
  void test1(){
     Klausur klausur = new Klausur(1L,
            "Rechnernetze",
             LocalDateTime.now(),
            60,123456,true);

    Klausur ergebnis = klausurRepository.klausurMitId(klausur.id());
    assertThat(klausur).isEqualTo(ergebnis);
  }
*/
}
