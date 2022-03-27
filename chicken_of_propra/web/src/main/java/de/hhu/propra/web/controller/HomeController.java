package de.hhu.propra.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

@Controller
public class HomeController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String loginPage(HttpServletRequest httpServletRequest, Model model) {
        if (httpServletRequest.isUserInRole("TUTOR")) {
            return "redirect://localhost:8080/logs";
        } else if (httpServletRequest.isUserInRole("Student")) {
            return "redirect://localhost:8080/student";
        }
        return "";
    }
}
