package de.hhu.propra.application.services;

import de.hhu.propra.application.dto.KlausurDto;
import de.hhu.propra.application.dto.UrlaubDto;
import de.hhu.propra.application.repositories.KlausurRepository;
import de.hhu.propra.application.repositories.StudentRepository;
import de.hhu.propra.application.stereotypes.ApplicationService;
import de.hhu.propra.application.utils.KlausurValidierung;
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
        UrlaubValidierung urlaubValidierung = new UrlaubValidierung();
        Student student = studentRepository.studentMitHandle(studentHandle);
        List<UrlaubDto> urlaubeAnTag = findeUrlaubeAmSelbenTag(student, urlaubDto);
        List<Klausur> klausurenVonStudent = holeAlleKlausurenMitID(student);
        List<Klausur> klausurenVonStudentAnTag = studentHatKlausurAnTag(klausurenVonStudent, urlaubDto.datum());

        if (!klausurenVonStudentAnTag.isEmpty()) {
            List<UrlaubDto> urlaubDtos = new ArrayList<>();
            urlaubDtos = urlaubKlausurBearbeitung.urlaubKlausurValidierung(urlaubDto, klausurenVonStudentAnTag);

        }

        if (urlaubValidierung.urlaubIstValide(urlaubDto) && urlaubValidierung.maxZweiUrlaube(urlaubeAnTag)) {
            if ((urlaubeAnTag.size() == 1) && (urlaubValidierung.zweiUrlaubeAnEinemTag(urlaubDto, urlaubeAnTag.get(0)))) {
                 fuegeUrlaubHinzu(student, urlaubDto);
            } else if (!student.urlaubExistiert(urlaubDto.datum(), urlaubDto.startzeit(), urlaubDto.endzeit())) {
                 fuegeUrlaubHinzu(student, urlaubDto);
            }
        }
        return urlaubValidierung.getFehlgeschlagen();
    }

    public List<UrlaubDto> findeUrlaubeAmSelbenTag(Student student, UrlaubDto urlaubDto) {
        UrlaubValidierung urlaubValidierung = new UrlaubValidierung();
        return student.getUrlaube().stream()
                .filter(u -> u.datum().equals(urlaubDto.datum()))
                .map(u -> new UrlaubDto(u.datum(), u.startzeit(), u.endzeit()))
                .toList();
    }

    //TODO: Notification for not enough holidays in weblayer
    private boolean fuegeUrlaubHinzu(Student student, UrlaubDto urlaubDto) {
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
    private boolean genugUrlaub(Student student, UrlaubDto urlaubDto) {
        Duration duration = Duration.between(urlaubDto.startzeit(), urlaubDto.endzeit());
        return (duration.toMinutes() <= student.getResturlaub());
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
    //TODO : KLAUSUR STORNIEREN

    private List<Klausur> studentHatKlausurAnTag(List<Klausur> klausuren, LocalDate datum) {
        return klausuren.stream()
                .filter(k -> k.datum().toLocalDate().equals(datum))
                .toList();

    }

    private List<Klausur> holeAlleKlausurenMitID(Student student) {
        return student.getKlausuren().stream()
                .map(klausurRepository::klausurMitId)
                .collect(Collectors.toList());
    }




}
