package de.hhu.propra.web.controller;

import de.hhu.propra.application.dto.KlausurDto;
import de.hhu.propra.application.dto.UrlaubDto;
import de.hhu.propra.application.services.KlausurService;
import de.hhu.propra.application.services.StudentService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Set;

@Secured("ROLE_STUDENT")
@Controller
public class StudentController {

  private final StudentService studentService;
  private final KlausurService klausurService;

  public StudentController(StudentService studentService, KlausurService klausurService) {
    this.studentService = studentService;
    this.klausurService = klausurService;
  }

  @GetMapping("/student")
  public String index(
      Model model,
      Principal principal,
      @RequestParam(name = "fehler", required = false) String fehler) {

    String handle = principal.getName();
    if (studentService.studentMitHandle(handle) == null) {
      studentService.createStudent(handle);
    }
    if (fehler != null && !fehler.isEmpty()) {
      model.addAttribute("fehler", fehler);
    }

    HashMap<Long, KlausurDto> klausuren =
        studentService.holealleklausurdtosmitid(studentService.studentMitHandle(handle));
    model.addAttribute("student", studentService.studentMitHandle(handle));
    model.addAttribute("klausuren", klausuren);
    return "studentSeite";
  }

  @GetMapping("student/klausuranmeldung")
  public String klausurAnmeldung(Model model) throws IOException {
    model.addAttribute("klausuren", klausurService.alleKlausuren());
    if (klausurService.alleKlausuren().isEmpty()) {
      model.addAttribute("keinKlausuren", true);
      return "klausuranmeldung";
    }
    return "klausuranmeldung";
  }

  @GetMapping("student/urlaubanmeldung")
  public String urlaubAnmeldung(Model model) {
    UrlaubDto urlaub = new UrlaubDto(null, null, null);
    model.addAttribute("urlaub", urlaub);
    return "urlaubanmeldung";
  }

  @GetMapping("student/klausurErstellen")
  public String klausurErstellen(Model model) {
    KlausurDto klausur = new KlausurDto(null, null, null, null, 1L, false);
    model.addAttribute("klausur", klausur);
    return "klausurErstellen";
  }

  @PostMapping("student/klausurErstellen")
  public String klausurErstellen(
      Model model,
      Principal principal,
      @Valid @ModelAttribute("klausur") KlausurDto klausurDto,
      BindingResult bindingResult)
      throws IOException {
    if (bindingResult.hasErrors()) {
      return "klausurErstellen";
    }

    Set<String> fehlermeldungen = studentService.klausurErstellen(principal.getName(), klausurDto);
    if (!fehlermeldungen.isEmpty()) {
      model.addAttribute("fehler", fehlermeldungen);
      return "klausurErstellen";
    }

    return "redirect://localhost:8080/student/klausuranmeldung";
  }

  @PostMapping("student/klausuranmeldung")
  public String klausurAnmelden(
      Model model, Principal principal, @ModelAttribute("klausur") Long klausurId) {
    Set<String> fehlermeldungen = studentService.klausurAnmelden(principal.getName(), klausurId);
    if (!fehlermeldungen.isEmpty()) {
      model.addAttribute("fehler", fehlermeldungen);
      model.addAttribute("klausuren", klausurService.alleKlausuren());
      return "klausuranmeldung";
    }
    return "redirect://localhost:8080/student";
  }

  @PostMapping("student/urlaubanmeldung")
  public String urlaubAnmelden(
      Model model,
      Principal principal,
      @Valid @ModelAttribute("urlaub") UrlaubDto urlaub,
      BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      return "urlaubanmeldung";
    }
    Set<String> fehlermeldungen = studentService.urlaubAnlegen(principal.getName(), urlaub);
    if (!fehlermeldungen.isEmpty()) {
      model.addAttribute("falscherUrlaub", fehlermeldungen);
      return "urlaubanmeldung";
    }
    return "redirect://localhost:8080/student";
  }

  @PostMapping("student/klausurstornieren")
  public String klausurStornieren(Principal principal, @ModelAttribute("referenz") Long klausurId) {
    studentService.klausurStornieren(principal.getName(), klausurService.klausurMitId(klausurId));
    return "redirect://localhost:8080/student";
  }

  @PostMapping("student/urlaubstornieren")
  public String urlaubStorniren(
      Model model,
      Principal principal,
      @ModelAttribute("datum") String datum,
      @ModelAttribute("startzeit") String startzeit,
      @ModelAttribute("endzeit") String endzeit) {

    Set<String> fehlermeldungen =
        studentService.urlaubStornieren(
            principal.getName(), new UrlaubDto(datum, startzeit, endzeit));
    if (!fehlermeldungen.isEmpty()) {
      String fehlerMeldung = fehlermeldungen.stream().toList().get(0);
      return "redirect://localhost:8080/student?fehler=" + fehlerMeldung;
    }
    return "redirect://localhost:8080/student";
  }
}
