package de.hhu.propra.application.services;

import de.hhu.propra.application.dto.KlausurDto;
import de.hhu.propra.application.dto.UrlaubDto;
import de.hhu.propra.application.repositories.KlausurRepository;
import de.hhu.propra.application.repositories.StudentRepository;
import de.hhu.propra.application.stereotypes.ApplicationService;
import de.hhu.propra.domain.aggregates.klausur.Klausur;
import de.hhu.propra.domain.aggregates.student.Student;


@ApplicationService
public class StudentService {
    private final StudentRepository studentRepository;
    private final KlausurRepository klausurRepository;

    public StudentService(StudentRepository studentRepository, KlausurRepository klausurRepository) {
        this.studentRepository = studentRepository;
        this.klausurRepository = klausurRepository;
    }

    public void urlaubAnlegen(Long studentId, UrlaubDto urlaubDto){

        //TODO :: VERIFY INPUT
        Student student  = studentRepository.studentMitId(studentId);

        //TODO :: CHANGE DATA
        if(!student.urlaubExistiert(urlaubDto.datum(),urlaubDto.startzeit(),urlaubDto.endzeit())) {
            student.addUrlaub(urlaubDto.datum(), urlaubDto.startzeit(), urlaubDto.endzeit());
            studentRepository.save(student);
        }
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
        student.urlaubStornieren(urlaubDto.datum(),urlaubDto.startzeit(),urlaubDto.endzeit());
        studentRepository.save(student);
    }

}
