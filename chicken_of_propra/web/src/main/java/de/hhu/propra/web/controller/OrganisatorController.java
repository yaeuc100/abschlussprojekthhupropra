package de.hhu.propra.web.controller;

import de.hhu.propra.application.dto.AuditDto;
import de.hhu.propra.application.services.AuditLogService;
import java.util.Comparator;
import java.util.List;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Secured("ROLE_ORGANISATOR")
public class OrganisatorController {

  private final AuditLogService auditLogService;

  public OrganisatorController(AuditLogService auditLogService) {
    this.auditLogService = auditLogService;
  }

  @GetMapping("logs")
  public String index(Model model) {

    List<AuditDto> logs = auditLogService.alle();
    logs.sort(Comparator.comparing(AuditDto::datum));
    model.addAttribute("logs", logs);
    return "auditLog";
  }
}
