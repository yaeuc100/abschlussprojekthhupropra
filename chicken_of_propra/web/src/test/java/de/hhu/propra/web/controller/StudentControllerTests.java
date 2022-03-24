package de.hhu.propra.web.controller;


import de.hhu.propra.application.dto.KlausurDto;
import de.hhu.propra.application.services.KlausurService;
import de.hhu.propra.application.services.StudentService;
import de.hhu.propra.domain.aggregates.klausur.Klausur;
import de.hhu.propra.domain.aggregates.student.Student;
import de.hhu.propra.web.security.AuthenticationTemplate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.stream.Stream;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class StudentControllerTests {


    @Autowired
    MockMvc mockMvc;

    @MockBean
    StudentService studentService;
    @MockBean
    KlausurService klausurService;


    @Test
    @DisplayName("Studentseite wird richtig angezeigt")
    void test() throws Exception {
        MockHttpSession session = AuthenticationTemplate.studentSession();
        Student student = new Student(1L,"AlexStudent");

        when(studentService.studentMitHandle("AlexStudent")).thenReturn(student);
        mockMvc.perform(get("/student").session(session))
                .andExpect(status().isOk())
                .andExpect(model().attribute("student",studentService.studentMitHandle("AlexStudent")))
                .andExpect(model().attributeExists("klausuren"))
                .andExpect(model().attributeDoesNotExist("fehler"));
    }

    @Test
    @DisplayName("Studentseite wird richtig angezeigt")
    void test1() throws Exception {
        MockHttpSession session = AuthenticationTemplate.studentSession();
       // studentService.studentMitHandle()
        mockMvc.perform(get("/student").session(session))
                .andExpect(status().isOk())
                .andExpect(model().attribute("student",studentService.studentMitHandle("AlexStudent")))
                .andExpect(model().attributeExists("klausuren"))
                .andExpect(model().attributeDoesNotExist("fehler"));
    }


    private Stream<Klausur> klausuren(){
        Klausur klausur = new Klausur(1L,"a" ,
                LocalDateTime.of(2022,3,19,10,30),
                90,
                222916,
                true);

        Klausur zweiteKlausur = new Klausur(2L,"z" ,
                LocalDateTime.of(2022,3,19,10,30),
                90,
                222916,
                true);
        return Stream.of(klausur,zweiteKlausur);
    }


}
