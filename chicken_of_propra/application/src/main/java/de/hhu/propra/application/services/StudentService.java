package de.hhu.propra.application.services;

import de.hhu.propra.application.dto.KlausurDto;
import de.hhu.propra.application.dto.UrlaubDto;
import de.hhu.propra.application.repositories.KlausurRepository;
import de.hhu.propra.application.repositories.StudentRepository;
import de.hhu.propra.application.stereotypes.ApplicationService;
import de.hhu.propra.application.utils.KlausurValidierung;
import de.hhu.propra.application.utils.StudentServiceHilfsMethoden;
import de.hhu.propra.application.utils.UrlaubKlausurBearbeitung;
import de.hhu.propra.application.utils.UrlaubValidierung;
import de.hhu.propra.domain.aggregates.klausur.Klausur;
import de.hhu.propra.domain.aggregates.student.Student;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@ApplicationService
public class StudentService {
    private final StudentRepository studentRepository;
    private final KlausurRepository klausurRepository;

    private UrlaubKlausurBearbeitung urlaubKlausurBearbeitung = new UrlaubKlausurBearbeitung();


    public StudentService(StudentRepository studentRepository, KlausurRepository klausurRepository) {
        this.studentRepository = studentRepository;
        this.klausurRepository = klausurRepository;
    }


    public Set<String> urlaubAnlegen(String studentHandle, UrlaubDto urlaubDto) {
        StudentServiceHilfsMethoden hilfsMethoden = new StudentServiceHilfsMethoden(klausurRepository);
        UrlaubValidierung urlaubValidierung = new UrlaubValidierung();
        Student student = studentRepository.studentMitHandle(studentHandle);
        List<UrlaubDto> urlaubeAnTag = hilfsMethoden.findeUrlaubeAmSelbenTag(student, urlaubDto.datum());
        List<Klausur> klausurenVonStudent = holeAlleKlausurenMitID(student);
        List<Klausur> klausurenVonStudentAnTag = hilfsMethoden.studentHatKlausurAnTag(klausurenVonStudent, urlaubDto.datum());

        if (!klausurenVonStudentAnTag.isEmpty() && hilfsMethoden.genugUrlaub(student,urlaubDto)) {
            List<UrlaubDto> urlaubDtos = urlaubKlausurBearbeitung.urlaubKlausurValidierung(urlaubDto, klausurenVonStudentAnTag);
            urlaubDtos.addAll(urlaubeAnTag);
            urlaubDtos = urlaubValidierung.urlaubeZusammenfuegen(urlaubDtos);
            fuegeUrlaubeZusammen(urlaubDto.datum(), student, urlaubDtos);
        }

        else if (urlaubValidierung.urlaubIstValide(urlaubDto) && urlaubValidierung.maxZweiUrlaube(urlaubeAnTag)) {
            urlaubHinzufuegenOhneKlausur(urlaubDto, urlaubValidierung, student, urlaubeAnTag);
        }
        return urlaubValidierung.getFehlgeschlagen();
    }


    //TODO: Notification for not enough holidays in weblayer
    public boolean fuegeUrlaubHinzu(Student student, UrlaubDto urlaubDto) {
        StudentServiceHilfsMethoden hilfsMethoden = new StudentServiceHilfsMethoden(klausurRepository);
        if (hilfsMethoden.genugUrlaub(student, urlaubDto)) {
            student.addUrlaub(urlaubDto.datum(), urlaubDto.startzeit(), urlaubDto.endzeit());
            studentRepository.save(student);
            return true;
        }
        return false;
    }

    public synchronized boolean klausurErstellen(KlausurDto klausurDto) throws IOException {
        KlausurValidierung klausurValidierung = new KlausurValidierung();
        Klausur klausur = new Klausur(null,
                klausurDto.name(),
                klausurDto.datum(),
                klausurDto.dauer(),
                klausurDto.lsf(),
                klausurDto.online());

        if (!klausurValidierung.klausurLiegtInDb(klausurRepository.alleKlausuren(),klausur)) {
            if(klausurValidierung.klausurIstValide(klausurDto)){
                klausurRepository.save(klausur);
                return true;
            }
        }
        return false;
    }

    public synchronized void klausurAnmelden(String studentHandle, Long klausurId) {
        StudentServiceHilfsMethoden hilfsMethoden = new StudentServiceHilfsMethoden(klausurRepository);
        Student student = studentRepository.studentMitHandle(studentHandle);
        Klausur klausurAusDb = klausurRepository.klausurMitId(klausurId);
        student.addKlausur(klausurAusDb);
        List<UrlaubDto> urlaube = hilfsMethoden.findeUrlaubeAmSelbenTag(student, klausurAusDb.datum().toLocalDate());
        urlaube = urlaubKlausurBearbeitung.reduziereUrlaubDurchEineKlausur(urlaube, urlaubKlausurBearbeitung.freieZeitDurchKlausur(klausurAusDb));
        fuegeUrlaubeZusammen(klausurAusDb.datum().toLocalDate(), student, urlaube);
    }

    public boolean urlaubStornieren(String studentHandle, UrlaubDto urlaubDto) {
        UrlaubValidierung urlaubValidierung = new UrlaubValidierung();
        boolean ergebnis = false;
        Student student = studentRepository.studentMitHandle(studentHandle);

        if (urlaubValidierung.urlaubNurVorDemTagDesUrlaubs(urlaubDto)) {
            ergebnis = student.urlaubStornieren(urlaubDto.datum(), urlaubDto.startzeit(), urlaubDto.endzeit());
            studentRepository.save(student);
        }
        return ergebnis;
    }


    public void klausurStornieren(String studentHandle, Klausur klausur) {
        Student student = studentRepository.studentMitHandle(studentHandle);
        student.klausurStornieren(klausur);
        studentRepository.save(student);
    }

    private List<Klausur> holeAlleKlausurenMitID(Student student) {
        return student.getKlausuren().stream()
                .map(klausurRepository::klausurMitId)
                .collect(Collectors.toList());
    }

    private void urlaubHinzufuegenOhneKlausur(UrlaubDto urlaubDto, UrlaubValidierung urlaubValidierung, Student student, List<UrlaubDto> urlaubeAnTag) {
        if ((urlaubeAnTag.size() == 1) && (urlaubValidierung.zweiUrlaubeAnEinemTag(urlaubDto, urlaubeAnTag.get(0)))) {
            fuegeUrlaubHinzu(student, urlaubDto);
        } else if (!student.urlaubExistiert(urlaubDto.datum(), urlaubDto.startzeit(), urlaubDto.endzeit())) {
            fuegeUrlaubHinzu(student, urlaubDto);
        }
    }

    public void fuegeUrlaubeZusammen(LocalDate datum, Student student, List<UrlaubDto> urlaubDtos) {
        StudentServiceHilfsMethoden hilfsMethoden = new StudentServiceHilfsMethoden(klausurRepository);
        hilfsMethoden.storniereAlleUrlaubeAnTag(student,datum);
        for(UrlaubDto dto : urlaubDtos){
            student.addUrlaub(dto.datum(),dto.startzeit(),dto.endzeit());
        }
        studentRepository.save(student);
    }
}
