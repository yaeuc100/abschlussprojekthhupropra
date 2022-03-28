package de.hhu.propra.web.controller;

import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class HomeController {

  @RequestMapping(value = "/", method = RequestMethod.GET)
  public String loginPage(HttpServletRequest httpServletRequest, Model model) {
    if (httpServletRequest.isUserInRole("ORGANISATOR")) {
      return "redirect://localhost:8080/logs";
    }
    if(httpServletRequest.isUserInRole("TUTOR")){
      return "redirect://localhost:8080/tutor";
    }
    return "redirect://localhost:8080/student";
  }
}
