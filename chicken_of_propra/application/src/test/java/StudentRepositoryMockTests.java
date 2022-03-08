import de.hhu.propra.application.dto.UrlaubDto;
import de.hhu.propra.application.repositories.AnwesenheitRepository;
import de.hhu.propra.application.repositories.StudentRepository;
import de.hhu.propra.application.repositories.UrlaubRepository;
import de.hhu.propra.application.services.StudentService;
import de.hhu.propra.domain.aggregates.student.Student;
import de.hhu.propra.domain.aggregates.student.Urlaub;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.mockito.Mockito.*;

public class StudentRepositoryMockTests {


    @Test
    @DisplayName("Urlaub wird erstellt und zu student angelegt")
    void test1(){
        UrlaubDto urlaubDto = new UrlaubDto(LocalDate.now(), LocalTime.now(),LocalTime.now());
        Urlaub urlaub = new Urlaub(null, LocalDate.now(), LocalTime.now(),LocalTime.now());
        Student student = new Student(1L,"x",240);
        UrlaubRepository urlaubRepository = mock(UrlaubRepository.class);
        AnwesenheitRepository anwesenheitRepository = mock(AnwesenheitRepository.class);
        StudentRepository studentRepository = mock(StudentRepository.class);
        when(studentRepository.studentMitId(1L)).thenReturn(student);
        StudentService service = new StudentService(studentRepository,anwesenheitRepository,urlaubRepository);
        service.urlaubAnlegen(1L,urlaubDto);
        verify(urlaubRepository).save(urlaub);
        verify(studentRepository).save(student);

    }


}
