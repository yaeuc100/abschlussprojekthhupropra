package de.hhu.propra.web.controller;

import de.hhu.propra.application.services.AuditLogService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Secured("ROLE_TUTOR")
public class TutorController {

    private final AuditLogService auditLogService;

    public TutorController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @GetMapping("logs")
    public String index(Model model){
        model.addAttribute("logs",auditLogService.alle());
        return "auditLog";
    }

}
