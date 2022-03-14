package de.hhu.propra.application.services;

import de.hhu.propra.application.dto.KlausurDto;
import de.hhu.propra.application.dto.UrlaubDto;
import de.hhu.propra.application.repositories.KlausurRepository;
import de.hhu.propra.application.repositories.StudentRepository;
import de.hhu.propra.application.stereotypes.ApplicationService;
import de.hhu.propra.application.utils.UrlaubKlausurValidierung;
import de.hhu.propra.application.utils.UrlaubValidierung;
import de.hhu.propra.domain.aggregates.klausur.Klausur;
import de.hhu.propra.domain.aggregates.student.Student;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@ApplicationService
public class StudentService {
    private final StudentRepository studentRepository;
    private final KlausurRepository klausurRepository;
    private UrlaubValidierung urlaubValidierung = new UrlaubValidierung();
    private UrlaubKlausurValidierung urlaubKlausurValidierung = new UrlaubKlausurValidierung();


    public StudentService(StudentRepository studentRepository, KlausurRepository klausurRepository) {
        this.studentRepository = studentRepository;
        this.klausurRepository = klausurRepository;
    }
    //TODO: klausurAnmelden : -Urlaub anpassen
    //TODO: urlaubAnmelden : -pruefen, ob Klausur an dem Tag
    public boolean urlaubAnlegen(Long studentId, UrlaubDto urlaubDto) {
        boolean erfolg = false;
        Student student = studentRepository.studentMitId(studentId);
        List<Klausur> klausurListe = student.getKlausuren().stream()
                .map(klausurRepository::klausurMitId)
                .collect(Collectors.toList());

        List<UrlaubDto> urlaube = findeUrlaubeAmSelbenTag(student, urlaubDto);

        //TODO : VERIFY KLAUSUR

        klausurListe = studentHatKlausur(klausurListe, urlaubDto.datum());
        if (!klausurListe.isEmpty()){

            List<UrlaubDto> urlaubDtos = new ArrayList<>();
            urlaubDtos = urlaubKlausurValidierung.urlaubKlausurValidierung(urlaubDto, klausurListe);

        }

        if (urlaubValidierung.urlaubIstValide(urlaubDto) && urlaube.size() < 2) {
            if((urlaube.size() == 1) && (urlaubValidierung.zweiUrlaubeAnEinemTag(urlaubDto,urlaube.get(0)))){
                erfolg = fuegeUrlaubHinzu(student, urlaubDto);
            }
            else if(!student.urlaubExistiert(urlaubDto.datum(), urlaubDto.startzeit(), urlaubDto.endzeit())) {
                erfolg = fuegeUrlaubHinzu(student, urlaubDto);
            }
        }
        return erfolg;
    }
    public List<UrlaubDto> findeUrlaubeAmSelbenTag(Student student, UrlaubDto urlaubDto){
        return student.getUrlaube().stream()
                .filter(u -> u.datum().equals(urlaubDto.datum()))
                .map(u -> new UrlaubDto(u.datum(), u.startzeit(), u.endzeit()))
                .toList();
    }

    //TODO: Notification for not enough holidays in weblayer
    private boolean fuegeUrlaubHinzu( Student student, UrlaubDto urlaubDto) {
        if (genugUrlaub(student, urlaubDto)) {
            student.addUrlaub(urlaubDto.datum(), urlaubDto.startzeit(), urlaubDto.endzeit());
            studentRepository.save(student);
            return true;
        }
        return false;
    }
/*
    private boolean hatKlausur(Student student, LocalDate datum){
        List<Klausur> klausurListe = student.getKlausuren();
    }
*/
    private boolean genugUrlaub(Student student, UrlaubDto urlaubDto){
        Duration duration = Duration.between(urlaubDto.startzeit(), urlaubDto.endzeit());
        return (duration.toMinutes() <= student.getResturlaub());
    }

    public synchronized boolean klausurErstellen(KlausurDto klausurDto){
        Klausur klausur = new Klausur(null,
                klausurDto.name(),
                klausurDto.datum(),
                klausurDto.dauer(),
                klausurDto.lsf(),
                klausurDto.online());

        if(!klausurRepository.alleKlausuren().contains(klausur)) {
            klausurRepository.save(klausur);
            return true;
        }
        return false;
    }


    public synchronized void klausurAnmelden(Long studentId, Long klausurId){
        Student student = studentRepository.studentMitId(studentId);
        Klausur klausurAusDb = klausurRepository.klausurMitId(klausurId);
        student.addKlausur(klausurAusDb);
        studentRepository.save(student);
    }

    public boolean urlaubStornieren(Long studentId, UrlaubDto urlaubDto){
        boolean ergebnis = false;
        Student student = studentRepository.studentMitId(studentId);

        if(urlaubValidierung.urlaubNurVorDemTagDesUrlaubs(urlaubDto)) {
            ergebnis = student.urlaubStornieren(urlaubDto.datum(), urlaubDto.startzeit(), urlaubDto.endzeit());
            studentRepository.save(student);
        }
        return ergebnis;
    }

    private List<Klausur> studentHatKlausur(List<Klausur> klausuren, LocalDate datum){
        return klausuren.stream()
                .filter(k -> k.datum().toLocalDate().equals(datum))
                .toList();

    }

}
