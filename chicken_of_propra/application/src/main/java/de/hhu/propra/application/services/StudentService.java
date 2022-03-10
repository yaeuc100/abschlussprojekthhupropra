package de.hhu.propra.application.services;

import de.hhu.propra.application.dto.KlausurDto;
import de.hhu.propra.application.dto.UrlaubDto;
import de.hhu.propra.application.repositories.KlausurRepository;
import de.hhu.propra.application.repositories.StudentRepository;
import de.hhu.propra.application.stereotypes.ApplicationService;
import de.hhu.propra.application.utils.UrlaubsMethoden;
import de.hhu.propra.domain.aggregates.klausur.Klausur;
import de.hhu.propra.domain.aggregates.student.Student;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;


@ApplicationService
public class StudentService {
    private final StudentRepository studentRepository;
    private final KlausurRepository klausurRepository;
    private UrlaubsMethoden urlaubsMethoden = new UrlaubsMethoden();

    public StudentService(StudentRepository studentRepository, KlausurRepository klausurRepository) {
        this.studentRepository = studentRepository;
        this.klausurRepository = klausurRepository;
    }

    public void urlaubAnlegen(Long studentId, UrlaubDto urlaubDto) {
        Student student = studentRepository.studentMitId(studentId);
        //TODO :: VERIFY INPUT // VERIFY KLAUSUR
        List<UrlaubDto> urlaube = student.getUrlaube().stream()
                .filter(u -> u.datum().equals(urlaubDto.datum()))
                .map(u-> new UrlaubDto(u.datum(),u.startzeit(),u.endzeit()))
                .collect(Collectors.toList());
        if (urlaubsMethoden.urlaubIsValide(urlaubDto) && urlaube.size() < 2) {
            if((urlaube.size() == 1) && (urlaubsMethoden.zweiUrlaubeAnEinemTag(urlaubDto,urlaube.get(0)))){
                fuegeUrlaubHinzu(student, urlaubDto);
            }
            else if(!student.urlaubExistiert(urlaubDto.datum(), urlaubDto.startzeit(), urlaubDto.endzeit())) {
                fuegeUrlaubHinzu(student, urlaubDto);
            }
        }
    }
    //TODO: Notification for not enough holidays
    private void fuegeUrlaubHinzu( Student student, UrlaubDto urlaubDto) {
        if (genugUrlaub(student, urlaubDto)) {
            student.addUrlaub(urlaubDto.datum(), urlaubDto.startzeit(), urlaubDto.endzeit());
            studentRepository.save(student);
        }
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

    public synchronized void klausurErstellen(KlausurDto klausurDto){
        Klausur klausur = new Klausur(null,
                klausurDto.name(),
                klausurDto.datum(),
                klausurDto.dauer(),
                klausurDto.lsf(),
                klausurDto.online());
        //TODO :: no duplicates(online/offline)
        klausurRepository.save(klausur);
    }


    public synchronized void klausurAnmelden(Long studentId, Long klausurId){
        Student student = studentRepository.studentMitId(studentId);
        Klausur klausurAusDb = klausurRepository.klausurMitId(klausurId);
        student.addKlausur(klausurAusDb);
        studentRepository.save(student);
    }

    public void urlaubStornieren(Long studentId, UrlaubDto urlaubDto){
        Student student = studentRepository.studentMitId(studentId);
        if(urlaubsMethoden.urlaubNurVorDemTagDesUrlaubs(urlaubDto)) {
            student.urlaubStornieren(urlaubDto.datum(), urlaubDto.startzeit(), urlaubDto.endzeit());
            studentRepository.save(student);
        }
    }

}
