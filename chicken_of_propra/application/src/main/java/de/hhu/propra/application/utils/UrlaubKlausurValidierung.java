package de.hhu.propra.application.utils;

import de.hhu.propra.application.dto.UrlaubDto;
import de.hhu.propra.domain.aggregates.klausur.Klausur;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UrlaubKlausurValidierung {



    public UrlaubDto freieZeitDurchKlausur(Klausur klausur){
        if(klausur.online()){
            return new UrlaubDto(klausur.datum().toLocalDate(),
                    klausur.datum().toLocalTime().minusMinutes(30),
                    klausur.datum().toLocalTime().plusMinutes(klausur.dauer()));
        }

        return new UrlaubDto(klausur.datum().toLocalDate(),
                klausur.datum().toLocalTime().minusMinutes(120),
                klausur.datum().toLocalTime().plusMinutes(klausur.dauer() + 120));

    }

    public List<UrlaubDto> reduziereUrlaubDurchKlausur(UrlaubDto urlaub, UrlaubDto freieZeitDurchKlausur){
        // xmax1 >= xmin2 and xmax2 >= xmin1
        List<UrlaubDto> urlaubDtos = new ArrayList<>();
        LocalTime urlaubsStart = urlaub.startzeit();                  // xmin1
        LocalTime klausurStart = freieZeitDurchKlausur.startzeit();   // xmin2
        LocalTime urlaubsEnde = urlaub.endzeit();                     // xmax1
        LocalTime klausurEnde = freieZeitDurchKlausur.endzeit();      // xmax2

        //schaut ob Überschneidung
        if(urlaubsEnde.isAfter(klausurStart) && klausurEnde.isAfter(urlaubsStart)){
            // Gibt es einen Urlaubsblock vor der Klausur?
            if(klausurStart.isAfter(urlaubsStart)) {
                urlaubDtos.add(new UrlaubDto(urlaub.datum(), urlaubsStart, klausurStart));
            }
            // Gibt es einen Urlaubsblock nach der Klausur?
            if(urlaubsEnde.isAfter(klausurEnde)) {
                urlaubDtos.add(new UrlaubDto(urlaub.datum(), klausurEnde, urlaubsEnde));
            }
            return urlaubDtos;
        }
        urlaubDtos.add(urlaub);
        return urlaubDtos;
    }

    public List<UrlaubDto> urlaubKlausurValidierung(UrlaubDto urlaub, List<Klausur> klausur){
        List<UrlaubDto> urlaubDtos = new ArrayList<>();
        for(Klausur klausuren : klausur){
            for(UrlaubDto urlaubDto : reduziereUrlaubDurchKlausur(urlaub, freieZeitDurchKlausur(klausuren))){
                urlaubDtos.add(urlaubDto);
            }
        }
        return urlaubDtos;
    }
    public List<UrlaubDto> urlaubeZusammenfuegen(List<UrlaubDto> urlaube){
        for(int i = 0; i < urlaube.size(); i++){
            for(int j = i + 1; j < urlaube.size(); j++){
                if(pruefeUrlaubUeberschneidung(urlaube.get(i), urlaube.get(j))){
                    urlaube.set(j, fasseZeitZusammen(urlaube.get(i), urlaube.get(j)));
                    urlaube.remove(i);
                    i--;
                    break;
                }
            }
        }
        return urlaube;
    }

     boolean pruefeUrlaubUeberschneidung(UrlaubDto erstesUrlaubsDto, UrlaubDto zweitesUrlaubsDto){
        return erstesUrlaubsDto.endzeit().isAfter(zweitesUrlaubsDto.startzeit()) &&
                zweitesUrlaubsDto.endzeit().isAfter(erstesUrlaubsDto.startzeit());
    }

     UrlaubDto fasseZeitZusammen (UrlaubDto erstesUrlaubDto, UrlaubDto zweitesUrlaubDto){
        LocalTime startzeit = erstesUrlaubDto.startzeit();
        LocalTime endzeit = erstesUrlaubDto.endzeit();
        if(zweitesUrlaubDto.startzeit().isBefore(startzeit)){
            startzeit = zweitesUrlaubDto.startzeit();
        }
        if(zweitesUrlaubDto.endzeit().isAfter(endzeit)){
            endzeit = zweitesUrlaubDto.endzeit();
        }
        return new UrlaubDto(erstesUrlaubDto.datum(),startzeit, endzeit);

    }

}
