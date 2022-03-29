package de.hhu.propra.application.services;

import de.hhu.propra.application.dto.KlausurDto;
import de.hhu.propra.application.dto.UrlaubDto;
import de.hhu.propra.application.repositories.AuditLogRepository;
import de.hhu.propra.application.repositories.KlausurRepository;
import de.hhu.propra.application.repositories.StudentRepository;
import de.hhu.propra.application.stereotypes.ApplicationService;
import de.hhu.propra.application.utils.AuditLogErzeugung;
import de.hhu.propra.application.utils.KlausurValidierung;
import de.hhu.propra.application.utils.StudentServiceHilfsMethoden;
import de.hhu.propra.application.utils.UrlaubKlausurBearbeitung;
import de.hhu.propra.application.utils.UrlaubValidierung;
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
  private StudentServiceHilfsMethoden hilfsMethoden = new StudentServiceHilfsMethoden();

  public StudentService(
      StudentRepository studentRepository,
      KlausurRepository klausurRepository,
      AuditLogRepository auditLogRepository) {
    this.studentRepository = studentRepository;
    this.klausurRepository = klausurRepository;
    this.auditLogRepository = auditLogRepository;
  }

  public Student studentMitHandle(String handle) {
    return studentRepository.studentMitHandle(handle);
  }
  
  public void erstelleStudent(String handle) {
    Student student = new Student(null, handle);
    studentRepository.save(student);
  }

