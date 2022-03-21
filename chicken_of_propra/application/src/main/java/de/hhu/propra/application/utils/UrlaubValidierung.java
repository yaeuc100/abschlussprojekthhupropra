package de.hhu.propra.application.utils;

import de.hhu.propra.application.fehler.UrlaubFehler;
import de.hhu.propra.domain.aggregates.student.Student;
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
//     entweder 240 oder 150 max
//     max 2 und falls 2 gibt dann mit 90 min abstand zwischen dauer der 2. und 1. urlaub
//     urlaub bis 00.00 uhr anmelden
    //TODO Praktikumsstart
public class UrlaubValidierung {

    private Set<String> fehlgeschlagen = new HashSet<>();


    public Set<String> getFehlgeschlagen() {
        return fehlgeschlagen;
    }

    public boolean vielfachesVon15(Urlaub urlaub) {
        int startMinuten = urlaub.startzeit().getMinute();
        int endMinuten = urlaub.endzeit().getMinute();
        boolean ergebnis = startMinuten % 15 == 0 && endMinuten % 15 == 0;
        if (!ergebnis) {
            fehlgeschlagen.add(UrlaubFehler.VIELFACHES_VON_15);
        }
        return ergebnis;
    }

    public boolean dauerIstValide(Urlaub urlaub) {
        Duration diff = Duration.between(urlaub.startzeit(), urlaub.endzeit());
        long minuten = diff.toMinutes();
        boolean ergebnis = (minuten == 240 || minuten <= 150 );
        if (!ergebnis) {
            fehlgeschlagen.add(UrlaubFehler.DAUER_IST_VALIDE);
        }
        return ergebnis;
    }

    public boolean zweiUrlaubeAnEinemTag(Urlaub ersterUrlaub, Urlaub zweiterUrlaub) {
        boolean valide = true;
        LocalTime startZeit = LocalTime.of(8, 30);
        if (ersterUrlaub.startzeit().isAfter(zweiterUrlaub.startzeit())) {
            Urlaub hilf = ersterUrlaub;
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
            fehlgeschlagen.add(UrlaubFehler.ZWEI_URLAUBe_AN_TAG);
        }
        return valide;
    }

    public boolean urlaubNurVorDemTagDesUrlaubs(Urlaub urlaub) {
        boolean ergebnis = urlaub.datum().isAfter(LocalDate.now());
        if (!ergebnis){
            fehlgeschlagen.add(UrlaubFehler.ANTRAG_RECHTZEITIG);
        }
        return ergebnis;
    }

    public boolean genugUrlaub(Student student, Urlaub urlaub) {
        Duration duration = Duration.between(urlaub.startzeit(), urlaub.endzeit());
        boolean ergebnis = (duration.toMinutes() <= student.getResturlaub());
        if(!ergebnis){
            fehlgeschlagen.add(UrlaubFehler.NICHT_GENUG_URLAUB_VORHANDEN);
        }
        return ergebnis;
    }

    boolean pruefeUrlaubUeberschneidung(Urlaub erstesUrlaubsDto, Urlaub zweitesUrlaubsDto) {
        return erstesUrlaubsDto.endzeit().isAfter(zweitesUrlaubsDto.startzeit()) &&
                zweitesUrlaubsDto.endzeit().isAfter(erstesUrlaubsDto.startzeit());
    }

    public List<Urlaub> urlaubeZusammenfuegen(List<Urlaub> urlaube) {
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

    Urlaub fasseZeitZusammen(Urlaub erstesUrlaub, Urlaub zweitesUrlaub) {
        LocalTime startzeit = erstesUrlaub.startzeit();
        LocalTime endzeit = erstesUrlaub.endzeit();
        if (zweitesUrlaub.startzeit().isBefore(startzeit)) {
            startzeit = zweitesUrlaub.startzeit();
        }
        if (zweitesUrlaub.endzeit().isAfter(endzeit)) {
            endzeit = zweitesUrlaub.endzeit();
        }
        return new Urlaub(erstesUrlaub.datum(), startzeit, endzeit);

    }

    // TODO: Auch Klausur
    public boolean startzeitVorEndzeit(Urlaub urlaub) {
        boolean ergebnis = urlaub.startzeit().isBefore(urlaub.endzeit());
        if (!ergebnis){
            fehlgeschlagen.add(UrlaubFehler.STARTZEIT_VOR_ENDZEIT);
        }
        return ergebnis;
    }

    public boolean datumLiegtInPraktikumszeit(Urlaub urlaub) {
        LocalDate start = LocalDate.of(2022, 3, 6); //ein tag vorher
        LocalDate ende = LocalDate.of(4000, 3, 26);
        boolean ergebnis = urlaub.datum().isAfter(start) && ende.isAfter(urlaub.datum());
        if (!ergebnis){
            fehlgeschlagen.add(UrlaubFehler.URLAUB_IN_ZEITRAUM);
        }
        return ergebnis;
    }


    public boolean bisherMaxEinUrlaub(List<Urlaub> urlaube){
        boolean ergebnis = urlaube.size() < 2;
        if(!ergebnis){
            fehlgeschlagen.add(UrlaubFehler.MAX_ZWEI_URLAUBE);
        }
        return ergebnis;
    }

    public boolean urlaubIstValide(Urlaub urlaub) {
        return vielfachesVon15(urlaub) && dauerIstValide(urlaub)
                && urlaubNurVorDemTagDesUrlaubs(urlaub) && startzeitVorEndzeit(urlaub)
                && datumLiegtInPraktikumszeit(urlaub);
    }

}
