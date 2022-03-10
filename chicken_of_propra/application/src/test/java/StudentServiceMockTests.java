import de.hhu.propra.application.dto.KlausurDto;
import de.hhu.propra.application.dto.UrlaubDto;
import de.hhu.propra.application.repositories.KlausurRepository;
import de.hhu.propra.application.repositories.StudentRepository;
import de.hhu.propra.application.services.StudentService;
import de.hhu.propra.domain.aggregates.klausur.Klausur;
import de.hhu.propra.domain.aggregates.student.Student;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class StudentRepositoryMockTests {


    @Test
    @DisplayName("Urlaub wird erstellt und zu student angelegt")
    void test1(){
        //TODO :: CHANGE THIS
        //arrange
        UrlaubDto urlaubDto = new UrlaubDto(LocalDate.now(), LocalTime.now(),LocalTime.now());
        Student student = new Student(1L,"x");
        StudentRepository studentRepository = mock(StudentRepository.class);
        KlausurRepository klausurRepository = mock(KlausurRepository.class);
        when(studentRepository.studentMitId(1L)).thenReturn(student);
        StudentService service = new StudentService(studentRepository, klausurRepository);
        //act
        service.urlaubAnlegen(1L,urlaubDto);
        //assert
        assertThat(student.getUrlaube()).hasSize(1);
        verify(studentRepository).save(student);
    }

    @Test
    @DisplayName("Duplikate werden nicht eingefügt")
    void test2(){
        //arrange
        UrlaubDto urlaubDto = new UrlaubDto(LocalDate.now(), LocalTime.now(),LocalTime.now());
        Student student = new Student(1L,"x");
        StudentRepository studentRepository = mock(StudentRepository.class);
        KlausurRepository klausurRepository = mock(KlausurRepository.class);
        when(studentRepository.studentMitId(1L)).thenReturn(student);
        StudentService service = new StudentService(studentRepository, klausurRepository);
        //act
        service.urlaubAnlegen(1L,urlaubDto);
        service.urlaubAnlegen(1L,urlaubDto);
        //assert
        assertThat(student.getUrlaube()).hasSize(1);
    }

    @Test
    @DisplayName("Klausur wird erstellt")
    void test3(){
        KlausurDto klausurDto = new KlausurDto("BS", LocalDateTime.now(),240,217480,false);
        Klausur klausur = new Klausur(null,
                klausurDto.name(),
                klausurDto.datum(),
                klausurDto.dauer(),
                klausurDto.lsf(),
                klausurDto.online());
        StudentRepository studentRepository = mock(StudentRepository.class);
        KlausurRepository klausurRepository = mock(KlausurRepository.class);

        StudentService service = new StudentService(studentRepository,klausurRepository);
        service.klausurErstellen(klausurDto);

        verify(klausurRepository).save(klausur);
    }

    @Test
    @DisplayName("Student für Klausur angemeldet")
    void test4(){

        Student student = new Student(1L,"x");
        Klausur klausur = new Klausur(1L,"BS",LocalDateTime.now(),120,217480,false);

        StudentRepository studentRepository = mock(StudentRepository.class);
        KlausurRepository klausurRepository = mock(KlausurRepository.class);
        when(studentRepository.studentMitId(1L)).thenReturn(student);
        when(klausurRepository.klausurMitId(1L)).thenReturn(klausur);

        StudentService service = new StudentService(studentRepository,klausurRepository);
        service.klausurAnmelden(1L,1L);
        verify(studentRepository).save(student);
    }

    @Test
    @DisplayName("Stornierung erfolgeich")

    void test5(){

        UrlaubDto urlaubDto = new UrlaubDto(LocalDate.now(), LocalTime.now(),LocalTime.now());
        UrlaubDto urlaubDto1 = new UrlaubDto(LocalDate.of(10,10,10),LocalTime.of(10,10),LocalTime.of(20,20));
        Student student = new Student(1L,"x");
        StudentRepository studentRepository = mock(StudentRepository.class);
        KlausurRepository klausurRepository = mock(KlausurRepository.class);
        when(studentRepository.studentMitId(1L)).thenReturn(student);
        StudentService service = new StudentService(studentRepository, klausurRepository);
        student.addUrlaub(urlaubDto.datum(),urlaubDto.startzeit(),urlaubDto.endzeit());
        student.addUrlaub(urlaubDto1.datum(),urlaubDto1.startzeit(),urlaubDto1.endzeit());
        service.urlaubStornieren(1L,urlaubDto);

        verify(studentRepository).save(student);
        assertThat(student.getUrlaube()).hasSize(1);
    }

}
