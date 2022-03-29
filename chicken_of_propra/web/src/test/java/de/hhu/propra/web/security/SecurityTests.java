package de.hhu.propra.web.security;

import de.hhu.propra.application.services.StudentService;
import de.hhu.propra.application.stereotypes.ApplicationService;
import de.hhu.propra.domain.aggregates.student.Student;
import de.hhu.propra.web.config.MethodSecurityConfiguration;
import de.hhu.propra.web.config.SecurityConfiguration;
import de.hhu.propra.web.config.WebSecurityConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


@SpringBootTest
@ComponentScan(basePackages = {"de.hhu.propra"}
        , includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = {ApplicationService.class}))
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class
SecurityTests {

    @Autowired
    MockMvc mockMvc;
    @MockBean
    StudentService studentService;

    @Test
    @DisplayName("Unangemeldet hat man keinen Zugriff")
    void test1() throws Exception {
        MockHttpSession session = AuthenticationTemplate.somebody();
        mockMvc.perform(get("/student").session(session))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/logs").session(session))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/tutor").session(session))
            .andExpect(status().isForbidden());
    }


    @Test
    @DisplayName("Student hat keinen Zugriff auf Logs und Tutorenseite")
    void test2() throws Exception {
        MockHttpSession session = AuthenticationTemplate.studentSession();
        when(studentService.studentMitHandle("AlexStudent")).thenReturn(new Student(1L, "AlexStudent"));
        mockMvc.perform(get("/student").session(session))
                .andExpect(status().isOk());
        mockMvc.perform(get("/logs").session(session))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/tutor").session(session))
            .andExpect(status().isForbidden());
    }


    @Test
    @DisplayName("Tutor hat kein Zugriff auf die Logs, aber auf die Tutorenseite")
    void test3() throws Exception {
        MockHttpSession session = AuthenticationTemplate.tutorSession();
        mockMvc.perform(get("/logs").session(session))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/tutor").session(session))
                .andExpect(status().isOk());
    }
}