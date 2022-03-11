package de.hhu.propra.application.utils;

import de.hhu.propra.application.dto.UrlaubDto;
import de.hhu.propra.domain.aggregates.klausur.Klausur;
import de.hhu.propra.domain.aggregates.student.Student;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

public class UrlaubKlausurValidierung {

    //TODO: in StudentService
    public boolean studentHatKlausur(List<Klausur> klausuren, LocalDate datum){
        return !klausuren.stream()
                .filter(k -> k.datum().toLocalDate().equals(datum))
                .toList()
                .isEmpty();
    }

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

    public UrlaubDto urlaubSchnittMitKlausur(UrlaubDto urlaub, UrlaubDto freieZeitDurchKlausur){
        if(urlaub.startzeit().isBefore(freieZeitDurchKlausur.startzeit()) &&
                urlaub.endzeit().isAfter(freieZeitDurchKlausur.startzeit())){
            return new UrlaubDto(urlaub.datum(), urlaub.startzeit(), freieZeitDurchKlausur.startzeit());
        }
        if(urlaub.startzeit().isBefore(freieZeitDurchKlausur.endzeit()) &&
                urlaub.endzeit().isAfter(freieZeitDurchKlausur.endzeit())){
            return new UrlaubDto(urlaub.datum(), freieZeitDurchKlausur.startzeit(), urlaub.endzeit());
        }
        if((urlaub.startzeit().isAfter(freieZeitDurchKlausur.startzeit()) ||
                urlaub.startzeit().equals(freieZeitDurchKlausur.startzeit())) &&
                (urlaub.endzeit().isBefore(freieZeitDurchKlausur.endzeit()) ||
                        urlaub.endzeit().equals(freieZeitDurchKlausur.endzeit()))){
            return null;
        }
        return urlaub;
    }

    public UrlaubDto urlaubKlausurValidierung(UrlaubDto urlaub, Klausur klausur){
        return urlaubSchnittMitKlausur(urlaub, freieZeitDurchKlausur(klausur));
    }
}
