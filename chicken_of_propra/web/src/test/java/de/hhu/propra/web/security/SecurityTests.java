package de.hhu.propra.web.security;

import de.hhu.propra.application.stereotypes.ApplicationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class SecurityTests {

    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("Unangemeldet hat keinen Zugriff")
    void test1() throws Exception {
        MockHttpSession session = AuthenticationTemplate.somebody();
        mockMvc.perform(get("/student").session(session))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/logs/").session(session))
                .andExpect(status().isForbidden());
    }
}