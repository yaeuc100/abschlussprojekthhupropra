package de.hhu.propra.application.utils;

import de.hhu.propra.application.dto.KlausurDto;
import de.hhu.propra.application.fehler.KlausurFehler;
import de.hhu.propra.application.fehler.UrlaubFehler;
import de.hhu.propra.domain.aggregates.klausur.Klausur;
import de.hhu.propra.domain.aggregates.student.Urlaub;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class KlausurValidierung {

    private Set<String> fehlgeschlagen = new HashSet<>();
    //TODO keine überschneidung

    public Set<String> getFehlgeschlagen() {
        return fehlgeschlagen;
    }

    public boolean klausurLiegtInDb(List<Klausur> klausurDtos, Klausur klausur){

        boolean ergebnis = klausurDtos.contains(klausur);
        if(ergebnis){
            fehlgeschlagen.add(KlausurFehler.KLAUSUR_LIEGT_In_DB);
        }
        return ergebnis;
    }

    public boolean keineKlausurUeberschneidung(List<Klausur> klausurDtos, Klausur klausur){

        UrlaubValidierung urlaubValidierung = new UrlaubValidierung();
        UrlaubKlausurBearbeitung urlaubKlausurBearbeitung = new UrlaubKlausurBearbeitung();
        Urlaub neueKlausur = urlaubKlausurBearbeitung.freieZeitDurchKlausur(klausur);
        boolean ergebnis = true;
        for(Klausur k : klausurDtos){
            System.out.println(k);
            if(k.datum().toLocalDate().equals(klausur.datum().toLocalDate())){
                Urlaub bereitsBestehendeKlausur = urlaubKlausurBearbeitung.freieZeitDurchKlausur(k);
                if(urlaubValidierung.pruefeUrlaubUeberschneidung(bereitsBestehendeKlausur,neueKlausur)){
                    ergebnis = false;
                }
            }
        }
        if(!ergebnis){
            fehlgeschlagen.add(KlausurFehler.NEUE_KLAUSUR_SCHNEIDET_ALTE);
        }
        return ergebnis;
    }

    public boolean datumLiegtInPraktikumszeit(KlausurDto klausurDto) {
        LocalDate start = LocalDate.of(2022, 3, 6); //ein tag vorher
        LocalDate ende = LocalDate.of(4000, 3, 26);
        Klausur klausur = KlausurDto.toKlausur(klausurDto);
        boolean ergebnis = klausur.datum().toLocalDate().isAfter(start) && ende.isAfter(klausur.datum().toLocalDate());
        if (!ergebnis) {
            fehlgeschlagen.add(KlausurFehler.KLAUSUR_IN_ZEITRAUM);
        }
        return ergebnis;
    }

    public boolean lsfIDPasst(KlausurDto klausur) throws IOException {
        String alsString = Long.toString(klausur.lsf());
        boolean ergebnis = true;
        if (alsString.length() != 6) {
            fehlgeschlagen.add(KlausurFehler.UNGUELTIGE_LSFID);
            ergebnis = false;
        }
        if (!LsfIdValidierung.namePasstZuId(klausur)) {
            String nachricht = new String(
                    ("Die angegebene Veranstaltungsname ist ungültig. " +
                            "Der dazu bestehende Name ist ").getBytes(), StandardCharsets.UTF_8) + LsfIdValidierung.getName(klausur);
            fehlgeschlagen.add(nachricht);
            ergebnis = false;
        }
        return ergebnis;
    }

    public boolean startzeitVorEndzeit(KlausurDto klausurDto) {
        boolean ergebnis = LocalTime.parse(klausurDto.startzeit()).isBefore(LocalTime.parse(klausurDto.endzeit()));
        if (!ergebnis){
            fehlgeschlagen.add(UrlaubFehler.STARTZEIT_VOR_ENDZEIT);
        }
        return ergebnis;
    }

    public boolean klausurIstValide(KlausurDto klausur) throws IOException {
        return datumLiegtInPraktikumszeit(klausur) && lsfIDPasst(klausur) && startzeitVorEndzeit(klausur);
    }
}
