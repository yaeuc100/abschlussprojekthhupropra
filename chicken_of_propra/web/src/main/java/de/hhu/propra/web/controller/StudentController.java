package de.hhu.propra.web.controller;

import de.hhu.propra.application.dto.KlausurDto;
import de.hhu.propra.application.dto.UrlaubDto;
import de.hhu.propra.application.services.KlausurService;
import de.hhu.propra.application.services.StudentService;
import de.hhu.propra.domain.aggregates.klausur.Klausur;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
    public String index(Model model , Principal principal){

        String handle = principal.getName();
        if(studentService.studentMitHandle(handle) == null){
            studentService.createStudent(handle);
        }

        System.out.println(studentService.studentMitHandle(handle).getUrlaube());
        model.addAttribute("student", studentService.studentMitHandle(handle));
        return "studentSeite";
    }

    @GetMapping("student/klausuranmeldung")
    public String klausurAnmeldung(Model model) throws IOException {
        studentService.klausurErstellen(new KlausurDto("Betriebssysteme und Systemprogrammierung",
                LocalDate.now().toString(),
                LocalTime.now().toString(),
                LocalTime.now().plusMinutes(90).toString(),
                217480,
                true));
        Klausur klausur = new Klausur(null,"", LocalDateTime.now(),0,0,false);
        model.addAttribute("klausuren", klausurService.alleKlausuren());
        model.addAttribute("klausur", klausur);
        return "klausuranmeldung";
    }

    @GetMapping("student/urlaubanmeldung")
    public String urlaubAnmeldung(Model model){
        UrlaubDto urlaub = new UrlaubDto(LocalDate.now().toString(), LocalTime.now().toString() , LocalTime.now().toString());
        model.addAttribute("urlaub", urlaub);
        return "urlaubanmeldung";
    }

    @GetMapping("student/klausurErstellen")
    public String klausurErstellen(Model model){

        return "klausurErstellen";
    }

    @PostMapping("student/klausuranmeldung")
    public String klausurAnmelden(Model model, Principal principal, @ModelAttribute("klausur") Klausur klausur){
        Set<String> fehlermeldungen = studentService.klausurAnmelden(principal.getName(), klausur.id());
        if(!fehlermeldungen.isEmpty()){
            model.addAttribute("fehler",fehlermeldungen);
            return "klausuranmeldung";
        }
        return "redirect://localhost:8080/student";
    }

    @PostMapping("student/urlaubanmeldung")
    public String urlaubAnmelden(Model model, Principal principal, @ModelAttribute("urlaub") UrlaubDto urlaub){
        Set<String> fehlermeldungen = studentService.urlaubAnlegen(principal.getName(), urlaub);
        if(!fehlermeldungen.isEmpty()){
            model.addAttribute("falscherUrlaub", fehlermeldungen);
            System.out.println(fehlermeldungen);
            return "urlaubanmeldung";
        }
        return "redirect://localhost:8080/student";
    }

}
