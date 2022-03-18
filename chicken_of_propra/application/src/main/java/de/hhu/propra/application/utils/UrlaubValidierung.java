package de.hhu.propra.application.utils;

import de.hhu.propra.application.dto.UrlaubDto;
import de.hhu.propra.application.fehler.UrlaubFehler;
import de.hhu.propra.domain.aggregates.student.Urlaub;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//     vielfaches von 15 min
//     startzeit mod 15 min
//     endezeit mod 15 min
//     entwerder 240 oder 150 max
//     max 2 und falls 2 gibt dann mit 90 min abstand zwischen dauer der 2. und 1. urlaub
//     urlaub bis 00.00 uhr anmelden
    //TODO Praktikumsstart
public class UrlaubValidierung {

    private Set<String> fehlgeschlagen = new HashSet<>();


    public Set<String> getFehlgeschlagen() {
        return fehlgeschlagen;
    }

    public boolean vielfachesVon15(UrlaubDto urlaubDto) {
        int startMinuten = urlaubDto.startzeit().getMinute();
        int endMinuten = urlaubDto.endzeit().getMinute();
        boolean ergebnis = startMinuten % 15 == 0 && endMinuten % 15 == 0;
        if (!ergebnis) {
            fehlgeschlagen.add(UrlaubFehler.VIELFACHES_VON_15);
        }
        return ergebnis;
    }

    public boolean dauerIstValide(UrlaubDto urlaubDto) {
        Duration diff = Duration.between(urlaubDto.startzeit(), urlaubDto.endzeit());
        long minuten = diff.toMinutes();
        boolean ergebnis = (minuten == 240 || (minuten <= 150 && minuten >= 15));
        if (!ergebnis) {
            fehlgeschlagen.add(UrlaubFehler.DAUER_IST_VALIDE);
        }
        return ergebnis;
    }

    public boolean zweiUrlaubeAnEinemTag(UrlaubDto ersterUrlaub, UrlaubDto zweiterUrlaub) {
        boolean valide = true;
        LocalTime startZeit = LocalTime.of(8, 30);
        if (ersterUrlaub.startzeit().isAfter(zweiterUrlaub.startzeit())) {
            UrlaubDto hilf = ersterUrlaub;
            ersterUrlaub = zweiterUrlaub;
            zweiterUrlaub = hilf;
        }

        if (!ersterUrlaub.startzeit().equals(startZeit) || !startZeit.plus(Duration.ofHours(4)).equals(zweiterUrlaub.endzeit())) {
            valide = false;
        }
        Duration duration = Duration.between(ersterUrlaub.endzeit(), zweiterUrlaub.startzeit());
        if (duration.toMinutes() < 90) {
            valide = false;
        }
        if (!valide){
            fehlgeschlagen.add(UrlaubFehler.ZWEI_URLAUB_AN_TAG);
        }
        return valide;
    }

    public boolean urlaubNurVorDemTagDesUrlaubs(UrlaubDto urlaub) {
        boolean ergebnis = urlaub.datum().isAfter(LocalDate.now());
        if (!ergebnis){
            fehlgeschlagen.add(UrlaubFehler.ANTRAG_RECHTZEITIG);
        }
        return ergebnis;
    }

    boolean pruefeUrlaubUeberschneidung(UrlaubDto erstesUrlaubsDto, UrlaubDto zweitesUrlaubsDto) {
        return erstesUrlaubsDto.endzeit().isAfter(zweitesUrlaubsDto.startzeit()) &&
                zweitesUrlaubsDto.endzeit().isAfter(erstesUrlaubsDto.startzeit());
    }

    public List<UrlaubDto> urlaubeZusammenfuegen(List<UrlaubDto> urlaube) {
        for (int i = 0; i < urlaube.size(); i++) {
            for (int j = i + 1; j < urlaube.size(); j++) {
                if (pruefeUrlaubUeberschneidung(urlaube.get(i), urlaube.get(j))) {
                    urlaube.set(j, fasseZeitZusammen(urlaube.get(i), urlaube.get(j)));
                    urlaube.remove(i);
                    i--;
                    break;
                }
            }
        }
        return urlaube;
    }

    UrlaubDto fasseZeitZusammen(UrlaubDto erstesUrlaubDto, UrlaubDto zweitesUrlaubDto) {
        LocalTime startzeit = erstesUrlaubDto.startzeit();
        LocalTime endzeit = erstesUrlaubDto.endzeit();
        if (zweitesUrlaubDto.startzeit().isBefore(startzeit)) {
            startzeit = zweitesUrlaubDto.startzeit();
        }
        if (zweitesUrlaubDto.endzeit().isAfter(endzeit)) {
            endzeit = zweitesUrlaubDto.endzeit();
        }
        return new UrlaubDto(erstesUrlaubDto.datum(), startzeit, endzeit);

    }

    // TODO: Auch Klausur
    public boolean startzeitVorEndzeit(UrlaubDto urlaub) {
        boolean ergebnis = urlaub.startzeit().isBefore(urlaub.endzeit());
        if (!ergebnis){
            fehlgeschlagen.add(UrlaubFehler.STARTZEIT_VOR_ENDZEIT);
        }
        return ergebnis;
    }

    public boolean datumLiegtInPraktikumszeit(UrlaubDto urlaub) {
        LocalDate start = LocalDate.of(2022, 3, 6); //ein tag vorher
        LocalDate ende = LocalDate.of(4000, 3, 26);
        boolean ergebnis = urlaub.datum().isAfter(start) && ende.isAfter(urlaub.datum());
        if (!ergebnis){
            fehlgeschlagen.add(UrlaubFehler.URLAUB_IN_ZEITRAUM);
        }
        return ergebnis;
    }

    public boolean urlaubIstValide(UrlaubDto urlaub) {
        return vielfachesVon15(urlaub) && dauerIstValide(urlaub)
                && urlaubNurVorDemTagDesUrlaubs(urlaub) && startzeitVorEndzeit(urlaub) && datumLiegtInPraktikumszeit(urlaub);
    }

    public boolean maxZweiUrlaube(List<UrlaubDto> urlaube){
        boolean ergebnis = urlaube.size() < 2;
        if(!ergebnis){
            fehlgeschlagen.add(UrlaubFehler.MAX_ZWEI_URLAUBE);
        }
        return ergebnis;
    }


}
