package de.hhu.propra.application.utils;

import de.hhu.propra.domain.aggregates.klausur.Klausur;
import de.hhu.propra.domain.aggregates.student.Urlaub;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UrlaubKlausurBearbeitung {

  UrlaubValidierung urlaubValidierung = new UrlaubValidierung();

  // fügt je nachdem, ob die Klausur online oder offline stattfindet, den jeweiligen Freistellungszuschlag hinzu
  public Urlaub freieZeitDurchKlausur(Klausur klausur) {
    if (klausur.online()) {
      return new Urlaub(klausur.datum().toLocalDate(),
          klausur.datum().toLocalTime().minusMinutes(30),
          klausur.datum().toLocalTime().plusMinutes(klausur.dauer()));
    }

    return new Urlaub(klausur.datum().toLocalDate(),
        klausur.datum().toLocalTime().minusMinutes(120),
        klausur.datum().toLocalTime().plusMinutes(klausur.dauer() + 120));

  }

  public List<Urlaub> reduziereUrlaubDurchKlausur(Urlaub urlaub, Urlaub freieZeitDurchKlausur) {
    // xmax1 >= xmin2 and xmax2 >= xmin1
    List<Urlaub> urlaubs = new ArrayList<>();
    LocalTime urlaubsStart = urlaub.startzeit();                  // xmin1
    LocalTime klausurStart = freieZeitDurchKlausur.startzeit();   // xmin2
    LocalTime urlaubsEnde = urlaub.endzeit();                     // xmax1
    LocalTime klausurEnde = freieZeitDurchKlausur.endzeit();      // xmax2

    if (ueberschneidet(urlaubsStart, klausurStart, urlaubsEnde, klausurEnde)) {
      // Gibt es einen Urlaubsblock vor der Klausur?
      if (urlaubStartetZuerst(urlaubsStart, klausurStart)) {
        urlaubs.add(new Urlaub(urlaub.datum(), urlaubsStart, klausurStart));
      }
      // Gibt es einen Urlaubsblock nach der Klausur?
      if (klausurEndetZuerst(urlaubsEnde, klausurEnde)) {
        urlaubs.add(new Urlaub(urlaub.datum(), klausurEnde, urlaubsEnde));
      }
    } else {
      urlaubs.add(urlaub);
    }
    return urlaubs;
  }

  public List<Urlaub> reduziereUrlaubDurchEineKlausur(List<Urlaub> urlaube,
      Urlaub freieZeitDurchKlausur) {
    return urlaube.stream()
        .flatMap(u -> reduziereUrlaubDurchKlausur(u, freieZeitDurchKlausur).stream())
        .collect(Collectors.toList());
  }

  public List<Urlaub> reduziereUrlaubDurchMehrereKlausuren(List<Urlaub> urlaube,
      List<Klausur> klausuren) {
    List<Urlaub> urlaubs = reduziereUrlaubDurchEineKlausur(urlaube,
        freieZeitDurchKlausur(klausuren.get(0)));
    for (int i = 1; i < klausuren.size(); i++) {
      urlaubs = reduziereUrlaubDurchEineKlausur(urlaubs, freieZeitDurchKlausur(klausuren.get(i)));
    }
    return urlaubs;
  }

  public List<Urlaub> urlaubKlausurBearbeitung(Urlaub urlaub, List<Klausur> klausuren) {
    List<Urlaub> urlaubs = new ArrayList<>();
    for (Klausur klausur : klausuren) {
      urlaubs.addAll(reduziereUrlaubDurchKlausur(urlaub, freieZeitDurchKlausur(klausur)));
    }
    urlaubs = reduziereUrlaubDurchMehrereKlausuren(urlaubs, klausuren);
    urlaubs = urlaubValidierung.urlaubeZusammenfuegen(urlaubs);
    return urlaubs;
  }


  private boolean klausurEndetZuerst(LocalTime urlaubsEnde, LocalTime klausurEnde) {
    return urlaubsEnde.isAfter(klausurEnde);
  }

  private boolean urlaubStartetZuerst(LocalTime urlaubsStart, LocalTime klausurStart) {
    return klausurStart.isAfter(urlaubsStart);
  }

  private boolean ueberschneidet(LocalTime urlaubsStart, LocalTime klausurStart,
      LocalTime urlaubsEnde, LocalTime klausurEnde) {
    return urlaubsEnde.isAfter(klausurStart) && klausurEnde.isAfter(urlaubsStart);
  }

}
