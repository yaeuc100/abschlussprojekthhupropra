import de.hhu.propra.application.dto.UrlaubDto;
import de.hhu.propra.application.repositories.KlausurRepository;
import de.hhu.propra.application.repositories.StudentRepository;
import de.hhu.propra.application.services.StudentService;
import de.hhu.propra.domain.aggregates.klausur.Klausur;
import de.hhu.propra.domain.aggregates.student.Student;
import de.hhu.propra.domain.aggregates.student.Urlaub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class StudentServiceTests {


    private KlausurRepository klausurRepository;
    private StudentRepository studentRepository;
    private StudentService studentService;

    @BeforeEach
    void vorbereiten(){
        this.klausurRepository = mock(KlausurRepository.class);
        this.studentRepository = mock(StudentRepository.class);
        this.studentService = new StudentService(studentRepository, klausurRepository);

    }

    @Test
    @DisplayName("Urlaub wird zu Student hinzugefuegt")
    void test1(){
        //arrange
        UrlaubDto urlaubDto = new UrlaubDto(LocalDate.of(3000,1,1), LocalTime.of(10,30),LocalTime.of(11,30));
        Student student = new Student(1L,"x");
        when(studentRepository.studentMitId(1L)).thenReturn(student);

        //act
        boolean ergebnis = studentService.urlaubAnlegen(student.getId(), urlaubDto);

        //assert
        assertThat(student.getUrlaube()).hasSize(1);
        assertThat(ergebnis).isTrue();


    }

    @Test
    @DisplayName("Derselbe Urlaub wird nur einmal pro Student eingetragen")
    void test2(){
        //arrange
        UrlaubDto urlaubDto = new UrlaubDto(LocalDate.of(3000,1,1), LocalTime.of(10,30),LocalTime.of(11,30));
        Student student = new Student(1L,"x");
        when(studentRepository.studentMitId(1L)).thenReturn(student);

        //act
        studentService.urlaubAnlegen(student.getId(), urlaubDto);
        boolean ergebnis = studentService.urlaubAnlegen(student.getId(), urlaubDto);

        //assert
        assertThat(student.getUrlaube()).hasSize(1);
        assertThat(ergebnis).isFalse();
    }

    @Test
    @DisplayName("Save Methode wird beim hinzufuegen eines Urlaubes aufgerufen")
    void test3(){
        //arrange
        UrlaubDto urlaubDto = new UrlaubDto(LocalDate.of(3000,1,1), LocalTime.of(10,30),LocalTime.of(11,30));
        Student student = new Student(1L,"x");
        when(studentRepository.studentMitId(1L)).thenReturn(student);

        //act
        studentService.urlaubAnlegen(student.getId(), urlaubDto);

        //assert
        verify(studentRepository, Mockito.times(1)).save(student);

    }

    @Test
    @DisplayName("Beim invalidem Urlaub wird die Save Methode nicht aufgerufen")
    void test4(){
        //arrange
        UrlaubDto urlaubDto = new UrlaubDto(LocalDate.of(2000,1,1), LocalTime.of(10,30),LocalTime.of(11,30));
        Student student = new Student(1L,"x");
        when(studentRepository.studentMitId(1L)).thenReturn(student);

        //act
        studentService.urlaubAnlegen(student.getId(), urlaubDto);

        //assert
        verify(studentRepository, Mockito.times(0)).save(student);
    }

    @Test
    @DisplayName("Bei Hinzufuegen eines Urlaubs wird der Resturlaub angepasst")
    void test5(){
        //arrange
        UrlaubDto urlaubDto = new UrlaubDto(LocalDate.of(3000,1,1), LocalTime.of(10,30),LocalTime.of(14,30));
        Student student = new Student(1L,"x");
        when(studentRepository.studentMitId(1L)).thenReturn(student);

        //act
        studentService.urlaubAnlegen(student.getId(), urlaubDto);

        //assert
        assertThat(student.getResturlaub()).isEqualTo(0);
    }

    @Test
    @DisplayName("Beim ungenuegendem Resturlaub wird die Save Methode nicht aufgerufen")
    void test6(){
        //arrange
        UrlaubDto urlaubDto1 = new UrlaubDto(LocalDate.of(3000,1,1), LocalTime.of(10,30),LocalTime.of(13,30));
        UrlaubDto urlaubDto2 = new UrlaubDto(LocalDate.of(3000,1,2), LocalTime.of(10,30),LocalTime.of(12,00));
        Student student = new Student(1L,"x");
        when(studentRepository.studentMitId(1L)).thenReturn(student);

        //act
        studentService.urlaubAnlegen(student.getId(), urlaubDto1);
        studentService.urlaubAnlegen(student.getId(), urlaubDto2);

        //assert
        verify(studentRepository, Mockito.times(1)).save(student);
    }



    @Test
    @DisplayName("Es werden zwei gueltige Urlaube an einem Tag hinzugefuegt")
    void test7(){
        //arrange
        UrlaubDto urlaubDto1 = new UrlaubDto(LocalDate.of(3000,1,1), LocalTime.of(8,30),LocalTime.of(10,30));
        UrlaubDto urlaubDto2 = new UrlaubDto(LocalDate.of(3000,1,1), LocalTime.of(12,00),LocalTime.of(12,30));
        Student student = new Student(1L,"x");
        when(studentRepository.studentMitId(1L)).thenReturn(student);

        //act
        studentService.urlaubAnlegen(student.getId(), urlaubDto1);
        boolean ergebnis = studentService.urlaubAnlegen(student.getId(), urlaubDto2);

        //assert
        assertThat(student.getUrlaube()).hasSize(2);
        assertThat(ergebnis).isTrue();
    }

    @Test
    @DisplayName("Bei zwei Urlauben an verschiedenen Tagen wird Resturlaub angepasst")
    void test8(){
        //arrange
        UrlaubDto urlaubDto1 = new UrlaubDto(LocalDate.of(3000,1,1), LocalTime.of(8,30),LocalTime.of(10,30));
        UrlaubDto urlaubDto2 = new UrlaubDto(LocalDate.of(3000,1,2), LocalTime.of(11,00),LocalTime.of(12,30));
        Student student = new Student(1L,"x");
        when(studentRepository.studentMitId(1L)).thenReturn(student);

        //act
        studentService.urlaubAnlegen(student.getId(), urlaubDto1);
        studentService.urlaubAnlegen(student.getId(), urlaubDto2);

        //assert
        assertThat(student.getResturlaub()).isEqualTo(30);
    }

    @Test
    @DisplayName("Es werden zwei gueltige Urlaube an verschiedenen Tagen hinzugefuegt")
    void test9(){
        //arrange
        UrlaubDto urlaubDto1 = new UrlaubDto(LocalDate.of(3000,1,1), LocalTime.of(8,30),LocalTime.of(10,30));
        UrlaubDto urlaubDto2 = new UrlaubDto(LocalDate.of(3000,1,2), LocalTime.of(11,00),LocalTime.of(12,30));
        Student student = new Student(1L,"x");
        when(studentRepository.studentMitId(1L)).thenReturn(student);

        //act
        studentService.urlaubAnlegen(student.getId(), urlaubDto1);
        boolean ergebnis = studentService.urlaubAnlegen(student.getId(), urlaubDto2);

        //assert
        assertThat(student.getUrlaube()).hasSize(2);
        assertThat(ergebnis).isTrue();
    }

    @Test
    @DisplayName("Es werden zwei gueltige Urlaube an einem Tag und ein weiterer an einem anderen Tag hinzugefuegt")
    void test10(){
        //arrange
        UrlaubDto urlaubDto1 = new UrlaubDto(LocalDate.of(3000,1,1), LocalTime.of(8,30),LocalTime.of(10,00));
        UrlaubDto urlaubDto2 = new UrlaubDto(LocalDate.of(3000,1,2), LocalTime.of(10,00),LocalTime.of(12,00));
        UrlaubDto urlaubDto3 = new UrlaubDto(LocalDate.of(3000,1,1), LocalTime.of(12,00),LocalTime.of(12,30));
        Student student = new Student(1L,"x");
        when(studentRepository.studentMitId(1L)).thenReturn(student);

        //act
        studentService.urlaubAnlegen(student.getId(), urlaubDto1);
        studentService.urlaubAnlegen(student.getId(), urlaubDto2);
        boolean ergebnis = studentService.urlaubAnlegen(student.getId(), urlaubDto3);

        //assert
        assertThat(student.getUrlaube()).hasSize(3);
        assertThat(ergebnis).isTrue();
    }

    @Test
    @DisplayName("Es werden maximal 2 Urlaube an einem Tag hinzugefuegt")
    void test11(){
        //arrange
        UrlaubDto urlaubDto1 = new UrlaubDto(LocalDate.of(3000,1,1), LocalTime.of(8,30),LocalTime.of(8,45));
        UrlaubDto urlaubDto2 = new UrlaubDto(LocalDate.of(3000,1,1), LocalTime.of(12,00),LocalTime.of(12,30));
        UrlaubDto urlaubDto3 = new UrlaubDto(LocalDate.of(3000,1,1), LocalTime.of(10,15),LocalTime.of(10,30));
        Student student = new Student(1L,"x");
        when(studentRepository.studentMitId(1L)).thenReturn(student);

        //act
        studentService.urlaubAnlegen(student.getId(), urlaubDto1);
        studentService.urlaubAnlegen(student.getId(), urlaubDto2);
        boolean ergebnis = studentService.urlaubAnlegen(student.getId(), urlaubDto3);

        //assert
        assertThat(student.getUrlaube()).hasSize(2);
        assertThat(ergebnis).isFalse();
    }

    @Test
    @DisplayName("nach einer Stornierung wird den Resturlaub angepasst")
    void test12(){
        //arrange
        UrlaubDto urlaubDto1 = new UrlaubDto(LocalDate.of(3000,1,1), LocalTime.of(8,30),LocalTime.of(10,30));
        UrlaubDto urlaubDto2 = new UrlaubDto(LocalDate.of(3000,1,1), LocalTime.of(12,00),LocalTime.of(12,30));
        Student student = new Student(1L,"x");
        when(studentRepository.studentMitId(1L)).thenReturn(student);

        //act
        studentService.urlaubAnlegen(student.getId(), urlaubDto1);
        studentService.urlaubAnlegen(student.getId(), urlaubDto2);
        studentService.urlaubStornieren(student.getId(),urlaubDto2);

        //assert
        assertThat(student.getResturlaub()).isEqualTo(120);
    }

    @Test
    @DisplayName("Es werden zwei gueltige Urlaube an einem Tag hinzugefuegt und einer davon storniert")
    void test13(){
        //arrange
        UrlaubDto urlaubDto1 = new UrlaubDto(LocalDate.of(3000,1,1), LocalTime.of(8,30),LocalTime.of(10,30));
        UrlaubDto urlaubDto2 = new UrlaubDto(LocalDate.of(3000,1,1), LocalTime.of(12,00),LocalTime.of(12,30));
        Student student = new Student(1L,"x");
        when(studentRepository.studentMitId(1L)).thenReturn(student);

        //act
        studentService.urlaubAnlegen(student.getId(), urlaubDto1);
        studentService.urlaubAnlegen(student.getId(), urlaubDto2);
        boolean ergebnis = studentService.urlaubStornieren(student.getId(),urlaubDto2);

        //assert
        assertThat(student.getUrlaube()).hasSize(1);
        assertThat(ergebnis).isTrue();
    }


    @Test
    @DisplayName("Urlaub wird bei Stornierungantrag am selben Tag nicht storniert")
    void test14(){
        //arrange
        UrlaubDto urlaubDto1 = new UrlaubDto(LocalDate.now(), LocalTime.of(8,30),LocalTime.of(10,30));
        Student student = new Student(1L,"x");
        when(studentRepository.studentMitId(1L)).thenReturn(student);

        //act
        student.addUrlaub(urlaubDto1.datum(),urlaubDto1.startzeit(),urlaubDto1.endzeit());
        boolean ergebnis = studentService.urlaubStornieren(student.getId(),urlaubDto1);

        //assert
        assertThat(student.getUrlaube()).hasSize(1);
        assertThat(ergebnis).isFalse();
    }

    @Test
    @DisplayName("Wenn ein Urlaub nicht existiert wird false beim Stornieren zurück geliefert")
    void test15(){
        UrlaubDto urlaubDto1 = new UrlaubDto(LocalDate.of(3000,1,1), LocalTime.of(8,30),LocalTime.of(10,30));
        Student student = new Student(1L,"x");
        when(studentRepository.studentMitId(1L)).thenReturn(student);

        //act
        boolean ergebnis = studentService.urlaubStornieren(student.getId(),urlaubDto1);

        //assert
        assertThat(ergebnis).isFalse();
    }

    @Test
    @DisplayName("Nach einer Stornierung wird die save Methode aufgerufen")
    void test16(){
        //arrange
        UrlaubDto urlaubDto1 = new UrlaubDto(LocalDate.of(3000,1,1), LocalTime.of(8,30),LocalTime.of(10,30));
        UrlaubDto urlaubDto2 = new UrlaubDto(LocalDate.of(3000,1,1), LocalTime.of(12,00),LocalTime.of(12,30));
        Student student = new Student(1L,"x");
        when(studentRepository.studentMitId(1L)).thenReturn(student);

        //act
        student.addUrlaub(urlaubDto1.datum(),urlaubDto1.startzeit(),urlaubDto1.endzeit());
        student.addUrlaub(urlaubDto2.datum(),urlaubDto2.startzeit(),urlaubDto2.endzeit());
        studentService.urlaubStornieren(student.getId(),urlaubDto2);

        //assert
        verify(studentRepository, Mockito.times(1)).save(student);
    }

    @Test
    @DisplayName("Urlaub wird bei Stornierungantrag am selben Tag nicht storniert und nicht gespeichert")
    void test17(){
        //arrange
        UrlaubDto urlaubDto1 = new UrlaubDto(LocalDate.now(), LocalTime.of(8,30),LocalTime.of(10,30));
        Student student = new Student(1L,"x");
        when(studentRepository.studentMitId(1L)).thenReturn(student);

        //act
        student.addUrlaub(urlaubDto1.datum(),urlaubDto1.startzeit(),urlaubDto1.endzeit());
        studentService.urlaubStornieren(student.getId(),urlaubDto1);

        //assert
        verify(studentRepository, Mockito.times(0)).save(student);
    }

    @Test
    @DisplayName("Neue Klausur wird erstellt")
    void test18(){

    }


}
