package de.hhu.propra.web.controller;

import com.sun.security.auth.UserPrincipal;
import de.hhu.propra.application.dto.AuditDto;
import de.hhu.propra.application.services.AuditLogService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.mockito.Mockito.when;

@WebMvcTest(OrganisatorController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles({ "web", "test" })
public class OrganisatorControllerTests {

  @Autowired
  MockMvc mockMvc;

  @MockBean
  AuditLogService auditLogService;

  Principal principal = new UserPrincipal("organisator");

  @Test
  @DisplayName("Logs sind vollständig angezeigt")
  void test() throws Exception {
    List<AuditDto> logs = audits();
    logs.sort(Comparator.comparing(AuditDto::datum));
    when(auditLogService.alle()).thenReturn(audits());
    mockMvc.perform(get("/logs").principal(principal))
        .andExpect(model().attribute("logs", logs))
        .andExpect(status().isOk());
  }


  private List<AuditDto> audits() {
    List<AuditDto> list = new ArrayList<>();
    AuditDto auditDto = new AuditDto(LocalDateTime.of(2022, 10, 10, 10, 10), "x", "bla");
    AuditDto auditDto1 = new AuditDto(LocalDateTime.of(2023, 10, 10, 10, 10), "x", "blabla");
    AuditDto auditDto2 = new AuditDto(LocalDateTime.of(2024, 10, 10, 10, 10), "y", "babla");
    list.add(auditDto);
    list.add(auditDto1);
    list.add(auditDto2);
    return list;
  }
}