// legt Urlaub an und gibt anschließend (ggf. leeres) Set mit potentiellen Fehlermeldungen zurück
  public Set<String> urlaubAnlegen(String studentHandle, UrlaubDto urlaubDto) {
    UrlaubValidierung urlaubValidierung = new UrlaubValidierung();
    Urlaub urlaub = UrlaubDto.toUrlaub(urlaubDto);
    if (urlaub == null) {
      urlaubValidierung.datumUngueltig();
      return urlaubValidierung.getFehlgeschlagen();
    }
    Student student = studentRepository.studentMitHandle(studentHandle);
    List<Urlaub> urlaubeAnTag = hilfsMethoden.findeUrlaubeAmSelbenTag(student, urlaub.datum());
    List<Klausur> klausurenVonStudentAnTag = holeKlausurenVonStudentAnTag(urlaub, student);

    if(pruefeUrlaubMoeglich(urlaub, student, urlaubValidierung)) {
      if (!klausurenVonStudentAnTag.isEmpty()) {
        urlaubHinzufuegenMitKlausuren(urlaubValidierung, urlaub, student, urlaubeAnTag, klausurenVonStudentAnTag);
      } else if (urlaubValidierung.bisherMaxEinUrlaub(urlaubeAnTag)) {
        urlaubHinzufuegenOhneKlausur(urlaub, urlaubValidierung, student, urlaubeAnTag);
      }
    }
    return urlaubValidierung.getFehlgeschlagen();
  }

  private void urlaubHinzufuegenMitKlausuren(UrlaubValidierung urlaubValidierung, Urlaub urlaub, Student student,
      List<Urlaub> urlaubeAnTag, List<Klausur> klausurenVonStudentAnTag) {
    List<Urlaub> resultierendeUrlaube =
        urlaubKlausurBearbeitung.urlaubKlausurBearbeitung(urlaub, klausurenVonStudentAnTag);
    resultierendeUrlaube.addAll(urlaubeAnTag);
    resultierendeUrlaube = urlaubValidierung.urlaubeZusammenfuegen(resultierendeUrlaube);
    fuegeUrlaubeZusammen(urlaub.datum(), student, resultierendeUrlaube);
  }


  private List<Klausur> holeKlausurenVonStudentAnTag(Urlaub urlaub, Student student) {
    List<Klausur> klausurenVonStudent = holeAlleKlausurenMitId(student);
    return hilfsMethoden.studentHatKlausurAnTag(klausurenVonStudent, urlaub.datum());
  }


  private boolean pruefeUrlaubMoeglich(Urlaub urlaub, Student student, UrlaubValidierung urlaubValidierung){
    return urlaubValidierung.urlaubIstValide(urlaub) && urlaubValidierung.genugUrlaub(student, urlaub);
  }


  public boolean fuegeUrlaubHinzu(Student student, Urlaub urlaub) {
    UrlaubValidierung urlaubValidierung = new UrlaubValidierung();
    if (urlaubValidierung.genugUrlaub(student, urlaub)) {
      student.addUrlaub(urlaub.datum(), urlaub.startzeit(), urlaub.endzeit());
      student.berechneResturlaub();
      auditLogRepository.save(AuditLogErzeugung.urlaubHinzugefuegt(student.getHandle(), urlaub));
      studentRepository.save(student);
      return true;
    }
    return false;
  }


  public synchronized Set<String> klausurErstellen(String studentHandle, KlausurDto klausurDto)
      throws IOException {
    KlausurValidierung klausurValidierung = new KlausurValidierung();
    Klausur klausur = KlausurDto.toKlausur(klausurDto);
    if (klausur == null) {
      klausurValidierung.datumUngueltig();
      return klausurValidierung.getFehlgeschlagen();
    }
    List<Klausur> klausuren = klausurRepository.alleKlausuren();

    if (!klausurValidierung.klausurLiegtInDb(klausuren, klausur)) {
      if (klausurValidierung.klausurIstValide(klausurDto)) {
        klausurRepository.save(klausur);
        Klausur klausurAusDb = klausurRepository.klausurMitDaten(klausurDto);
        auditLogRepository.save(AuditLogErzeugung.klausurErstellen(studentHandle, klausurAusDb));
      }
    }
    return klausurValidierung.getFehlgeschlagen();
  }

  
   // prueft, ob die beantragte Klausur valide ist. Falls ja, wird eine Klausurreferenz beim Studenten eingefügt
  public synchronized Set<String> klausurAnmelden(String studentHandle, Long klausurId) {
    KlausurValidierung klausurValidierung = new KlausurValidierung();
    Student student = studentRepository.studentMitHandle(studentHandle);
    Klausur klausurAusDb = klausurRepository.klausurMitId(klausurId);

    List<Klausur> klausurenDerStudentAmTag =
        hilfsMethoden.studentHatKlausurAnTag(
            holeAlleKlausurenMitId(student), klausurAusDb.datum().toLocalDate());

    if (klausurValidierung.keineKlausurUeberschneidung(klausurenDerStudentAmTag, klausurAusDb)) {
      student.addKlausur(klausurAusDb);
      auditLogRepository.save(AuditLogErzeugung.klausurAnmelden(studentHandle, klausurAusDb));
      List<Urlaub> urlaube =
          hilfsMethoden.findeUrlaubeAmSelbenTag(student, klausurAusDb.datum().toLocalDate());
      urlaube =
          urlaubKlausurBearbeitung.reduziereUrlaubDurchEineKlausur(
              urlaube, urlaubKlausurBearbeitung.freieZeitDurchKlausur(klausurAusDb));
      fuegeUrlaubeZusammen(klausurAusDb.datum().toLocalDate(), student, urlaube);
    }
    return klausurValidierung.getFehlgeschlagen();
  }
  
   // wenn der Urlaub existent ist und der Antrag rechtzeitig, wird der Urlaub storniert
  public Set<String> urlaubStornieren(String studentHandle, UrlaubDto urlaubDto) {
    UrlaubValidierung urlaubValidierung = new UrlaubValidierung();
    Urlaub urlaub = UrlaubDto.toUrlaub(urlaubDto);
    boolean ergebnis = true;
    Student student = studentRepository.studentMitHandle(studentHandle);

    if (urlaubValidierung.urlaubNurVorDemTagDesUrlaubsStornieren(urlaub)) {
      ergebnis = student.urlaubStornieren(urlaub.datum(), urlaub.startzeit(), urlaub.endzeit());
      student.berechneResturlaub();
      auditLogRepository.save(AuditLogErzeugung.urlaubStorniert(studentHandle, urlaubDto));
      studentRepository.save(student);
    }
    Set<String> fehlermeldungen = urlaubValidierung.getFehlgeschlagen();
    if (!ergebnis) {
      fehlermeldungen.add("Urlaub liegt nicht im Datenbank");
    }
    return fehlermeldungen;
  }
  
  
  public void klausurStornieren(String studentHandle, Klausur klausur) {
    KlausurValidierung klausurValidierung = new KlausurValidierung();
    if (klausurValidierung
        .klausurNurVorDemTagDerKlausurStornieren(KlausurDto.toKlausurDto(klausur))) {
      Student student = studentRepository.studentMitHandle(studentHandle);
      student.klausurStornieren(klausur);
      auditLogRepository.save(AuditLogErzeugung.klausurStorniert(studentHandle, klausur));
      studentRepository.save(student);
    }
  }


  private List<Klausur> holeAlleKlausurenMitId(Student student) {
    return student.getKlausuren().stream()
        .map(klausurRepository::klausurMitId)
        .collect(Collectors.toList());
  }
  
  
  public HashMap<Long, KlausurDto> holeAlleKlausurdtosMitId(Student student) {
    HashMap<Long, KlausurDto> dtos = new HashMap<>();
    holeAlleKlausurenMitId(student)
        .forEach(k -> dtos.put(k.id(), KlausurDto.toKlausurDto(k)));
    return dtos;
  }

   
  private void urlaubHinzufuegenOhneKlausur(
      Urlaub urlaub,
      UrlaubValidierung urlaubValidierung,
      Student student,
      List<Urlaub> urlaubeAnTag) {
    if (urlaubeAnTag.size() == 1
        && (urlaubValidierung.zweiUrlaubeAnEinemTag(urlaub, urlaubeAnTag.get(0)))) {
      fuegeUrlaubHinzu(student, urlaub);
    } else if (!student.urlaubExistiertSchon(urlaub.datum(), urlaub.startzeit(), urlaub.endzeit())
        && urlaubeAnTag.size() < 1) {
      fuegeUrlaubHinzu(student, urlaub);
    }
  }

  
   // fügt alle beantragten Urlaube eines Studenten zu einer Liste mit disjunkten Urlaubsobjekten zusammen
  public void fuegeUrlaubeZusammen(LocalDate datum, Student student, List<Urlaub> urlaube) {
    logsZufuegeUrlaubeZusammen(datum, student, urlaube);
    hilfsMethoden.storniereAlleUrlaubeAnTag(student, datum);
    for (Urlaub urlaub : urlaube) {
      student.addUrlaub(urlaub.datum(), urlaub.startzeit(), urlaub.endzeit());
    }
    student.berechneResturlaub();
    studentRepository.save(student);
  }

  private void logsZufuegeUrlaubeZusammen(LocalDate datum, Student student, List<Urlaub> urlaube) {
    List<Urlaub> stornierte =
        hilfsMethoden.stornierteUrlaube(
            hilfsMethoden.findeUrlaubeAmSelbenTag(student, datum), urlaube);
    List<Urlaub> neue =
        hilfsMethoden.neueUrlaube(hilfsMethoden.findeUrlaubeAmSelbenTag(student, datum), urlaube);
    if (!stornierte.isEmpty()) {
      for (Urlaub urlaub : stornierte) {
        auditLogRepository.save(
            AuditLogErzeugung.urlaubStorniert(student.getHandle(), UrlaubDto.toUrlaubDto(urlaub)));
      }
    }
    if (!neue.isEmpty()) {
      for (Urlaub urlaub : neue) {
        auditLogRepository.save(AuditLogErzeugung.urlaubHinzugefuegt(student.getHandle(), urlaub));
      }
    }
  }
}
