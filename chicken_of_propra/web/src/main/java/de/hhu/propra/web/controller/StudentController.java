package de.hhu.propra.web.controller;

import de.hhu.propra.application.dto.KlausurDto;
import de.hhu.propra.application.dto.UrlaubDto;
import de.hhu.propra.application.services.KlausurService;
import de.hhu.propra.application.services.StudentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class StudentController {

    private final StudentService studentService;
    private final KlausurService klausurService;

    public StudentController(StudentService studentService, KlausurService klausurService) {
        this.studentService = studentService;
        this.klausurService = klausurService;
    }

    @GetMapping("student")
    public String index(Model model){
        //model.addAtribute("student", studentService.studentMitId(principal));
        return "index";
    }

    @GetMapping("student/klausurAnmelden")
    public String klausurAnmeldung(Model model){
        model.addAttribute("klausuren", klausurService.alleKlausuren());
        return "klausurAnmelden";
    }

    @GetMapping("student/urlaubAnmelden")
    public String urlaubAnmeldung(Model model){
        return "urlaubAnmelden";
    }

    @GetMapping("student/klausurErstellen")
    public String klausurErstellen(Model model){

        return "klausurErstellen";
    }

    @PostMapping("student/klausurAnmelden")
    public String klausurAnmelden(KlausurDto klausurDto){
        //studentService.klausurAnmelden(principal.getHandle(), klausurDto);

        return "index";
    }

    @PostMapping("student/urlaubAnmelden")
    public String urlaubAnmelden(Model model, UrlaubDto urlaubDto){
        /*boolean erfolgreich = studentService.urlaubAnlegen(principal, urlaubDto);
        if(!erfolgreich){
            List<String> urlaubsFehler = studentService.urlaubFehler(principal, urlaubDto);
            model.addAttribute("falscherUrlaub", urlaubsFehler);
            return "urlaubAnmelden";
        }

         */
        return "redi";
    }
}
