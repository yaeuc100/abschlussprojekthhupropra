package de.hhu.propra.database.entities;

import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;

public record AuditEntity(@Id Long id, String aenderung, String handle, LocalDateTime zeitpunkt) {

}
