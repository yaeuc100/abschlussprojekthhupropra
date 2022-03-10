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

public class StudentServiceMockTests {


    StudentService studentService;
/*
    @Test
    @DisplayName("Urlaub wird erstellt und zu student angelegt")
    void test1(){
        //TODO :: CHANGE THIS
        //arrange
        UrlaubDto urlaubDto = new UrlaubDto(LocalDate.now(), LocalTime.now(),LocalTime.now());
        Student student = new Student(1L,"x");


        //act

        //assert
        assertThat(student.getUrlaube()).hasSize(1);

    }

    @Test
    @DisplayName("Duplikate werden nicht eingefügt")
    void test2(){
        //arrange
        UrlaubDto urlaubDto = new UrlaubDto(LocalDate.now(), LocalTime.now(),LocalTime.now());
        Student student = new Student(1L,"x");


        //act

        //assert
        assertThat(student.getUrlaube()).hasSize(1);
    }

    @Test
    @DisplayName("Klausur wird erstellt")
    void test3(){
        //arrange
        KlausurDto klausurDto = new KlausurDto("BS", LocalDateTime.now(),240,217480,false);
        Klausur klausur = new Klausur(null,
                klausurDto.name(),
                klausurDto.datum(),
                klausurDto.dauer(),
                klausurDto.lsf(),
                klausurDto.online());


        //act

        //assert

    }

    @Test
    @DisplayName("Student für Klausur angemeldet")
    void test4(){
        //arrange
        Student student = new Student(1L,"x");
        Klausur klausur = new Klausur(1L,"BS",LocalDateTime.now(),120,217480,false);
        //act

        //assert




    }

    @Test
    @DisplayName("Stornierung erfolgeich")
    void test5(){
        //Arrange
        UrlaubDto urlaubDto = new UrlaubDto(LocalDate.of(2030,10,10),LocalTime.of(10,10),LocalTime.of(20,20));
        Student student = new Student(1L,"x");


        //Act

        //Assert

    }
*/
}
