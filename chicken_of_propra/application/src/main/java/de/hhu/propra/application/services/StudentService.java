package de.hhu.propra.application.services;

import de.hhu.propra.application.dto.KlausurDto;
import de.hhu.propra.application.dto.UrlaubDto;
import de.hhu.propra.application.repositories.AnwesenheitRepository;
import de.hhu.propra.application.repositories.StudentRepository;
import de.hhu.propra.application.repositories.UrlaubRepository;
import de.hhu.propra.application.stereotypes.ApplicationService;
import de.hhu.propra.domain.aggregates.student.Student;
import de.hhu.propra.domain.aggregates.student.Urlaub;
import org.springframework.stereotype.Service;


@ApplicationService
public class StudentService {
    private final StudentRepository studentRepository;
    private final AnwesenheitRepository anwesenheitRepository;
    private final UrlaubRepository urlaubRepository;

    public StudentService(StudentRepository studentRepository, AnwesenheitRepository anwesenheitRepository, UrlaubRepository urlaubRepository) {
        this.studentRepository = studentRepository;
        this.anwesenheitRepository = anwesenheitRepository;
        this.urlaubRepository = urlaubRepository;
    }

    public void urlaubAnlegen(Long studentId, UrlaubDto urlaubDto){

        Urlaub urlaub = new Urlaub(null,
                urlaubDto.datum(),
                urlaubDto.startzeit(),
                urlaubDto.endzeit());

        //TODO :: no duplicates
        urlaubRepository.save(urlaub);
        Student student  = studentRepository.studentMitId(studentId);
        student.addUrlaub(urlaub);
        studentRepository.save(student);
    }

    public void klausurAnmelden(Long studentId, Long klausId){

    }

    public void urlaubStornieren(Long id){

    }

    public void klausurErstellen(KlausurDto klausurDto){

    }


}
