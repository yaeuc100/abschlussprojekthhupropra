package de.hhu.propra.application.utils;

import de.hhu.propra.application.dto.KlausurDto;
import de.hhu.propra.application.dto.UrlaubDto;
import de.hhu.propra.application.fehler.KlausurFehler;
import de.hhu.propra.application.fehler.UrlaubFehler;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class KlausurValidierung {

    private Set<String> fehlgeschlagen = new HashSet<>();


    public Set<String> getFehlgeschlagen() {
        return fehlgeschlagen;
    }

    public boolean startzeitVorEndzeit(KlausurDto klausur) {
        boolean ergebnis = klausur.datum().toLocalTime().isBefore(klausur.datum().toLocalTime().plusMinutes(klausur.dauer()));
        if (!ergebnis) {
            fehlgeschlagen.add(KlausurFehler.STARTZEIT_VOR_ENDZEIT);
        }
        return ergebnis;
    }

    public boolean datumLiegtInPraktikumszeit(KlausurDto klausur) {
        LocalDate start = LocalDate.of(2022, 3, 6); //ein tag vorher
        LocalDate ende = LocalDate.of(4000, 3, 26);
        boolean ergebnis = klausur.datum().toLocalDate().isAfter(start) && ende.isAfter(klausur.datum().toLocalDate());
        if (!ergebnis) {
            fehlgeschlagen.add(KlausurFehler.KLAUSUR_IN_ZEITRAUM);
        }
        return ergebnis;
    }

    public boolean lsfIDPasst(KlausurDto klausur) {
        String alsString = Long.toString(klausur.lsf());
        boolean ergebnis = true;
        if (alsString.length() != 6) {
            fehlgeschlagen.add(KlausurFehler.UNGUELTIGE_LSFID);
            ergebnis = false;
        }
        return ergebnis;
    }

    public boolean lsfIDMussMitAnderenPassen(KlausurDto klausur, List<KlausurDto> klausuren) {
        List<KlausurDto> gefundeneKlausuren = klausuren.stream()
                .filter(k -> k.lsf() == klausur.lsf())
                .toList();
        boolean ergebnis = true;
        if (!gefundeneKlausuren.isEmpty()) {
            if (!klausur.name().equals(gefundeneKlausuren.get(0).name())) {
                ergebnis = false;
            }
        }
        if (!ergebnis) {
            fehlgeschlagen.add("VeranstaltungsID existiert bereits mit " + klausur.lsf() +
                    ", der eingetragene Name ist dazu nicht gültig. Der dazu bestehende Name ist "
                    + gefundeneKlausuren.get(0).name());
        }
        return ergebnis;


    }

    public boolean klausurIstValide(KlausurDto klausur){
        return startzeitVorEndzeit(klausur) && datumLiegtInPraktikumszeit(klausur) &&
                lsfIDPasst(klausur);
    }
}
