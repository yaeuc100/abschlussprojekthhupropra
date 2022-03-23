package de.hhu.propra.application.services;

import de.hhu.propra.application.dto.KlausurDto;
import de.hhu.propra.application.dto.UrlaubDto;
import de.hhu.propra.application.repositories.AuditLogRepository;
import de.hhu.propra.application.repositories.KlausurRepository;
import de.hhu.propra.application.repositories.StudentRepository;
import de.hhu.propra.application.stereotypes.ApplicationService;
import de.hhu.propra.application.utils.*;
import de.hhu.propra.domain.aggregates.klausur.Klausur;
import de.hhu.propra.domain.aggregates.student.Student;
import de.hhu.propra.domain.aggregates.student.Urlaub;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@ApplicationService
public class StudentService {
    private final StudentRepository studentRepository;
    private final KlausurRepository klausurRepository;
    private final AuditLogRepository auditLogRepository;

    private UrlaubKlausurBearbeitung urlaubKlausurBearbeitung = new UrlaubKlausurBearbeitung();


    public StudentService(StudentRepository studentRepository, KlausurRepository klausurRepository, AuditLogRepository auditLogRepository) {
        this.studentRepository = studentRepository;
        this.klausurRepository = klausurRepository;
        this.auditLogRepository = auditLogRepository;
    }

    public Student studentMitHandle(String handle){
        return studentRepository.studentMitHandle(handle);
    }

    public void createStudent(String handle){
        Student student = new Student(null,handle);
        studentRepository.save(student);
    }

    public Set<String> urlaubAnlegen(String studentHandle, UrlaubDto urlaubDto) {
        StudentServiceHilfsMethoden hilfsMethoden = new StudentServiceHilfsMethoden();
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
        UrlaubValidierung urlaubValidierung = new UrlaubValidierung();
        if (urlaubValidierung.genugUrlaub(student, urlaub)) {
            student.addUrlaub(urlaub.datum(), urlaub.startzeit(), urlaub.endzeit());
            student.berechneRestUrlaub();
            auditLogRepository.save(AuditLogErzeugung.urlaubHinzugefuegt(student.getHandle(), urlaub));
            studentRepository.save(student);
            return true;
        }
        return false;
    }

    public synchronized Set<String> klausurErstellen(String studentHandle, KlausurDto klausurDto) throws IOException {
        KlausurValidierung klausurValidierung = new KlausurValidierung();

        Klausur klausur = KlausurDto.toKlausur(klausurDto);
        List<Klausur> klausuren = klausurRepository.alleKlausuren();

        if (!klausurValidierung.klausurLiegtInDb(klausuren,klausur) ) {
            if(klausurValidierung.klausurIstValide(klausurDto)){
                klausurRepository.save(klausur);
                Klausur klausurAusDb = klausurRepository.klausurMitDaten(klausurDto);
                auditLogRepository.save(AuditLogErzeugung.klausurErstellen(studentHandle, klausurAusDb));
            }
        }
        return klausurValidierung.getFehlgeschlagen();
    }

    public synchronized Set<String> klausurAnmelden(String studentHandle, Long klausurId) {
        StudentServiceHilfsMethoden hilfsMethoden = new StudentServiceHilfsMethoden();
        KlausurValidierung klausurValidierung = new KlausurValidierung();
        Student student = studentRepository.studentMitHandle(studentHandle);
        Klausur klausurAusDb = klausurRepository.klausurMitId(klausurId);

        List<Klausur> klausurenDerStudentAmTag =
                hilfsMethoden.studentHatKlausurAnTag(holeAlleKlausurenMitID(student),
                        klausurAusDb.datum().toLocalDate());

        if(klausurValidierung.keineKlausurUeberschneidung(klausurenDerStudentAmTag,klausurAusDb)) {
            student.addKlausur(klausurAusDb);
            auditLogRepository.save(AuditLogErzeugung.klausurAnmelden(studentHandle, klausurAusDb));
            List<Urlaub> urlaube = hilfsMethoden.findeUrlaubeAmSelbenTag(student, klausurAusDb.datum().toLocalDate());
            urlaube = urlaubKlausurBearbeitung.reduziereUrlaubDurchEineKlausur(urlaube, urlaubKlausurBearbeitung.freieZeitDurchKlausur(klausurAusDb));
            fuegeUrlaubeZusammen(klausurAusDb.datum().toLocalDate(), student, urlaube);
        }
        return klausurValidierung.getFehlgeschlagen();
    }

