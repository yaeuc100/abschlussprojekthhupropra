package de.hhu.propra.application.utils;

import de.hhu.propra.application.dto.UrlaubDto;
import de.hhu.propra.application.repositories.KlausurRepository;
import de.hhu.propra.domain.aggregates.klausur.Klausur;
import de.hhu.propra.domain.aggregates.student.Student;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class StudentServiceHilfsMethoden {


    private KlausurRepository klausurRepository;

    public StudentServiceHilfsMethoden(KlausurRepository klausurRepository){
        this.klausurRepository = klausurRepository;
    }

    public List<Klausur> studentHatKlausurAnTag(List<Klausur> klausuren, LocalDate datum) {
        return klausuren.stream()
                .filter(k -> k.datum().toLocalDate().equals(datum))
                .toList();

    }
    public boolean genugUrlaub(Student student, UrlaubDto urlaubDto) {
        Duration duration = Duration.between(urlaubDto.startzeit(), urlaubDto.endzeit());
        return (duration.toMinutes() <= student.getResturlaub());
    }


    public List<UrlaubDto> findeUrlaubeAmSelbenTag(Student student, UrlaubDto urlaubDto) {
        UrlaubValidierung urlaubValidierung = new UrlaubValidierung();
        return student.getUrlaube().stream()
                .filter(u -> u.datum().equals(urlaubDto.datum()))
                .map(u -> new UrlaubDto(u.datum(), u.startzeit(), u.endzeit()))
                .toList();
    }



}
