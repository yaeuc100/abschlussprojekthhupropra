package de.hhu.propra.web.controller;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Secured("ROLE_TUTOR")
public class TutorController {

  @GetMapping("/tutor")
  public String tutor() {
    return "tutor";
  }


}