    public Set<String> urlaubStornieren(String studentHandle, UrlaubDto urlaubDto) {
        UrlaubValidierung urlaubValidierung = new UrlaubValidierung();
        Urlaub urlaub = UrlaubDto.toUrlaub(urlaubDto);
        boolean ergebnis = true;
        Student student = studentRepository.studentMitHandle(studentHandle);

        if (urlaubValidierung.urlaubNurVorDemTagDesUrlaubsStornieren(urlaub)) {
            ergebnis = student.urlaubStornieren(urlaub.datum(), urlaub.startzeit(), urlaub.endzeit());
            student.berechneRestUrlaub();
            auditLogRepository.save(AuditLogErzeugung.urlaubStorniert(studentHandle, urlaubDto));
            studentRepository.save(student);
        }
        Set<String> fehlermeldungen = urlaubValidierung.getFehlgeschlagen();
        if(!ergebnis){
            fehlermeldungen.add("Urlaub liegt nicht im Datenbank");
        }
        return fehlermeldungen;
    }


    public void klausurStornieren(String studentHandle, Klausur klausur) {
        Student student = studentRepository.studentMitHandle(studentHandle);
        student.klausurStornieren(klausur);
        auditLogRepository.save(AuditLogErzeugung.klausurStorniert(studentHandle, klausur));
        studentRepository.save(student);
    }

    private List<Klausur> holeAlleKlausurenMitID(Student student) {
        return student.getKlausuren().stream()
                .map(klausurRepository::klausurMitId)
                .collect(Collectors.toList());
    }
    public HashMap<Long,KlausurDto> holeAlleKlausurDtosMitID(Student student) {
        HashMap<Long,KlausurDto> dtos = new HashMap<>();
         holeAlleKlausurenMitID(student).stream().forEach(k->dtos.put(k.id(),KlausurDto.toKlausurDto(k)));
         return dtos;

    }

    // TODO urlaubExistiert schon prüfen
    private void urlaubHinzufuegenOhneKlausur(Urlaub urlaub, UrlaubValidierung urlaubValidierung, Student student, List<Urlaub> urlaubeAnTag) {
        if (urlaubeAnTag.size() == 1 && (urlaubValidierung.zweiUrlaubeAnEinemTag(urlaub, urlaubeAnTag.get(0)))) {
            fuegeUrlaubHinzu(student, urlaub);
        } else if (!student.urlaubExistiertSchon(urlaub.datum(), urlaub.startzeit(), urlaub.endzeit())) {
            fuegeUrlaubHinzu(student, urlaub);
        }
    }

    public void fuegeUrlaubeZusammen(LocalDate datum, Student student, List<Urlaub> urlaube) {
        StudentServiceHilfsMethoden hilfsMethoden = new StudentServiceHilfsMethoden();
        logsZufuegeUrlaubeZusammen(datum, student, urlaube);
        hilfsMethoden.storniereAlleUrlaubeAnTag(student,datum);
        for(Urlaub urlaub : urlaube){
            student.addUrlaub(urlaub.datum(),urlaub.startzeit(),urlaub.endzeit());
        }
        student.berechneRestUrlaub();
        studentRepository.save(student);
    }

    private void logsZufuegeUrlaubeZusammen(LocalDate datum, Student student, List<Urlaub> urlaube){
        StudentServiceHilfsMethoden hilfsMethoden = new StudentServiceHilfsMethoden();
        List<Urlaub> stornierte = hilfsMethoden.stornierteUrlaube(hilfsMethoden.findeUrlaubeAmSelbenTag(student, datum), urlaube);
        List<Urlaub> neue = hilfsMethoden.neueUrlaube(hilfsMethoden.findeUrlaubeAmSelbenTag(student, datum), urlaube);
        if(!stornierte.isEmpty()){
            for(Urlaub urlaub : stornierte){
                auditLogRepository.save(AuditLogErzeugung.urlaubStorniert(student.getHandle(), UrlaubDto.toUrlaubDto(urlaub)));
            }
        }
        if(!neue.isEmpty()){
            for(Urlaub urlaub : neue){
                auditLogRepository.save(AuditLogErzeugung.urlaubHinzugefuegt(student.getHandle(), urlaub));
            }
        }
    }
}
