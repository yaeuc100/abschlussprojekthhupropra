package de.hhu.propra.application.utils;

import de.hhu.propra.application.dto.UrlaubDto;
import de.hhu.propra.domain.aggregates.klausur.Klausur;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UrlaubKlausurBearbeitung {
    UrlaubValidierung urlaubValidierung = new UrlaubValidierung();


    public UrlaubDto freieZeitDurchKlausur(Klausur klausur) {
        if (klausur.online()) {
            return new UrlaubDto(klausur.datum().toLocalDate(),
                    klausur.datum().toLocalTime().minusMinutes(30),
                    klausur.datum().toLocalTime().plusMinutes(klausur.dauer()));
        }

        return new UrlaubDto(klausur.datum().toLocalDate(),
                klausur.datum().toLocalTime().minusMinutes(120),
                klausur.datum().toLocalTime().plusMinutes(klausur.dauer() + 120));

    }


    //lücken erstellen
    public List<UrlaubDto> reduziereUrlaubDurchKlausur(UrlaubDto urlaub, UrlaubDto freieZeitDurchKlausur) {
        // xmax1 >= xmin2 and xmax2 >= xmin1
        List<UrlaubDto> urlaubDtos = new ArrayList<>();
        LocalTime urlaubsStart = urlaub.startzeit();                  // xmin1
        LocalTime klausurStart = freieZeitDurchKlausur.startzeit();   // xmin2
        LocalTime urlaubsEnde = urlaub.endzeit();                     // xmax1
        LocalTime klausurEnde = freieZeitDurchKlausur.endzeit();      // xmax2

        //schaut ob Überschneidung
        if (ueberschneidet(urlaubsStart, klausurStart, urlaubsEnde, klausurEnde)) {
            // Gibt es einen Urlaubsblock vor der Klausur?
            if (urlaubStartetZuerst(urlaubsStart, klausurStart)) {
                urlaubDtos.add(new UrlaubDto(urlaub.datum(), urlaubsStart, klausurStart));
            }
            // Gibt es einen Urlaubsblock nach der Klausur?
            if (klausurEndetZuerst(urlaubsEnde, klausurEnde)) {
                urlaubDtos.add(new UrlaubDto(urlaub.datum(), klausurEnde, urlaubsEnde));
            }
        }
        else {
            urlaubDtos.add(urlaub);
        }
        return urlaubDtos;
    }
    public List<UrlaubDto> reduziereUrlaubDurchEineKlausur(List<UrlaubDto> urlaube, UrlaubDto freieZeitDurchKlausur){
        List<UrlaubDto> urlaubDtos = urlaube.stream()
                .flatMap(u -> reduziereUrlaubDurchKlausur(u,freieZeitDurchKlausur).stream())
                .collect(Collectors.toList());
        return  urlaubDtos;
    }

    public List<UrlaubDto> reduziereUrlaubDurchMehrereKlausuren(List<UrlaubDto> urlaube, List<Klausur> klausuren){
        List<UrlaubDto> urlaubDtos = reduziereUrlaubDurchEineKlausur(urlaube,freieZeitDurchKlausur(klausuren.get(0)));
        for(int i = 1 ; i<klausuren.size() ; i++){
            urlaubDtos = reduziereUrlaubDurchEineKlausur(urlaubDtos,freieZeitDurchKlausur(klausuren.get(i)));
        }
        return urlaubDtos;
    }

    public List<UrlaubDto> urlaubKlausurValidierung(UrlaubDto urlaub, List<Klausur> klausuren) {
        List<UrlaubDto> urlaubDtos = new ArrayList<>();
        for (Klausur klausur : klausuren) {
            urlaubDtos.addAll(reduziereUrlaubDurchKlausur(urlaub, freieZeitDurchKlausur(klausur)));
        }
        urlaubDtos = reduziereUrlaubDurchMehrereKlausuren(urlaubDtos,klausuren);
        urlaubDtos = urlaubValidierung.urlaubeZusammenfuegen(urlaubDtos);
        return urlaubDtos;
    }


    private boolean klausurEndetZuerst(LocalTime urlaubsEnde, LocalTime klausurEnde) {
        return urlaubsEnde.isAfter(klausurEnde);
    }

    private boolean urlaubStartetZuerst(LocalTime urlaubsStart, LocalTime klausurStart) {
        return klausurStart.isAfter(urlaubsStart);
    }

    private boolean ueberschneidet(LocalTime urlaubsStart, LocalTime klausurStart, LocalTime urlaubsEnde, LocalTime klausurEnde) {
        return urlaubsEnde.isAfter(klausurStart) && klausurEnde.isAfter(urlaubsStart);
    }

}
