package de.hhu.propra.web.controller;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;

@Controller
@Secured("ROLE_ORGANIZER")
public class OrganizerController {
}
