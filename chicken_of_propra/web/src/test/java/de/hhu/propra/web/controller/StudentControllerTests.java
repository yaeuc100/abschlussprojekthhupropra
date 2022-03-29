package de.hhu.propra.web.controller;


import com.sun.security.auth.UserPrincipal;
import de.hhu.propra.application.dto.KlausurDto;
import de.hhu.propra.application.dto.UrlaubDto;
import de.hhu.propra.application.fehler.KlausurFehler;
import de.hhu.propra.application.fehler.UrlaubFehler;
import de.hhu.propra.application.services.KlausurService;
import de.hhu.propra.application.services.StudentService;
import de.hhu.propra.domain.aggregates.klausur.Klausur;
import de.hhu.propra.domain.aggregates.student.KlausurReferenz;
import de.hhu.propra.domain.aggregates.student.Student;
import de.hhu.propra.domain.aggregates.student.Urlaub;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Stream;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(StudentController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles({ "web", "test" })
public class StudentControllerTests {

  @Autowired
  MockMvc mockMvc;

  @MockBean
  StudentService studentService;
  @MockBean
  KlausurService klausurService;

  Principal principal = new UserPrincipal("AlexStudent");

  @Test
  @DisplayName("Studentseite wird richtig angezeigt")
  void test1() throws Exception {
    Student student = new Student(1L, "AlexStudent");
    HashMap<Long, KlausurDto> map = new HashMap<>();
    map.put(1L, KlausurDto.toKlausurDto(klausuren().get(0)));
    student.addKlausurRef(new KlausurReferenz(1L));
    urlaube().forEach(u -> student.addUrlaub(u.datum(), u.startzeit(), u.endzeit()));

    when(klausurService.alleKlausuren()).thenReturn(klausuren());
    when(klausurService.klausurMitId(1L)).thenReturn(klausuren().get(0));
    when(studentService.studentMitHandle("AlexStudent")).thenReturn(student);
    when(studentService.holeAlleKlausurdtosMitId(student)).thenReturn(map);

    mockMvc.perform(get("/student").principal(principal))
        .andExpect(status().isOk())
        .andExpect(model().attribute("student", studentService.studentMitHandle("AlexStudent")))
        .andExpect(model().attribute("klausuren", map))
        .andExpect(model().attributeDoesNotExist("fehler"));
  }

  @Test
  @DisplayName("Klausuranmeldung Seite zeigt alle verfügbare Klausuren")
  void test2() throws Exception {
    when(klausurService.alleKlausuren()).thenReturn(klausuren());
    mockMvc.perform(get("/student/klausuranmeldung"))
        .andExpect(status().isOk())
        .andExpect(model().attribute("klausuren", klausuren()));
  }

  @Test
  @DisplayName("Urlaubanmeldung Seite richtig angezeigt")
  void test3() throws Exception {
    when(klausurService.alleKlausuren()).thenReturn(klausuren());
    mockMvc.perform(get("/student/urlaubanmeldung"))
        .andExpect(status().isOk())
        .andExpect(model().attributeExists("urlaub"));
  }

  @Test
  @DisplayName("Klausurerstellung Seite richtig angezeigt")
  void test4() throws Exception {
    when(klausurService.alleKlausuren()).thenReturn(klausuren());
    mockMvc.perform(get("/student/klausurErstellen"))
        .andExpect(status().isOk())
        .andExpect(model().attributeExists("klausur"));
  }

  @Test
  @DisplayName("Klausur wird erstellt")
  void test5() throws Exception {
    KlausurDto neuerKlausur = new KlausurDto("Einführung in die Computerlinguistik",
        LocalDate.of(2022, 3, 25).toString(),
        LocalTime.of(10, 0).toString(),
        LocalTime.of(10, 30).toString(),
        222916,
        false
    );
    when(klausurService.alleKlausuren()).thenReturn(klausuren());
    mockMvc.perform(post("/student/klausurErstellen")
        .principal(principal)
        .flashAttr("klausur", neuerKlausur))
        .andExpect(status().is3xxRedirection());
  }

  @Test
  @DisplayName("Klausur wird nicht erstellt und fehler wird angezeigt")
  void test6() throws Exception {
    KlausurDto neuerKlausur = new KlausurDto("Einführung in die Computerlinguistik",
        LocalDate.of(2022, 3, 25).toString(),
        LocalTime.of(10, 10).toString(),
        LocalTime.of(10, 00).toString(),
        222916,
        false
    );
    Set<String> fehler = new HashSet<>();
    fehler.add(KlausurFehler.STARTZEIT_VOR_ENDZEIT);
    when(klausurService.alleKlausuren()).thenReturn(klausuren());
    when(studentService.klausurErstellen("AlexStudent", neuerKlausur)).thenReturn(fehler);
    mockMvc.perform(post("/student/klausurErstellen")
        .principal(principal)
        .flashAttr("klausur", neuerKlausur))
        .andExpect(status().isOk())
        .andExpect(model().attribute("fehler", fehler));
  }

  @Test
  @DisplayName("Student kann sich nicht im Klausur anmelden und fehler wird angezeigt")
  void test7() throws Exception {

    Set<String> fehler = new HashSet<>();
    fehler.add(KlausurFehler.NEUE_KLAUSUR_SCHNEIDET_ALTE);

    when(studentService.klausurAnmelden("AlexStudent", 1L)).thenReturn(fehler);
    mockMvc.perform(post("/student/klausuranmeldung")
        .principal(principal)
        .flashAttr("klausur", 1L))
        .andExpect(status().isOk())
        .andExpect(model().attribute("fehler", fehler));
  }

  @Test
  @DisplayName("Student kann sich im Klausur anmelden und wird weitegeleitet")
  void test8() throws Exception {

    mockMvc.perform(post("/student/klausuranmeldung")
        .principal(principal)
        .flashAttr("klausur", 1L))
        .andExpect(status().is3xxRedirection());
  }

  @Test
  @DisplayName("Student kann nicht ein Urlaub beantragen und fehler wird angezeigt")
  void test9() throws Exception {

    UrlaubDto urlaubDto = new UrlaubDto(LocalDate.now().toString(),
        LocalTime.now().toString(),
        LocalTime.now().toString());

    Set<String> fehler = new HashSet<>();
    fehler.add(UrlaubFehler.AM_WOCHENENDE);

    when(studentService.urlaubAnlegen("AlexStudent", urlaubDto)).thenReturn(fehler);
    mockMvc.perform(post("/student/urlaubanmeldung")
        .principal(principal)
        .flashAttr("urlaub", urlaubDto))
        .andExpect(status().isOk())
        .andExpect(model().attribute("falscherUrlaub", fehler));
  }

  @Test
  @DisplayName("Student kann ein Urlaub anlegen und wird weitegeleitet")
  void test10() throws Exception {

    UrlaubDto urlaubDto = new UrlaubDto(LocalDate.now().toString(),
        LocalTime.now().toString(),
        LocalTime.now().toString());
    mockMvc.perform(post("/student/urlaubanmeldung")
        .principal(principal)
        .flashAttr("urlaub", urlaubDto))
        .andExpect(status().is3xxRedirection());
  }

  @Test
  @DisplayName("Student kann ein Klausur stornieren und wird weitegeleitet")
  void test11() throws Exception {
    mockMvc.perform(post("/student/klausurstornieren")
        .principal(principal)
        .flashAttr("referenz", 1L))
        .andExpect(status().is3xxRedirection());
  }

  @Test
  @DisplayName("Student kann nicht ein Urlaub stornieren und fehler wird angezeigt")
  void test12() throws Exception {
    UrlaubDto urlaubDto = new UrlaubDto(LocalDate.of(2022, 10, 10).toString(),
        LocalTime.of(10, 0).toString(),
        LocalTime.of(11, 0).toString());
    Set<String> fehler = new HashSet<>();
    fehler.add(UrlaubFehler.ANTRAG_RECHTZEITIG);
    when(studentService.urlaubStornieren("AlexStudent", urlaubDto)).thenReturn(fehler);

    mockMvc.perform(post("/student/urlaubstornieren")
        .principal(principal)
        .flashAttr("datum", "2022-10-10")
        .flashAttr("startzeit", "10:00")
        .flashAttr("endzeit", "11:00"))
        .andExpect(status().is3xxRedirection())
        .andExpect(
            redirectedUrl("//localhost:8080/student?fehler=" + fehler.stream().toList().get(0)));
  }

  @Test
  @DisplayName("Student kann ein Urlaub stornieren und wird weitergeleitet")
  void test13() throws Exception {
    mockMvc.perform(post("/student/urlaubstornieren")
        .principal(principal)
        .flashAttr("datum", "2022-10-10")
        .flashAttr("startzeit", "10:00")
        .flashAttr("endzeit", "11:00"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("//localhost:8080/student"));
  }

  private List<Klausur> klausuren() {
    Klausur klausur = new Klausur(1L, "a",
        LocalDateTime.of(2022, 3, 19, 10, 30),
        90,
        222916,
        true);

    Klausur zweiteKlausur = new Klausur(2L, "z",
        LocalDateTime.of(2022, 3, 19, 10, 30),
        90,
        222916,
        true);
    return List.of(klausur, zweiteKlausur);
  }

  private Stream<Urlaub> urlaube() {
    Urlaub urlaub = new Urlaub(LocalDate.now(),
        LocalTime.now(),
        LocalTime.now());
    Urlaub urlaub1 = new Urlaub(LocalDate.now().plusDays(1),
        LocalTime.now(),
        LocalTime.now());
    return Stream.of(urlaub, urlaub1);
  }


}
