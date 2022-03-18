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
import java.time.Duration;
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

    // TODO: Fehler, falls Id nicht vorhanden
    public Student studentMitId(Long studentId) {
        return studentRepository.studentMitId(studentId);
    }

    public List<String> urlaubFehler(Long studentId) {
        return new ArrayList<>();
    }

    //TODO: klausurAnmelden : -Urlaub anpassen
    //TODO: urlaubAnmelden : -pruefen, ob Klausur an dem Tag

    public Set<String> urlaubAnlegen(String studentHandle, UrlaubDto urlaubDto) {
        StudentServiceHilfsMethoden hilfsMethoden = new StudentServiceHilfsMethoden(klausurRepository);
        UrlaubValidierung urlaubValidierung = new UrlaubValidierung();
        Student student = studentRepository.studentMitHandle(studentHandle);
        List<UrlaubDto> urlaubeAnTag = hilfsMethoden.findeUrlaubeAmSelbenTag(student, urlaubDto);
        List<Klausur> klausurenVonStudent = holeAlleKlausurenMitID(student);
        List<Klausur> klausurenVonStudentAnTag = hilfsMethoden.studentHatKlausurAnTag(klausurenVonStudent, urlaubDto.datum());

        if (!klausurenVonStudentAnTag.isEmpty() && hilfsMethoden.genugUrlaub(student,urlaubDto)) {
            List<UrlaubDto> urlaubDtos = urlaubKlausurBearbeitung.urlaubKlausurValidierung(urlaubDto, klausurenVonStudentAnTag);
            urlaubDtos.addAll(urlaubeAnTag);
            urlaubDtos = urlaubValidierung.urlaubeZusammenfuegen(urlaubDtos);
            fuegeUrlaubZusammen(urlaubDto, student, urlaubDtos);
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

    //TODO reduziere nach klausuranmeldung den Urlaub

    public synchronized void klausurAnmelden(Long studentId, Long klausurId) {
        Student student = studentRepository.studentMitId(studentId);
        Klausur klausurAusDb = klausurRepository.klausurMitId(klausurId);
        student.addKlausur(klausurAusDb);
        studentRepository.save(student);
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

    //TODO : KLAUSUR
    // public boolean
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

    public void fuegeUrlaubZusammen(UrlaubDto urlaubDto, Student student, List<UrlaubDto> urlaubDtos) {
        student.storniereUrlaubeAmTag(urlaubDto.datum());
        for(UrlaubDto dto : urlaubDtos){
            student.addUrlaub(dto.datum(),dto.startzeit(),dto.endzeit());
        }
        studentRepository.save(student);
    }

}
