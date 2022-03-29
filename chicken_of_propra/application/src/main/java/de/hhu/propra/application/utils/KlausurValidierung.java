package de.hhu.propra.application.utils;

import static java.util.Calendar.getInstance;

import de.hhu.propra.application.dto.KlausurDto;
import de.hhu.propra.application.fehler.KlausurFehler;
import de.hhu.propra.application.fehler.UrlaubFehler;
import de.hhu.propra.domain.aggregates.klausur.Klausur;
import de.hhu.propra.domain.aggregates.student.Urlaub;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class KlausurValidierung {
  private Set<String> fehlgeschlagen = new HashSet<>();
  DataParser globalData = DataParser.readFile();

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

  public boolean klausurNurVorDemTagDerKlausurStornieren(KlausurDto klausurDto) {
    Klausur klausur = KlausurDto.toKlausur(klausurDto);
    boolean ergebnis = klausur.datum().toLocalDate().isAfter(LocalDate.now());
    if (!ergebnis) {
      fehlgeschlagen.add(KlausurFehler.STORNIERUNG_RECHTZEITIG);
    }
    return ergebnis;
  }

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
    LocalDate start = globalData.getStart(); // ein tag vorher
    //LocalDate start = ;
   //LocalDate datum = p.getDatumStart();
   // System.out.println(datum);
    LocalDate ende = globalData.getEnd();
    Klausur klausur = KlausurDto.toKlausur(klausurDto);
    boolean ergebnis =
        klausur.datum().toLocalDate().isAfter(start) && ende.isAfter(klausur.datum().toLocalDate());
    if (!ergebnis) {
      fehlgeschlagen.add(KlausurFehler.KLAUSUR_IN_ZEITRAUM);
    }
    return ergebnis;
  }

  public void datumUngueltig() {
    fehlgeschlagen.add(KlausurFehler.DATUM_FALSCH);
  }

  boolean lsfIdPasst(KlausurDto klausur) throws IOException {
    boolean ergebnis = true;
    if (klausur.name().isBlank()) {
      fehlgeschlagen.add(KlausurFehler.NAME_NICHT_LEER);
      return false;
    }

    if (!LsfIdValidierung.namePasstZuId(klausur)) {
      String nachricht;
      if (LsfIdValidierung.getName(klausur).isBlank()) {
        nachricht = KlausurFehler.UNGUELTIGE_LSFID;
      } else {
        nachricht =
            new String(
                ("Der angegebene Veranstaltungsname ist ungültig. "
                    + "Der dazu bestehende Name ist ")
                    .getBytes(),
                StandardCharsets.UTF_8)
                + LsfIdValidierung.getName(klausur);
      }
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
        && lsfIdPasst(klausur)
        && startzeitVorEndzeit(klausur)
        && startzeitVorEndzeit(klausur)
        && amWochenende(klausur)
        && vielfachesVon15(klausur);
  }
}
