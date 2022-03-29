package de.hhu.propra.application.services;

import de.hhu.propra.application.dto.KlausurDto;
import de.hhu.propra.application.repositories.KlausurRepository;
import de.hhu.propra.application.repositories.StudentRepository;
import de.hhu.propra.domain.aggregates.klausur.Klausur;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class KlausurServiceTests {

  private KlausurRepository klausurRepository;
  private KlausurService klausurService;

  @BeforeEach
  void vorbereiten() {
    this.klausurRepository = mock(KlausurRepository.class);
    this.klausurService = new KlausurService(klausurRepository);

  }


  @Test
  @DisplayName("Alle Klausuren werden korrekt zurückgegeben")
  void test1() {
    Klausur klausur = new Klausur(1L,
        "BS",
        LocalDateTime.now(),
        10,
        123456,
        true);

    Klausur klausur1 = new Klausur(2L,
        "RN",
        LocalDateTime.now(),
        100,
        123456,
        true);

    when(klausurRepository.alleKlausuren()).thenReturn(List.of(klausur, klausur1));

    List<Klausur> klausurDtos = klausurService.alleKlausuren();

    assertThat(klausurDtos).contains(klausur1, klausur);
  }
}
