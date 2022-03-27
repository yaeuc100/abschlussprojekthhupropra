package de.hhu.propra.application.utils;

import de.hhu.propra.application.dto.KlausurDto;
import de.hhu.propra.application.fehler.KlausurFehler;
import de.hhu.propra.domain.aggregates.klausur.Klausur;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;

import static java.util.Calendar.getInstance;

public class KlausurValidierung {

  private Set<String> fehlgeschlagen = new HashSet<>();

  public Set<String> getFehlgeschlagen() {
    return fehlgeschlagen;
  }

  boolean vielfachesVon15(KlausurDto klausurDto) {
    Klausur klausur = KlausurDto.toKlausur(klausurDto);

    int startMinuten = klausur.datum().getMinute();
    boolean ergebnis = startMinuten % 15 == 0 && klausur.dauer() % 15 == 0;
    if (!ergebnis) {
      fehlgeschlagen.add(KlausurFehler.VIELFACHES_VON_15);
    }
    return ergebnis;
  }

  public boolean klausurLiegtInDb(List<Klausur> klausuren, Klausur klausur) {
    boolean ergebnis = klausuren.contains(klausur);
    if (ergebnis) {
      fehlgeschlagen.add(KlausurFehler.KLAUSUR_LIEGT_In_DB);
    }
    return ergebnis;
  }

  /**
   *
   * @param klausuren
   * @param klausur
   * @return
   */
  public boolean keineKlausurUeberschneidung(List<Klausur> klausuren, Klausur klausur) {
    boolean ergebnis = true;
    for (Klausur k : klausuren) {
      if (pruefeUeberschneidung(k, klausur)) {
        ergebnis = false;
        break;
      }
    }
    if (!ergebnis) {
      fehlgeschlagen.add(KlausurFehler.NEUE_KLAUSUR_SCHNEIDET_ALTE);
    }
    return ergebnis;
  }

  boolean pruefeUeberschneidung(Klausur ersteKlausur, Klausur zweiteKlausur) {
    LocalTime ersteKlausurStart = ersteKlausur.datum().toLocalTime();
    LocalTime ersteKlausurEnde =
        ersteKlausur.datum().toLocalTime().plusMinutes(ersteKlausur.dauer());
    LocalTime zweiteKlausurStart = zweiteKlausur.datum().toLocalTime();
    LocalTime zweiteKlausurEnde =
        zweiteKlausur.datum().toLocalTime().plusMinutes(zweiteKlausur.dauer());
    if (!ersteKlausur.datum().toLocalDate().equals(zweiteKlausur.datum().toLocalDate())) {
      return false;
    }
    return (ersteKlausurEnde.isAfter(zweiteKlausurStart)
        && zweiteKlausurEnde.isAfter(ersteKlausurStart));
  }

  boolean amWochenende(KlausurDto klausur) {
    boolean ergebnis = true;
    Date date =
        Date.from(
            KlausurDto.toKlausur(klausur)
                .datum()
                .toLocalDate()
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant());
    Calendar datum = getInstance();
    datum.setTime(date);
    if ((datum.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
        || (datum.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)) {
      fehlgeschlagen.add(KlausurFehler.AM_WOCHENENDE);
      ergebnis = false;
    }
    return ergebnis;
  }

  boolean datumLiegtInPraktikumszeit(KlausurDto klausurDto) {
    LocalDate start = LocalDate.of(2022, 3, 6); // ein tag vorher
    LocalDate ende = LocalDate.of(4000, 3, 26);
    Klausur klausur = KlausurDto.toKlausur(klausurDto);
    boolean ergebnis =
        klausur.datum().toLocalDate().isAfter(start) && ende.isAfter(klausur.datum().toLocalDate());
    if (!ergebnis) {
      fehlgeschlagen.add(KlausurFehler.KLAUSUR_IN_ZEITRAUM);
    }
    return ergebnis;
  }

  boolean lsfIDPasst(KlausurDto klausur) throws IOException {
    String alsString = Long.toString(klausur.lsf());
    boolean ergebnis = true;
    if (klausur.name().isBlank()) {
      fehlgeschlagen.add(KlausurFehler.NAME_NICHT_LEER);
      return false;
    }

    if (!LsfIdValidierung.namePasstZuId(klausur)) {
      String nachricht;
      if (LsfIdValidierung.getName(klausur).isBlank()) {
        nachricht = KlausurFehler.UNGUELTIGE_LSFID;
      } else
        nachricht =
            new String(
                    ("Der angegebene Veranstaltungsname ist ungültig. "
                            + "Der dazu bestehende Name ist ")
                        .getBytes(),
                    StandardCharsets.UTF_8)
                + LsfIdValidierung.getName(klausur);
      fehlgeschlagen.add(nachricht);
      ergebnis = false;
    }

    return ergebnis;
  }

  boolean startzeitVorEndzeit(KlausurDto klausurDto) {
    boolean ergebnis =
        LocalTime.parse(klausurDto.startzeit()).isBefore(LocalTime.parse(klausurDto.endzeit()));
    if (!ergebnis) {
      fehlgeschlagen.add(KlausurFehler.STARTZEIT_VOR_ENDZEIT);
    }
    return ergebnis;
  }

  public boolean klausurIstValide(KlausurDto klausur) throws IOException {
    return datumLiegtInPraktikumszeit(klausur)
        && lsfIDPasst(klausur)
        && startzeitVorEndzeit(klausur)
        && startzeitVorEndzeit(klausur)
        && amWochenende(klausur)
        && vielfachesVon15(klausur);
  }
}
