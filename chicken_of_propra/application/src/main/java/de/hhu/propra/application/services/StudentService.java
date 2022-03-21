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
import de.hhu.propra.domain.aggregates.student.Urlaub;

import java.io.IOException;
import java.time.LocalDate;
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

    public Student studentMitHandle(String handle){
        return studentRepository.studentMitHandle(handle);
    }

    public void createStudent(String handle){
        Student student = new Student(null,handle);
        studentRepository.save(student);
    }

    public Set<String> urlaubAnlegen(String studentHandle, UrlaubDto urlaubDto) {
        StudentServiceHilfsMethoden hilfsMethoden = new StudentServiceHilfsMethoden(klausurRepository);
        Urlaub urlaub = UrlaubDto.toUrlaub(urlaubDto);
        UrlaubValidierung urlaubValidierung = new UrlaubValidierung();
        Student student = studentRepository.studentMitHandle(studentHandle);
        List<Urlaub> urlaubeAnTag = hilfsMethoden.findeUrlaubeAmSelbenTag(student, urlaub.datum());
        List<Klausur> KlausurenVonStudent = holeAlleKlausurenMitID(student);
        List<Klausur> klausurenVonStudentAnTag = hilfsMethoden.studentHatKlausurAnTag(KlausurenVonStudent, urlaub.datum());

        if (!klausurenVonStudentAnTag.isEmpty() && urlaubValidierung.genugUrlaub(student,urlaub)) {
            List<Urlaub> resultierendeUrlaube = urlaubKlausurBearbeitung.urlaubKlausurValidierung(urlaub, klausurenVonStudentAnTag);
            resultierendeUrlaube.addAll(urlaubeAnTag);
            resultierendeUrlaube = urlaubValidierung.urlaubeZusammenfuegen(resultierendeUrlaube);
            fuegeUrlaubeZusammen(urlaub.datum(), student, resultierendeUrlaube);
        }

        else if (urlaubValidierung.urlaubIstValide(urlaub) && urlaubValidierung.bisherMaxEinUrlaub(urlaubeAnTag)) {
            urlaubValidierung.genugUrlaub(student,urlaub);
            urlaubHinzufuegenOhneKlausur(urlaub, urlaubValidierung, student, urlaubeAnTag);
        }
        return urlaubValidierung.getFehlgeschlagen();
    }


    //TODO: Notification for not enough holidays in weblayer
    public boolean fuegeUrlaubHinzu(Student student, Urlaub urlaub) {
        StudentServiceHilfsMethoden hilfsMethoden = new StudentServiceHilfsMethoden(klausurRepository);
        UrlaubValidierung urlaubValidierung = new UrlaubValidierung();
        if (urlaubValidierung.genugUrlaub(student, urlaub)) {
            student.addUrlaub(urlaub.datum(), urlaub.startzeit(), urlaub.endzeit());
            student.berechneRestUrlaub();
            studentRepository.save(student);
            return true;
        }
        return false;
    }

    public synchronized Set<String> klausurErstellen(KlausurDto klausurDto) throws IOException {
        KlausurValidierung klausurValidierung = new KlausurValidierung();

        Klausur klausur = KlausurDto.toKlausur(klausurDto);
        List<Klausur> klausuren = klausurRepository.alleKlausuren();

        if (!klausurValidierung.klausurLiegtInDb(klausuren,klausur) ) {
            if(klausurValidierung.klausurIstValide(klausurDto)){
                klausurRepository.save(klausur);
            }
        }
        return klausurValidierung.getFehlgeschlagen();
    }

    public synchronized Set<String> klausurAnmelden(String studentHandle, Long klausurId) {
        StudentServiceHilfsMethoden hilfsMethoden = new StudentServiceHilfsMethoden(klausurRepository);
        KlausurValidierung klausurValidierung = new KlausurValidierung();
        Student student = studentRepository.studentMitHandle(studentHandle);
        Klausur klausurAusDb = klausurRepository.klausurMitId(klausurId);

        List<Klausur> klausurenDerStudentAmTag =
                hilfsMethoden.studentHatKlausurAnTag(holeAlleKlausurenMitID(student),
                        klausurAusDb.datum().toLocalDate());

        if(klausurValidierung.keineKlausurUeberschneidung(klausurenDerStudentAmTag,klausurAusDb)) {
            student.addKlausur(klausurAusDb);
            List<Urlaub> urlaube = hilfsMethoden.findeUrlaubeAmSelbenTag(student, klausurAusDb.datum().toLocalDate());
            urlaube = urlaubKlausurBearbeitung.reduziereUrlaubDurchEineKlausur(urlaube, urlaubKlausurBearbeitung.freieZeitDurchKlausur(klausurAusDb));
            fuegeUrlaubeZusammen(klausurAusDb.datum().toLocalDate(), student, urlaube);
        }
        return klausurValidierung.getFehlgeschlagen();
    }

    public boolean urlaubStornieren(String studentHandle, UrlaubDto urlaubDto) {
        UrlaubValidierung urlaubValidierung = new UrlaubValidierung();
        Urlaub urlaub = UrlaubDto.toUrlaub(urlaubDto);
        boolean ergebnis = false;
        Student student = studentRepository.studentMitHandle(studentHandle);

        if (urlaubValidierung.urlaubNurVorDemTagDesUrlaubs(urlaub)) {
            ergebnis = student.urlaubStornieren(urlaub.datum(), urlaub.startzeit(), urlaub.endzeit());
            student.berechneRestUrlaub();
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
    // TODO urlaubExistiert schon prüfen
    private void urlaubHinzufuegenOhneKlausur(Urlaub urlaub, UrlaubValidierung urlaubValidierung, Student student, List<Urlaub> urlaubeAnTag) {
        System.out.println(urlaubeAnTag.size() == 1);
        if (urlaubeAnTag.size() == 1 && (urlaubValidierung.zweiUrlaubeAnEinemTag(urlaub, urlaubeAnTag.get(0)))) {
            System.out.println("oops");
            fuegeUrlaubHinzu(student, urlaub);
        } else if (!student.urlaubExistiertSchon(urlaub.datum(), urlaub.startzeit(), urlaub.endzeit())) {
            System.out.println("ja");
            fuegeUrlaubHinzu(student, urlaub);
        }
        System.out.println(urlaub);
    }

    public void fuegeUrlaubeZusammen(LocalDate datum, Student student, List<Urlaub> urlaube) {
        StudentServiceHilfsMethoden hilfsMethoden = new StudentServiceHilfsMethoden(klausurRepository);
        hilfsMethoden.storniereAlleUrlaubeAnTag(student,datum);
        for(Urlaub urlaub : urlaube){
            student.addUrlaub(urlaub.datum(),urlaub.startzeit(),urlaub.endzeit());
        }
        student.berechneRestUrlaub();
        studentRepository.save(student);
    }
}
