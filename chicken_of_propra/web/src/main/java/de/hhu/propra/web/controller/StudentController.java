package de.hhu.propra.web.controller;

import de.hhu.propra.application.dto.KlausurDto;
import de.hhu.propra.application.dto.UrlaubDto;
import de.hhu.propra.application.services.KlausurService;
import de.hhu.propra.application.services.StudentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

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

    @GetMapping("student/klausurAnmelden")
    public String klausurAnmeldung(Model model){
        model.addAttribute("klausuren", klausurService.alleKlausuren());
        return "klausurAnmelden";
    }

    @GetMapping("student/urlaubAnmelden")
    public String urlaubAnmeldung(Model model){
        UrlaubDto urlaub = new UrlaubDto(LocalDate.now().toString(), LocalTime.now().toString() , LocalTime.now().toString());
        model.addAttribute("urlaub", urlaub);
        return "urlaubAnmelden";
    }

    @GetMapping("student/klausurErstellen")
    public String klausurErstellen(Model model){

        return "klausurErstellen";
    }

    @PostMapping("student/klausurAnmelden")
    public String klausurAnmelden(KlausurDto klausurDto){
        //studentService.klausurAnmelden(principal.getHandle(), klausurDto);

        return "studentSeite";
    }

    @PostMapping("student/urlaubAnmelden")
    public String urlaubAnmelden(Model model, Principal principal, @ModelAttribute("urlaub") UrlaubDto urlaub){
        Set<String> fehlermeldungen = studentService.urlaubAnlegen(principal.getName(), urlaub);
        if(!fehlermeldungen.isEmpty()){
            model.addAttribute("falscherUrlaub", fehlermeldungen);
            System.out.println(fehlermeldungen);
            return "urlaubAnmelden";
        }
        return "redirect://localhost:8080/student";
    }

}
