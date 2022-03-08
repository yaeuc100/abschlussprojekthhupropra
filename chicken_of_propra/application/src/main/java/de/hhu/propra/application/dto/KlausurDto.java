package de.hhu.propra.application.dto;

import java.time.LocalDateTime;

public record KlausurDto (String name , LocalDateTime datum , int dauer , long lsf){}
